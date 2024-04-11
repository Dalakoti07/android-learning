package com.dalakoti07.android.uploads.networks

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import android.os.Handler
import android.os.Looper

interface UploadCallbacks {
    fun onProgressUpdate(percentage: Int)
    fun onError()
    fun onFinish()
}

class ProgressRequestBody(
    private val file: File,
    private val contentType: MediaType?,
    private val callbacks: UploadCallbacks
) : RequestBody() {

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val inputStream = FileInputStream(file)
        var uploaded: Long = 0

        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            inputStream.use { fileInputStream ->
                while (fileInputStream.read(buffer).also { read = it } != -1) {
                    handler.post(ProgressUpdater(uploaded, fileLength))
                    uploaded += read
                    sink.write(buffer, 0, read)
                }
            }
        } finally {
            inputStream.close()
        }
    }

    private var lastPercent = 0

    private inner class ProgressUpdater(
        private val uploaded: Long,
        private val fileLength: Long
    ) : Runnable {
        override fun run() {
            val currentPercent = ((100 * uploaded) / fileLength).toInt()
            if(lastPercent!=currentPercent){
                callbacks.onProgressUpdate(currentPercent)
            }
            lastPercent = currentPercent
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}
