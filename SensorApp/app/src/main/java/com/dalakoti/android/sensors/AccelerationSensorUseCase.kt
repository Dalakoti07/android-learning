package com.dalakoti.android.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs
import kotlin.math.sqrt

private const val TAG = "AccelerationSensorUseCa"

data class XYZCoordinates(
    val x: Float,
    val y: Float,
    val z: Float
)

class AccelerationSensorUseCase {
    private lateinit var sensorManager: SensorManager

    private var previousReading : XYZCoordinates? = null
    private var previousTimeStamp: Long = 0L

    private var previousAcceleration: Float = 0f

    private val debounceBetweenTwoReadings = 1_000L

    private fun emitOnlyWhenSignificantChange(current: XYZCoordinates): Boolean{
        if(previousReading==null){
            return true
        }
        val currentAcceleration = sqrt(current.x * current.x
                    + current.y * current.y
                + current.z * current.z
        )
        Log.d(TAG, "current accn: $currentAcceleration")
        val difference = abs(currentAcceleration - previousAcceleration)
        previousAcceleration = currentAcceleration
        return difference>0.0001
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
                val currentTime = System.currentTimeMillis()
                if(previousTimeStamp == 0L || currentTime-previousTimeStamp>=debounceBetweenTwoReadings){
                    val data = XYZCoordinates(
                        x = event.values[0],
                        y = event.values[1],
                        z = event.values[2],
                    )
                    Log.d(TAG, "onSensorChanged: $data")
                    if(emitOnlyWhenSignificantChange(
                            data
                        )){
                        trySend(
                            previousAcceleration
                        )
                    }
                    previousTimeStamp = currentTime
                    previousReading = data
                }
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