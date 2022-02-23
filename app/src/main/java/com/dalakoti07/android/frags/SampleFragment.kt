package com.dalakoti07.android.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dalakoti07.android.R
import com.dalakoti07.android.databinding.FragmentSampleBinding

class SampleFragment: Fragment(R.layout.fragment_sample) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSampleBinding.bind(view)
        binding.composableViewS1.setContent {
            MaterialTheme{
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Hello")
                    Button(onClick = {
                        findNavController().navigate(
                            R.id.action_sampleFragment_to_sampleFragment2
                        )
                    }) {
                        Text(text = "Button")
                    }
                }
            }
        }
    }

}