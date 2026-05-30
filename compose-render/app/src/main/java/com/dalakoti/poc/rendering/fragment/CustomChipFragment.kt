package com.dalakoti.poc.rendering.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.dalakoti.poc.rendering.compose.Chip
import com.dalakoti.poc.rendering.compose.FlowChipLayout
import com.dalakoti.poc.rendering.compose.debugRendering
import com.dalakoti.poc.rendering.databinding.FragmentCustomChipBinding

class CustomChipFragment : Fragment() {

    private lateinit var binding: FragmentCustomChipBinding

    private val chipLabels = listOf(
        "Android", "Kotlin", "Compose", "XML Views", "Layout",
        "Material", "Design System", "UI", "UX", "Animation",
        "Rendering", "Canvas"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomChipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupXmlChips()
        setupComposeChips()
    }

    private fun setupXmlChips() {
        chipLabels.forEach { label ->
            binding.xmlChipLayout.addView(createChipView(label))
        }

        var isFlowMode = true
        binding.btnToggleXml.setOnClickListener {
            isFlowMode = !isFlowMode
            binding.xmlChipLayout.maxChipsPerRow = if (isFlowMode) Int.MAX_VALUE else 2
            binding.btnToggleXml.text = if (isFlowMode) "Switch to 2 per row" else "Switch to flow"
        }
    }

    private fun createChipView(text: String): TextView {
        val density = resources.displayMetrics.density
        return TextView(requireContext()).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(
                (16 * density).toInt(),
                (8 * density).toInt(),
                (16 * density).toInt(),
                (8 * density).toInt()
            )
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 100f * density
                setColor(Color.parseColor("#6200EE"))
            }
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setupComposeChips() {
        binding.composeChipView.setContent {
            var isFlowMode by remember { mutableStateOf(true) }

            Column(modifier = Modifier.padding(bottom = 16.dp)) {

                // StaticDebugLabel receives a constant String — its input never changes.
                // When isFlowMode toggles, the Column lambda re-executes but Compose sees
                // the same argument and skips recomposing/remeasuring this composable entirely.
                StaticDebugLabel("Compose chip layout — sibling, not affected by toggle")

                FlowChipLayout(
                    modifier = Modifier.fillMaxWidth(),
                    maxChipsPerRow = if (isFlowMode) Int.MAX_VALUE else 2,
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp,
                ) {
                    chipLabels.forEach { label ->
                        Chip(text = label)
                    }
                }

                // Button text changes on every toggle — expect Measure+Layout+Draw every time
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .debugRendering("ToggleButton"),
                    onClick = { isFlowMode = !isFlowMode }
                ) {
                    Text(if (isFlowMode) "Switch to 2 per row" else "Switch to flow")
                }
            }
        }
    }
}

// Extracted as a named composable so Compose can identify it as a stable call site.
// Constant String input → Compose skips recomposition when parent re-executes.
@Composable
private fun StaticDebugLabel(text: String) {
    Log.d("StaticDebugLabel", "Recomposition")
    Text(
        text = text,
        modifier = Modifier.debugRendering("StaticDebugLabel"),
    )
}
