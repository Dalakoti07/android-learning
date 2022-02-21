package com.dalakoti07.android.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dalakoti07.android.R
import kotlinx.android.synthetic.main.fragment_sample.*

class SampleFragment: Fragment(R.layout.fragment_sample) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composable_view_s1.setContent {
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