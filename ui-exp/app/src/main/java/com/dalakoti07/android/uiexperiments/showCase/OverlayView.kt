package com.dalakoti07.android.uiexperiments.showCase

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.View

class OverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#99000000") // Semi-transparent black
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // To clear the area
    }

    private var highlightRect: RectF? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the semi-transparent overlay

        // Draw a clear rectangle over the area to be highlighted
        highlightRect?.let {
            canvas.clipRect(it, Region.Op.DIFFERENCE)
            //canvas.drawRect(it, clearPaint)
        }
        canvas.drawPaint(overlayPaint)
    }

    fun setHighlightArea(rect: RectF) {
        highlightRect = rect
        invalidate() // Redraw the view with the updated highlight area
    }
}
