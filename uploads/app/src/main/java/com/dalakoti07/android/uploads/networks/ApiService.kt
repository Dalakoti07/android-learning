package com.dalakoti07.android.uploads.networks

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

data class ServerResponse(
    val message: String,
    val largeData: String,
)

interface ApiService {

    @GET("/data")
    suspend fun fetchData(): ServerResponse


}