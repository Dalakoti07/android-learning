package com.dalakoti07.android.uploads.networks.optimise

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.PriorityQueue
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.log

private const val TAG = "RequestQueryManager"

class RequestQueueManager {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val priorityQueue = PriorityQueue<NetworkRequest<*>>(compareByDescending { it.priority })
    private var currentJob: Job? = null
    private val mutex = Mutex()

    private lateinit var currentProcessedRequest: NetworkRequest<*>

    fun <T> addRequest(request: NetworkRequest<T>): Deferred<T> {
        val deferred = CompletableDeferred<T>()
        coroutineScope.launch {
            mutex.withLock {
                // Add request to the queue
                request.deferred = deferred
                priorityQueue.add(request)
                // Check if we should preempt the current job
                if (shouldPreempt(request.priority)) {
                    currentJob?.cancel()  // Cancel current job if it is of lower priority
                }
                processQueue()  // Process the queue
            }
        }
        return deferred
    }

    private fun shouldPreempt(newTaskPriority: Int): Boolean {
        if(!::currentProcessedRequest.isInitialized){
            // means first request, hence no preemption
            return false
        }
        // Only preempt if the current task is running and the new task has a higher priority
        Log.d(TAG, "shouldPreempt: active -> ${currentJob?.isActive}")
        Log.d(TAG, "shouldPreempt: newTaskPriority -> $newTaskPriority")
        Log.d(TAG, "shouldPreempt: currentTaskPriority -> ${(currentProcessedRequest.priority)}")
        val should = currentJob?.isActive == true &&
                (currentProcessedRequest.priority) < newTaskPriority
        Log.d(TAG, "shouldPreempt: $should")
        return should
    }

    private fun processQueue() {
        coroutineScope.launch {
            if (currentJob == null || !currentJob!!.isActive) {
                while (priorityQueue.isNotEmpty()) {
                    val request = priorityQueue.peek() as NetworkRequest<Any>
                    currentProcessedRequest = request
                    currentJob = launch {
                        try {
                            Log.d(TAG, "processQueue executing ... ${request.priority}")
                            val result = request.operations.invoke()
                            (request.deferred as CompletableDeferred<Any>).complete(result)
                            priorityQueue.poll()
                        } catch (e: CancellationException) {
                            println("Task was cancelled: ${e.message}")
                            (request.deferred as CompletableDeferred<Any>).cancel(e)
                            priorityQueue.poll()
                        }
                    }
                    currentJob?.join()  // Wait for the current task to complete or be cancelled
                }
            }
        }
    }

}
