package com.dalakoti.poc.rendering

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dalakoti.poc.rendering.compose.CircularProgressBar
import com.dalakoti.poc.rendering.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
        lifecycleScope.launch {
            delay(2*1000)
            binding.circularProgress.progress = 90f
        }
    }
}