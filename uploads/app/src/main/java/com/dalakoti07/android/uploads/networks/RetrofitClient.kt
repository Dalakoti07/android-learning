package com.dalakoti07.android.uploads.networks

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.dalakoti07.android.uploads.networks.optimise.OptimisedApiCalls
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import kotlin.math.log

private const val TAG = "RetrofitClient"

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Log basic details about the request
        println("Sending request to URL: ${request.url} with method: ${request.method}")

        // You can add more detailed logging here if needed

        return chain.proceed(request) // Proceed with the request
    }
}

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private val optimisedApiCalls = OptimisedApiCalls()

    private fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            // Create an OkHttpClient and attach the interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor())  // Add the custom logging interceptor
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    private val apiService = getClient("https://4097-103-87-59-2.ngrok-free.app/").create(ApiService::class.java)

    suspend fun getDataFromServer(): Int {
        val startTime = System.currentTimeMillis()
        return try {
            val responseBody = optimisedApiCalls.doApiCall {
                apiService.fetchData()
            }.await()

            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime

            //println("Response time: $responseTime ms")
            responseTime.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    suspend fun postDataToServer(): Int {
        val startTime = System.currentTimeMillis()
        return try {
            val largeString = "x".repeat(10240)  // 10KB of 'x'
            val requestData = RequestData(message = largeString)
            val responseBody = apiService.postData(
                requestData
            )
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
        onUpdates: (Int)-> Unit,
    ): Int{
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(fileUri, "r", null)
        val fileInputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val file = File(context.cacheDir, context.contentResolver.getFileName(fileUri))
        file.outputStream().use {
            fileInputStream.copyTo(it)
        }

        val requestBody = ProgressRequestBody(
            file,
            "text/plain".toMediaTypeOrNull(),
            object : UploadCallbacks{
                override fun onProgressUpdate(percentage: Int) {
                    Log.d(TAG, "onProgressUpdate $percentage")
                    onUpdates(percentage)
                }

                override fun onError() {
                    Log.d(TAG, "onError ")
                }

                override fun onFinish() {
                    Log.d(TAG, "onFinish ")
                }
            }
        )
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        parcelFileDescriptor?.close()
        return try {
            val startTime = System.currentTimeMillis()
            val call = optimisedApiCalls.doFileUpload{
                apiService.uploadFile(part)
            }.await()

            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime
            Log.d(TAG, "uploadFile success")
            responseTime.toInt()
        }catch (e: Exception){
            Log.d(TAG, "uploadFile failed ")
            0
        }
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
