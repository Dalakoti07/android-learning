package com.dalakoti07.android.uploads.networks.optimise

import kotlinx.coroutines.Deferred

class OptimisedApiCalls {

    private val requestQueueManager = RequestQueueManager()

    suspend fun <T> doApiCall(
        call: suspend () -> T,
    ): Deferred<T> {
        val request = NetworkRequest(
            requestType = RequestType.API_CALL,
            priority = 100,
            operations = call,
        )
        return requestQueueManager.addRequest(request)
    }

    suspend fun<T> doFileUpload(
        call: suspend () -> T,
    ): Deferred<T> {
        val request = NetworkRequest(
            requestType = RequestType.FILE_UPLOAD,
            priority = 0,
            operations = call,
        )
        return requestQueueManager.addRequest(request)
    }

}