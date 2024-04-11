package com.dalakoti07.android.uploads

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.dalakoti07.android.uploads.networks.RetrofitClient
import com.dalakoti07.android.uploads.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.io.FileInputStream
import kotlin.math.log

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    // Function to upload file using Retrofit
    private fun uploadFile(fileUri: Uri) {
        lifecycleScope.launch {
            RetrofitClient.uploadFile(this@MainActivity,fileUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                HomeScreen(
                    uploadFile = ::uploadFile,
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    uploadFile: (Uri)-> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit, block = {
        coroutineScope.launch {
            /*val response = RetrofitClient.performConcurrentApiCallsAndCalculateAverage(
                n = 100,
            )
            Log.d(TAG, "total average -> $response")*/
        }
    })
    val pickFileLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "HomeScreen -> uri: $uri")
            uri?.let { uploadFile(it) }
        }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(
            onClick = {
                pickFileLauncher.launch("text/plain")
            },
        ) {
            Text(text = "Select File")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        HomeScreen()
    }
}