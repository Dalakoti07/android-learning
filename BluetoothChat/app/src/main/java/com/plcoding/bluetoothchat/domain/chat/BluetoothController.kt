package com.plcoding.bluetoothchat.domain.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun release()

    fun startBluetoothServer(): Flow<ConnectionResult>

    fun connectToDevice( device: BluetoothDeviceDomain): Flow<ConnectionResult>

    fun closeConnection()

    fun isConnected(): Flow<Boolean>

    fun errors(): SharedFlow<String>

    suspend fun trySendMessage(message: String): BluetoothMessage?

}