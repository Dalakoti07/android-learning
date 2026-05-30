package com.dalakoti.poc.rendering.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dalakoti.poc.rendering.compose.AudioVisualizerCompose
import com.dalakoti.poc.rendering.databinding.FragmentAudioVisualizerBinding

class AudioVisualizerFragment : Fragment() {

    private lateinit var binding: FragmentAudioVisualizerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioVisualizerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeVisualizer.setContent {
            AudioVisualizerCompose()
        }
    }
}
