package com.dalakoti.poc.rendering.compose

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

// Two chained modifiers (.layout{} then .drawWithContent{}) are TWO separate nodes at different
// positions in the modifier chain. Measure/Layout are intercepted outer, Draw is intercepted inner —
// they don't track the same composable's render pass atomically.
//
// Fix: a single Modifier.Node implementing both LayoutModifierNode + DrawModifierNode so all
// three phases are intercepted at the exact same position in the chain.

fun Modifier.debugRendering(tag: String): Modifier = this.then(DebugRenderingElement(tag))

private data class DebugRenderingElement(val tag: String) : ModifierNodeElement<DebugRenderingNode>() {
    override fun create() = DebugRenderingNode(tag)
    override fun update(node: DebugRenderingNode) {
        node.tag = tag
    }
}

private class DebugRenderingNode(var tag: String) :
    LayoutModifierNode, DrawModifierNode, Modifier.Node() {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        Log.d(tag, "Measure")
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            Log.d(tag, "Layout")
            placeable.place(0, 0)
        }
    }

    override fun ContentDrawScope.draw() {
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
