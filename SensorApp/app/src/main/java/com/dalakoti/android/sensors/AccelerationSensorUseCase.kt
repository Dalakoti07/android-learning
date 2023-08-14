package com.dalakoti.android.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

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

    private val debounceBetweenTwoReadings = 1_000L

    private fun emitOnlyWhenSignificantChange(current: XYZCoordinates): Boolean{
        if(previousReading==null){
            return true
        }
        var difference = 0f
        difference += kotlin.math.abs(current.x - previousReading!!.x)
        difference += kotlin.math.abs(current.y - previousReading!!.y)
        difference += kotlin.math.abs(current.z - previousReading!!.z)
        return difference>0.05
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
                            data
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