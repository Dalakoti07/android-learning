package com.dalakoti.poc.rendering.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.sin

/**
 * Custom View: draws an audio visualizer as vertical bars.
 *
 * Measure-pass proof point:
 *   This View lives inside a horizontal LinearLayout where each bar
 *   cell uses layout_weight. LinearLayout with layout_weight makes
 *   TWO measure passes per child:
 *     Pass 1 — UNSPECIFIED / WRAP: collect natural sizes.
 *     Pass 2 — distribute remaining space according to weight.
 *   That LinearLayout is itself inside a ConstraintLayout, which
 *   measures its children once — but LinearLayout triggers its own
 *   internal double-pass, so each AudioVisualizerView.onMeasure
 *   fires TWICE per layout cycle.
 *
 * Watch the logcat tag "AudioVisualizerView" to see onMeasure ×2.
 */
class AudioVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "AudioVisualizerView"
        private const val BAR_COUNT = 20
        private const val ANIM_INTERVAL_MS = 80L
    }

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6200EE")
    }

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#F3F3F3")
    }

    // Amplitudes in [0f, 1f]
    private val amplitudes = FloatArray(BAR_COUNT) { 0f }
    private var phase = 0.0

    private val animRunnable = object : Runnable {
        override fun run() {
            tick()
            postDelayed(this, ANIM_INTERVAL_MS)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(animRunnable)
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(animRunnable)
        super.onDetachedFromWindow()
    }

    private fun tick() {
        phase += 0.3
        for (i in 0 until BAR_COUNT) {
            amplitudes[i] = ((sin(phase + i * 0.5) + 1.0) / 2.0).toFloat()
        }
        // Only pixel content changes → invalidate() is sufficient.
        // requestLayout() is NOT called — bar heights are computed inside
        // onDraw from the already-known view size.
        Log.d(TAG, "tick → invalidate() (no layout pass needed)")
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val modeW = MeasureSpec.getMode(widthMeasureSpec)
        val modeH = MeasureSpec.getMode(heightMeasureSpec)
        Log.d(
            TAG,
            "onMeasure modeW=${modeStr(modeW)} modeH=${modeStr(modeH)}"
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, "onDraw")
        val w = width.toFloat()
        val h = height.toFloat()

        // Background
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        val barWidth = w / (BAR_COUNT * 2 - 1).toFloat()
        val gap = barWidth
        val maxBarHeight = h * 0.9f
        val baseline = h

        for (i in 0 until BAR_COUNT) {
            val left = i * (barWidth + gap)
            val barH = maxBarHeight * amplitudes[i]
            val top = baseline - barH
            canvas.drawRect(left, top, left + barWidth, baseline, barPaint)
        }
    }

    private fun modeStr(mode: Int) = when (mode) {
        MeasureSpec.EXACTLY -> "EXACTLY"
        MeasureSpec.AT_MOST -> "AT_MOST"
        else -> "UNSPECIFIED"
    }
}
