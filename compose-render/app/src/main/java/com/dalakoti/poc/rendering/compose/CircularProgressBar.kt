package com.dalakoti.poc.rendering.compose

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// debugRendering modifier lives in DebugModifier.kt (same package)

@Composable
fun CircularProgressBar(
    progress: Float
) {

    Log.d(
        "CircularProgressCompose",
        "Recomposition"
    )

    Canvas(
        modifier = Modifier
            .size(200.dp)
            .debugRendering(
                "CircularProgressCompose"
            )
    ) {

        drawCircle(
            color = Color.LightGray,
            style = Stroke(12.dp.toPx())
        )

        drawArc(
            color = Color.Blue,
            startAngle = -90f,
            sweepAngle = progress * 3.6f,
            useCenter = false,
            style = Stroke(
                width = 12.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}
