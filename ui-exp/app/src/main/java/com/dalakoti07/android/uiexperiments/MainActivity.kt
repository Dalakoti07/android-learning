package com.dalakoti07.android.uiexperiments

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.dalakoti07.android.uiexperiments.databinding.ActivityMainBinding
import com.dalakoti07.android.uiexperiments.databinding.TooltipViewBinding
import com.dalakoti07.android.uiexperiments.showCase.OverlayView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var overLayView: OverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setShowCase()
    }

    private fun setShowCase() {
        overLayView = OverlayView(
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
            showTooltip()
        }
    }

    private fun showTooltip() {
//        val tooltipView = LayoutInflater.from(this).inflate(R.layout.tooltip_view, null)
        val tooltipView = TooltipViewBinding.inflate(layoutInflater, null, false)
        val popupWindow = PopupWindow(
            tooltipView.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set up touchable and focusable if you need interaction
        popupWindow.isFocusable = true
        popupWindow.isTouchable = true

        // Calculate position
        val location = IntArray(2)
        val anchorView = binding.cardView
        location[0] = anchorView.left
        location[1] = anchorView.top
        // define X
        val x = location[0] + anchorView.width * .1 // + anchorView.width/2 + 200
        val y = location[1] + anchorView.height + 100

        // Show the PopupWindow
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x.toInt(), y)
        tooltipView.btnOk.setOnClickListener {
            popupWindow.dismiss()
            binding.mainLayout.removeView(
                overLayView
            )
        }
    }
}
