package com.dalakoti.poc.rendering.customViews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout

class DebugLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "DebugLinearLayout"
    }

    override fun requestLayout() {
        val caller = Thread.currentThread().stackTrace
            .firstOrNull { it.className.contains("com.dalakoti.poc.rendering") }
        Log.d(TAG, "requestLayout ← called from ${caller?.className?.substringAfterLast('.')}::${caller?.methodName}")
        super.requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d(TAG, "onLayout changed=$changed")
        super.onLayout(changed, l, t, r, b)
    }
}
