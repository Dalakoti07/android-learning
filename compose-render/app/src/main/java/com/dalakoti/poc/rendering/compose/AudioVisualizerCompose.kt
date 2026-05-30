package com.dalakoti.poc.rendering.compose

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

private const val TAG = "AudioVisualizerCompose"
private const val BAR_COUNT = 20

/**
 * Compose version of the audio visualizer.
 *
 * Single-pass proof point:
 *   The Canvas composable is measured exactly ONCE per frame — Compose's
 *   Layout phase runs once with a single Constraints object. No weight
 *   distribution, no second pass.
 *
 *   The [animatedPhase] state changes every frame → only Canvas recomposes;
 *   the parent column and sibling label are NOT recomposed (scoped recomposition).
 *
 * Watch logcat tag "AudioVisualizerCompose" to see:
 *   - Recomposition fires on every frame
 *   - "Measure" fires once per recomposition (not twice)
 *   - "Draw" fires once per frame
 */
@Composable
fun AudioVisualizerCompose(modifier: Modifier = Modifier) {

    Log.d(TAG, "Recomposition")

    val infiniteTransition = rememberInfiniteTransition(label = "viz")

    // A single float ticks 0→1 over 2 s, looping forever.
    // On each frame Compose reads this state, recomposes only this Canvas.
    val animatedPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .debugRendering(TAG)           // logs Measure / Layout / Draw
    ) {
        val w = size.width
        val h = size.height
        val barWidth = w / (BAR_COUNT * 2 - 1).toFloat()
        val gap = barWidth
        val maxBarHeight = h * 0.9f
        val baseline = h

        for (i in 0 until BAR_COUNT) {
            val amplitude = ((sin((animatedPhase + i * 0.5).toDouble()) + 1.0) / 2.0).toFloat()
            val barH = maxBarHeight * amplitude
            val left = i * (barWidth + gap)
            val top = baseline - barH
            drawRect(
                color = Color(0xFF6200EE),
                topLeft = Offset(left, top),
                size = Size(barWidth, barH)
            )
        }
    }
}
