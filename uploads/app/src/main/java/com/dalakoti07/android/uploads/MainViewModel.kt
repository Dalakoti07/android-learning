package com.dalakoti07.android.uploads

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dalakoti07.android.uploads.networks.RetrofitClient
import com.dalakoti07.android.uploads.networks.optimise.OptimisedApiCalls
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MainScreenState(
    val apiTiming: List<Int> = emptyList(),
    val uploadProgress: Float = 0f,
    val uploadTotalTime: Int? = null,
    val averageApiCallTime: Int? = null,
)

private const val TAG = "MainViewModel"

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        MainScreenState()
    )
    val state: StateFlow<MainScreenState>
        get() = _state


    // Function to upload file using Retrofit
    fun uploadFile(fileUri: Uri) {
        viewModelScope.launch {
            val response = RetrofitClient.uploadFile(application, fileUri) { progress ->
                _state.update {
                    state.value.copy(
                        uploadProgress = progress.toFloat(),
                    )
                }
            }
            _state.update {
                state.value.copy(
                    uploadTotalTime = response
                )
            }
        }
    }

    fun startRestCalls() {
        viewModelScope.launch {
            val response = performConcurrentApiCallsAndCalculateAverage(
                n = 100,
            )
            _state.update {
                state.value.copy(
                    averageApiCallTime = response.toInt(),
                )
            }
            Log.d(TAG, "total average -> $response")
        }
    }

    private suspend fun performConcurrentApiCallsAndCalculateAverage(n: Int): Double = withContext(
        Dispatchers.IO
    ) {
        val timeTakenList = mutableListOf<Deferred<Int>>()

        for (i in 1..n) {
            val timeTakenDeferred = async { RetrofitClient.getDataFromServer() }
            timeTakenList.add(timeTakenDeferred)
        }

        val totalTimeValues = mutableListOf<Int>()
        val totalTimeTaken = timeTakenList.sumOf {
            val timeConsumed = it.await()
            totalTimeValues.add(timeConsumed)
            addValueToState(timeConsumed)
            timeConsumed
        }
        val averageTimeTaken = totalTimeTaken.toDouble() / n
        println("time take array -> $totalTimeValues")

        averageTimeTaken
    }

    private fun addValueToState(timeConsumed: Int) {
        _state.update {
            state.value.copy(
                apiTiming = state.value.apiTiming + timeConsumed,
            )
        }
    }

}