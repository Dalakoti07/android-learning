package com.dalakoti07.android.uploads

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dalakoti07.android.uploads.networks.RetrofitClient
import com.dalakoti07.android.uploads.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.io.FileInputStream
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
fun HomeScreen() {
    val viewModel = viewModel(modelClass = MainViewModel::class.java)
    val state by viewModel.state.collectAsState()

    MainScreenContent(
        uploadFile = {
            viewModel.uploadFile(it)
        },
        startRestCalls = {
            viewModel.startRestCalls()
        },
        state = state,
    )
}

@Composable
fun MainScreenContent(
    uploadFile: (Uri) -> Unit = {},
    startRestCalls: () -> Unit = {},
    state: MainScreenState,
) {
    val pickFileLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "HomeScreen -> uri: $uri")
            uri?.let { uploadFile(it) }
        }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Button(
                onClick = {
                    startRestCalls()
                },
            ) {
                Text(text = "Make API calls")
            }
        }
        item {
            Button(
                onClick = {
                    pickFileLauncher.launch("text/plain")
                },
            ) {
                Text(text = "Select File")
            }
        }
        items(state.apiTiming.size) {
            val entry = state.apiTiming[it]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 10.dp,
                        start = 10.dp,
                        end = 10.dp,
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp),
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Time Taken By Api ${it + 1}",
                    modifier = Modifier
                        .padding(
                            8.dp,
                        ),
                )
                Text(
                    text = entry.toString(), modifier = Modifier.padding(
                        end = 10.dp,
                    )
                )
            }
        }
        item {
            Text(
                text = "File upload timings",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        top = 10.dp,
                    ),
            )
        }
        item {
            Text(
                text = "Uploaded ${state.uploadProgress.toInt()} %",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                    ),
            )
        }
        item {
            if (state.averageApiCallTime != null) {
                Text(
                    text = "Total average rest API call ${state.averageApiCallTime}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            top = 10.dp,
                        ),
                )
            }
        }
        item {
            if (state.uploadTotalTime != null) {
                Text(
                    text = "Total upload time ${state.uploadTotalTime}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            top = 10.dp,
                        ),
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreenContent(
            state = MainScreenState(
                apiTiming = listOf(205, 305, 503, 456, 233, 902),
                uploadProgress = 43f,
                uploadTotalTime = 21,
                averageApiCallTime = 500,
            )
        )
    }
}