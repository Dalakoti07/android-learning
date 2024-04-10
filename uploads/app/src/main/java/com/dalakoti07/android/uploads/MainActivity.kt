package com.dalakoti07.android.uploads

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dalakoti07.android.uploads.networks.RetrofitClient
import com.dalakoti07.android.uploads.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import kotlin.math.log

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(){
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit, block = {
        coroutineScope.launch {
            val response = RetrofitClient.performConcurrentApiCallsAndCalculateAverage(
                n = 100,
            )
            Log.d(TAG, "total average -> $response")
        }
    })
    Text(text = "Hello baby")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        HomeScreen()
    }
}