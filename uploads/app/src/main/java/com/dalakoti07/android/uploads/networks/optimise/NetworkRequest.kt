package com.dalakoti07.android.uploads.networks.optimise

import kotlinx.coroutines.CompletableDeferred

data class NetworkRequest<T>(
    // todo make, enum such that high number means high priority
    val priority: Int,
    val operations: suspend () -> T,
    var deferred: CompletableDeferred<T>? = null
)

enum class RequestType {
    API_CALL, FILE_UPLOAD
}
