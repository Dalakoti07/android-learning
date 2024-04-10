package com.dalakoti07.android.uploads.networks

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    private val apiService = getClient("https://cb29-103-87-59-2.ngrok-free.app/").create(ApiService::class.java)

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
