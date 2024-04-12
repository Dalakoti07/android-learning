package com.dalakoti07.android.uploads.networks

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class ServerResponse(
    val message: String,
    val largeData: String,
)

data class RequestData(
    val message: String,
)

interface ApiService {

    @GET("/data")
    suspend fun fetchData(): ServerResponse

    @Multipart
    @POST("/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): ServerResponse

    @POST("/data")
    suspend fun postData(@Body data: RequestData): ServerResponse

}