package com.dalakoti.poc.rendering.customViews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup

class FlowChipLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    companion object {
        private const val TAG = "FlowChipLayout"
    }

    var maxChipsPerRow: Int = Int.MAX_VALUE
        set(value) {
            field = value
            Log.d(TAG, "maxChipsPerRow=$value -> requestLayout()")
            requestLayout()
        }

    private val hSpacingDp = 8f
    private val vSpacingDp = 8f

    private val hSpacingPx get() = (hSpacingDp * resources.displayMetrics.density).toInt()
    private val vSpacingPx get() = (vSpacingDp * resources.displayMetrics.density).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        Log.d(TAG, "onMeasure availableWidth=$availableWidth childCount=$childCount")

        for (i in 0 until childCount) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
        }

        var currentX = paddingLeft
        var currentY = paddingTop
        var rowHeight = 0
        var chipsInRow = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val needsNewRow = (currentX + childWidth + paddingRight > availableWidth)
                    || (chipsInRow >= maxChipsPerRow)

            if (needsNewRow && chipsInRow > 0) {
                currentY += rowHeight + vSpacingPx
                currentX = paddingLeft
                rowHeight = 0
                chipsInRow = 0
            }

            rowHeight = maxOf(rowHeight, childHeight)
            currentX += childWidth + hSpacingPx
            chipsInRow++
        }

        val totalHeight = currentY + rowHeight + paddingBottom
        Log.d(TAG, "onMeasure totalHeight=$totalHeight")
        setMeasuredDimension(availableWidth, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d(TAG, "onLayout changed=$changed")
        val availableWidth = r - l

        var currentX = paddingLeft
        var currentY = paddingTop
        var rowHeight = 0
        var chipsInRow = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val needsNewRow = (currentX + childWidth + paddingRight > availableWidth)
                    || (chipsInRow >= maxChipsPerRow)

            if (needsNewRow && chipsInRow > 0) {
                currentY += rowHeight + vSpacingPx
                currentX = paddingLeft
                rowHeight = 0
                chipsInRow = 0
            }

            child.layout(currentX, currentY, currentX + childWidth, currentY + childHeight)
            rowHeight = maxOf(rowHeight, childHeight)
            currentX += childWidth + hSpacingPx
            chipsInRow++
        }
    }

    override fun requestLayout() {
        Log.d(TAG, "requestLayout")
        super.requestLayout()
    }

    override fun invalidate() {
        Log.d(TAG, "invalidate")
        super.invalidate()
    }
}
