package com.dalakoti.poc.rendering.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val TAG = "ChipLayoutCompose"

@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFF6200EE), RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FlowChipLayout(
    modifier: Modifier = Modifier,
    maxChipsPerRow: Int = Int.MAX_VALUE,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Log.d(TAG, "Recomposition maxChipsPerRow=$maxChipsPerRow")

    Layout(
        content = content,
        modifier = modifier.debugRendering(TAG),
    ) { measurables, constraints ->

        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()

        data class RowItem(val placeable: Placeable, val x: Int, val y: Int)

        val placed = mutableListOf<RowItem>()
        var currentX = 0
        var currentY = 0
        var rowHeight = 0
        var chipsInRow = 0

        for (placeable in placeables) {
            val needsNewRow = (currentX + placeable.width > constraints.maxWidth)
                    || (chipsInRow >= maxChipsPerRow)

            if (needsNewRow && chipsInRow > 0) {
                currentY += rowHeight + vSpacingPx
                currentX = 0
                rowHeight = 0
                chipsInRow = 0
            }

            placed.add(RowItem(placeable, currentX, currentY))
            rowHeight = maxOf(rowHeight, placeable.height)
            currentX += placeable.width + hSpacingPx
            chipsInRow++
        }

        val totalHeight = currentY + rowHeight

        layout(constraints.maxWidth, totalHeight) {
            for (item in placed) {
                item.placeable.placeRelative(item.x, item.y)
            }
        }
    }
}
