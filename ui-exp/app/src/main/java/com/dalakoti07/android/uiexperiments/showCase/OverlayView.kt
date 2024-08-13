package com.dalakoti07.android.uiexperiments.showCase

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
        // Create a Path that includes rounded corners
        val clipPath = Path().apply {
            if (highlightRect != null) {
                addRoundRect(
                    RectF(highlightRect),
                    30f, // Horizontal radius for corners
                    30f, // Vertical radius for corners
                    Path.Direction.CW // Path direction
                )
            }
        }

        // Save the canvas state before clipping
        canvas.save()

        // Clip the path from the canvas
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            canvas.clipOutPath(clipPath) // Use clipOutPath for API 26 and above
        } else {
            @Suppress("DEPRECATION")
            canvas.clipPath(clipPath, Region.Op.DIFFERENCE) // Use deprecated method for older versions
        }

        // Draw the semi-transparent overlay
        canvas.drawPaint(overlayPaint)

        // Restore the canvas to remove clipping effect outside this method
        canvas.restore()
    }

    fun setHighlightArea(rect: RectF) {
        highlightRect = rect
        invalidate() // Redraw the view with the updated highlight area
    }
}
