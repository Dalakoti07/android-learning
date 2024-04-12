package com.dalakoti07.android.uploads.networks.optimise

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.PriorityQueue
import kotlin.coroutines.cancellation.CancellationException

class RequestQueueManager {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val priorityQueue = PriorityQueue<NetworkRequest<*>>(compareByDescending { it.priority })
    private var currentJob: Job? = null
    private val mutex = Mutex()

    // Function to add requests to the queue
    suspend fun <T> addRequest(request: NetworkRequest<T>): Deferred<T> {
        val deferred = coroutineScope.async {
            request.operations.invoke()
        }

        mutex.withLock {
            priorityQueue.offer(request)
            // Automatically handle the task preemption
            if (shouldPreemptCurrentTask(request.priority)) {
                preemptCurrentTask()
            }
        }
        processQueue()
        return deferred
    }

    // Processes the queue, taking care to only start a new job if the current job is completed
    private fun processQueue() {
        coroutineScope.launch {
            mutex.withLock {
                if (currentJob == null || currentJob!!.isCompleted) {
                    while (priorityQueue.isNotEmpty()) {
                        val request = priorityQueue.poll()
                        currentJob = launch {
                            try {
                                request.operations.invoke()
                            } catch (e: CancellationException) {
                                println("Task was cancelled: ${e.message}")
                            }
                        }
                        currentJob!!.join()
                    }
                }
            }
        }
    }

    // Check if the current running task should be preempted
    private fun shouldPreemptCurrentTask(newTaskPriority: Int): Boolean {
        return currentJob?.isActive == true && ((priorityQueue.peek()?.priority
            ?: 0) < newTaskPriority)
    }

    // Cancel the current task to allow higher priority task to proceed
    private fun preemptCurrentTask() {
        currentJob?.cancel()
        currentJob = null
    }

}
