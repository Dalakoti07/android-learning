package com.dalakoti.android.sensors

import android.hardware.Sensor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val deviceSensors: MutableList<Sensor> = mutableListOf()

    private val accelerationSensorUseCase = AccelerationSensorUseCase()

    private lateinit var textView: TextView
    private lateinit var tvLastTime: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

    }

    private fun timeStampToString(timestamp: Long,format:String? = "dd MMM yyyy, hh:mm a"): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp
        cal.timeZone = (TimeZone.getTimeZone("Asia/Calcutta"))
        val date = DateFormat.format(format, cal).toString()
        return date
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.tv_main)
        tvLastTime = findViewById(R.id.tvLastTime)

        lifecycleScope.launch {
            accelerationSensorUseCase.startSensing(
                context = this@MainActivity,
            )
                .collect {
                    Log.d(TAG, "data: $it")
                    tvLastTime.text = timeStampToString(System.currentTimeMillis())
                    textView.text = "x: ${it.x}, y: ${it.y}, z: ${it.z}"
                }
        }
    }

}