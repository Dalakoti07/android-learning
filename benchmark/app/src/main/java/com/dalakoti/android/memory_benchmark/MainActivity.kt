package com.dalakoti.android.memory_benchmark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.dalakoti.android.memory_benchmark.object_tasks.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            testObjects()

        }

    }

    private fun testObjects() {
        val allTasks = listOf(
            Task1, Task2, Task3, Task4, Task5, Task6, Task7, Task8, Task9, Task10, Task11, Task12, Task13, Task14, Task15, Task16, Task17,
            Task18, Task19, Task20, Task21, Task22, Task23, Task24, Task25, Task26, Task27, Task28, Task29, Task30, Task31, Task32, Task33,
            Task34, Task35, Task36, Task37, Task38, Task39, Task40, Task41, Task42, Task43, Task44, Task45, Task46, Task47, Task48, Task49, Task50, Task51, Task52, Task53, Task54, Task55, Task56, Task57, Task58, Task59, Task60, Task61, Task62, Task63, Task64, Task65, Task66, Task67, Task68, Task69, Task70, Task71, Task72, Task73, Task74, Task75, Task76, Task77, Task78, Task79, Task80, Task81, Task82, Task83, Task84, Task85, Task86, Task87, Task88, Task89, Task90, Task91, Task92, Task93, Task94, Task95, Task96, Task97, Task98, Task99, Task100, Task101, Task102, Task103, Task104, Task105, Task106, Task107, Task108, Task109, Task110, Task111, Task112, Task113, Task114, Task115, Task116, Task117, Task118, Task119, Task120, Task121, Task122, Task123, Task124, Task125, Task126, Task127, Task128, Task129, Task130, Task131, Task132, Task133, Task134, Task135, Task136, Task137, Task138, Task139, Task140, Task141, Task142, Task143, Task144, Task145, Task146, Task147, Task148, Task149, Task150, Task151, Task152, Task153, Task154, Task155, Task156, Task157, Task158, Task159, Task160, Task161, Task162, Task163, Task164, Task165, Task166, Task167, Task168, Task169, Task170, Task171, Task172, Task173, Task174, Task175, Task176, Task177, Task178, Task179, Task180, Task181, Task182, Task183, Task184, Task185, Task186, Task187, Task188, Task189, Task190, Task191, Task192, Task193, Task194, Task195, Task196, Task197, Task198, Task199, Task200
        )
        allTasks.forEach {
            val result = it.showDate()
            Log.d(TAG, "testObjects: $result")
        }
    }
}
