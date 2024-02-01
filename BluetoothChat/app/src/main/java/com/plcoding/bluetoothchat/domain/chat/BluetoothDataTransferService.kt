package com.plcoding.bluetoothchat.domain.chat

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

class TransferException : IOException("reading message via Bluetooth failed")

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {

    fun listenForIncomingMessage(): Flow<BluetoothMessage> {
        return flow {
            if (socket.isConnected) {
                return@flow
            }
            val buffer = ByteArray(1024)
            while (true) {
                val byCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    throw TransferException()
                }
                emit(
                    buffer.decodeToString(
                        endIndex = byCount
                    ).toBluetoothMessage(isFromLocalUser = false)
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean{
        return withContext(Dispatchers.IO){
            try {
                socket.outputStream.write(bytes)
            }catch (e: Exception){
                e.printStackTrace()
                return@withContext false
            }
            true
        }
    }

}