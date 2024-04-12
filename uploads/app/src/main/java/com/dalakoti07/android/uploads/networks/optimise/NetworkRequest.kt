package com.dalakoti07.android.uploads.networks.optimise

import kotlinx.coroutines.CompletableDeferred

data class NetworkRequest<T>(
    val requestType: RequestType,
    val priority: Int,
    val operations: suspend ()-> T,
    var deferred: CompletableDeferred<T>? = null
)

enum class RequestType {
    API_CALL, FILE_UPLOAD
}
