package com.dalakoti07.android.uploads.networks.optimise

import kotlinx.coroutines.Deferred

class OptimisedApiCalls {

    private val requestQueueManager = RequestQueueManager()

    fun <T> doApiCall(
        call: suspend () -> T,
    ): Deferred<T> {
        val request = NetworkRequest(
            priority = 100,
            operations = call,
        )
        return requestQueueManager.addRequest(request)
    }

    fun<T> doFileUpload(
        call: suspend () -> T,
    ): Deferred<T> {
        val request = NetworkRequest(
            priority = 0,
            operations = call,
        )
        return requestQueueManager.addRequest(request)
    }

}