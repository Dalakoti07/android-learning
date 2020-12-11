package com.dalakoti07.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class BezierCurve(context: Context) : View(context) {

    // Paint object for coloring and styling
    private val paint = Paint()
    // Some colors for the face background, eyes and mouth.
    private var backGroundColor = Color.YELLOW

    //size of the the view
    private var viewHeight=100
    private var viewWidth=300

    private val curvePath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawCurve(canvas)
    }

    private fun drawCurve(canvas: Canvas) {
        // set paint and color
        paint.color = backGroundColor
        paint.style = Paint.Style.FILL

        curvePath.reset()
        curvePath.moveTo(0F,100F)

        //create bezier curve
        curvePath.quadTo(150F,0F ,300F ,100F )
        curvePath.close()

        canvas.drawPath(curvePath,paint)
    }

    /*override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 1
        val smaller = Math.min(measuredWidth, measuredHeight)
        val greater=Math.max(measuredWidth,measuredHeight)
        // 2
        setMeasuredDimension(size, size)
    }*/
}