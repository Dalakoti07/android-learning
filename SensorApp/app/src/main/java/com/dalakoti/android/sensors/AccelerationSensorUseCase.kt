package com.dalakoti.android.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Double.max

private const val TAG = "AccelerationSensorUseCa"

data class XYZAccelerations(
    val x: Float,
    val y: Float,
    val z: Float
)

class AccelerationSensorUseCase {
    private lateinit var sensorManager: SensorManager

    private var previousReading : XYZAccelerations? = null
    private var previousTimeStamp: Long = 0L
    private var previousSendTimeStamp: Long = 0L

    // in milli seconds, converted to nano seconds
    private val eventEmitTime = 500L * 1e6

    private var currentSpeed: Double = 0.0

    // speed array
    private val lastNSpeeds = mutableListOf<Double>()
    private val windowSize = 3

    private fun handleSensorData(current: XYZAccelerations, eventTimeStamp: Long,){
        if(previousReading==null){
            return
        }
        Log.d(TAG, "current acc: ${current.x} and speed: $currentSpeed")
        // convert time to seconds from nano seconds
        val speedChange = (current.x * ((previousTimeStamp - eventTimeStamp)* 1e-9))
        currentSpeed += speedChange
        currentSpeed = max(currentSpeed, 0.0)

        // update array
        if(lastNSpeeds.size == windowSize){
            lastNSpeeds.removeFirst()
            lastNSpeeds.add(currentSpeed)
        }else{
            lastNSpeeds.add(currentSpeed)
        }

        Log.d(TAG, "speed-change: $speedChange and speed: $currentSpeed ")
    }

    fun startSensing(context: Context) = callbackFlow {
        Log.d(TAG, "startSensing: thread is main? -> ")
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        val eventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if(event?.values?.size!=3){
                    return
                }
                val currentTime = event.timestamp
                val data = XYZAccelerations(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2],
                )
                handleSensorData(
                    data,
                    event.timestamp,
                )
                // Log.d(TAG, "previous: $previousSendTimeStamp and current: $currentTime ")
                if(previousSendTimeStamp == 0L || currentTime-previousSendTimeStamp>=eventEmitTime){
                    // Log.d(TAG, "onSensorChanged: $data")
                    trySend(
                        currentSpeed
                    )
                    previousSendTimeStamp = event.timestamp
                }
                previousTimeStamp = event.timestamp
                previousReading = data
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                val type = when(accuracy){
                    SensorManager.SENSOR_STATUS_NO_CONTACT->{
                        "SENSOR_STATUS_NO_CONTACT"
                    }
                    SensorManager.SENSOR_STATUS_ACCURACY_LOW->{
                        "SENSOR_STATUS_ACCURACY_LOW"
                    }
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH->{
                        "SENSOR_STATUS_ACCURACY_HIGH"
                    }
                    SensorManager.SENSOR_STATUS_UNRELIABLE->{
                        "SENSOR_STATUS_UNRELIABLE"
                    }
                    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM->{
                        "SENSOR_STATUS_ACCURACY_MEDIUM"
                    }
                    else-> "nothing"
                }
                Log.d(TAG, "onAccuracyChanged -> $type")
            }
        }

        sensorManager.registerListener(
            eventListener,
            sensor,
//            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_UI,
        )

        awaitClose {
            Log.d(TAG, "startSensing: closing flow callback")
            sensorManager.unregisterListener(eventListener)
        }
    }

}