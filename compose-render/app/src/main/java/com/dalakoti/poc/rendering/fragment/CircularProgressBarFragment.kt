package com.dalakoti.poc.rendering.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.dalakoti.poc.rendering.compose.CircularProgressBar
import com.dalakoti.poc.rendering.databinding.CircularProgressBarBinding

class CircularProgressBarFragment: Fragment() {

    private lateinit var binding: CircularProgressBarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CircularProgressBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            var progress by remember {
                mutableStateOf(75f)
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(
                    top = 20.dp,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressBar(
                    progress = progress,
                )
                Button(
                    modifier = Modifier.padding(
                        top = 10.dp,
                    ),
                    onClick = {
                        progress = 90f
                    }
                ) {
                    Text("Increase")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btnViewIncrease.setOnClickListener {
            binding.circularProgress.progress = 90f
        }
    }

}