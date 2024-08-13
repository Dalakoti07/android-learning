package com.dalakoti07.android.uiexperiments.showCase

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class OverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#99000000") // Semi-transparent black
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // To clear the area
    }

    private var highlightRect: Rect? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the semi-transparent overlay
        canvas.drawPaint(overlayPaint)

        // Draw a clear rectangle over the area to be highlighted
        highlightRect?.let {
            canvas.drawRect(it, clearPaint)
        }
    }

    fun setHighlightArea(rect: Rect) {
        highlightRect = rect
        invalidate() // Redraw the view with the updated highlight area
    }
}
