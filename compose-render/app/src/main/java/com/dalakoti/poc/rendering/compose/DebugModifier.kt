package com.dalakoti.poc.rendering.compose

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints

// Single modifier node intercepting all three phases at the same chain position.
// Use on any composable: Canvas, Text, Layout, Button, etc.
//
// Usage:  Modifier.debugRendering("MyTag")
//
// Logs:
//   MyTag: Measure   → composable is being measured
//   MyTag: Layout    → composable is being placed
//   MyTag: Draw      → composable is drawing

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
