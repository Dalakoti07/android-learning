package com.dalakoti.poc.rendering.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        private const val TAG = "CircularProgress"
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
    }

    var progress = 75f
        set(value) {
            field = value

            Log.d(TAG, "progress updated -> invalidate()")

            invalidate()
        }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        Log.d(TAG, "onMeasure")

        val size = 100

        setMeasuredDimension(
            resolveSize(size, widthMeasureSpec),
            resolveSize(size, heightMeasureSpec)
        )
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(
            changed,
            left,
            top,
            right,
            bottom
        )

        Log.d(
            TAG,
            "onLayout changed=$changed"
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d(TAG, "onDraw")

        val radius = width / 2f - 30

        canvas.drawCircle(
            width / 2f,
            height / 2f,
            radius,
            bgPaint
        )

        val sweep = progress * 3.6f

        canvas.drawArc(
            30f,
            30f,
            width - 30f,
            height - 30f,
            -90f,
            sweep,
            false,
            progressPaint
        )
    }

    override fun invalidate() {
        Log.d(TAG, "invalidate")
        super.invalidate()
    }

    override fun requestLayout() {
        Log.d(TAG, "requestLayout")
        super.requestLayout()
    }
}