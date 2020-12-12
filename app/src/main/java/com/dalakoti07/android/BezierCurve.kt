package com.dalakoti07.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View

class BezierCurve(context: Context, attrs: AttributeSet) : View(context, attrs){

    // Paint object for coloring and styling
    private val paint = Paint()
    // Some colors for the face background, eyes and mouth.
    private var backGroundColor = Color.YELLOW

    //size of the the view
    private var viewHeight=300F
    private var viewWidth=1100F
    private var extraHeight=200F

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
        curvePath.moveTo(0F,viewHeight)

        //create bezier curve
        curvePath.quadTo(viewWidth/2,0F ,viewWidth ,viewHeight )

        //create rectangle
        curvePath.lineTo(viewWidth,viewHeight+extraHeight)
        curvePath.lineTo(0F,viewHeight+extraHeight)

        curvePath.close()

        canvas.drawPath(curvePath,paint)
    }

    //width: height 's ratio should be 2:1
    //setMeasuredDimension(measuredWidth, measuredHeight/2)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val smaller = Math.min(measuredWidth, measuredHeight)
//        val greater=Math.max(widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth/2)
    }
}