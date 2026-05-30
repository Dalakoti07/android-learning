package com.dalakoti.poc.rendering.compose

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

fun Modifier.debugRendering(
    tag: String
): Modifier {

    return this
        .layout { measurable, constraints ->

            Log.d(tag, "Measure")

            val placeable =
                measurable.measure(constraints)

            layout(
                placeable.width,
                placeable.height
            ) {

                Log.d(tag, "Layout")

                placeable.place(0, 0)
            }
        }
        .drawWithContent {

            Log.d(tag, "Draw")

            drawContent()
        }
}

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
