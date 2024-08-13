package com.dalakoti07.android.uiexperiments

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
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
            location[0] = targetView.left
            location[1] = targetView.top
            val rect = RectF(
                location[0].toFloat(),
                location[1].toFloat(),
                location[0] + targetView.width.toFloat(),
                location[1] + targetView.height.toFloat(),
            )
            overLayView.setHighlightArea(rect)
        }
    }
}
