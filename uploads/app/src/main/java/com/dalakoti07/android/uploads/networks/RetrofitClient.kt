package com.dalakoti07.android.uploads.networks

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream

private const val TAG = "RetrofitClient"

object RetrofitClient {
    private var retrofit: Retrofit? = null

    private fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    private val apiService = getClient("https://004d-103-87-59-2.ngrok-free.app/").create(ApiService::class.java)

    private suspend fun getDataFromServer(): Int {
        val startTime = System.currentTimeMillis()
        return try {
            val responseBody = apiService.fetchData()
            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime

            //println("Response time: $responseTime ms")
            responseTime.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    suspend fun uploadFile(
        context: Context,
        fileUri: Uri,
    ){
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(fileUri, "r", null)
        val fileInputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val file = File(context.cacheDir, context.contentResolver.getFileName(fileUri))
        file.outputStream().use {
            fileInputStream.copyTo(it)
        }

        val requestBody = file.asRequestBody("text/plain".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        // Create Retrofit instance and start upload (not shown here, refer to Retrofit setup documentation)

        parcelFileDescriptor?.close()
        try {
            val call = apiService.uploadFile(part)

        }catch (e: Exception){
            Log.d(TAG, "uploadFile failed ")
        }
    }


    suspend fun performConcurrentApiCallsAndCalculateAverage(n: Int): Double = withContext(Dispatchers.IO) {
        val timeTakenList = mutableListOf<Deferred<Int>>()

        for (i in 1..n) {
            val timeTakenDeferred = async { getDataFromServer() }
            timeTakenList.add(timeTakenDeferred)
        }

        val totalTimeValues = mutableListOf<Int>()
        val totalTimeTaken = timeTakenList.sumOf {
            val timeConsumed = it.await()
            totalTimeValues.add(timeConsumed)
            timeConsumed
        }
        val averageTimeTaken = totalTimeTaken.toDouble() / n
        println("time take array -> $totalTimeValues")

        averageTimeTaken
    }

}

private fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val cursor = query(fileUri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            name = cursor.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        }
    }
    return name
}
