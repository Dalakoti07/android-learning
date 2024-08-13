package com.dalakoti07.android.uiexperiments

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.dalakoti07.android.uiexperiments.databinding.ActivityMainBinding
import com.dalakoti07.android.uiexperiments.showCase.OverlayView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setShowCase()
    }

    private fun setShowCase() {
        val overLayView = OverlayView(
            context = this,
        ).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        binding.mainLayout.addView(
            overLayView
        )
        val targetView = binding.cardView
        targetView.post {
            val location = IntArray(2)
            targetView.getLocationInWindow(location)
            val rect = Rect(
                location[0],
                location[1],
                location[0] + targetView.width,
                location[1] + targetView.height,
            )
            overLayView.setHighlightArea(rect)
        }
    }
}
