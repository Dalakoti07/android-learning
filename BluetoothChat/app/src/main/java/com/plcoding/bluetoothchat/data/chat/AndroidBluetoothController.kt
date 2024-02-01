package com.plcoding.bluetoothchat.data.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.ui.res.colorResource
import com.plcoding.bluetoothchat.domain.chat.BluetoothController
import com.plcoding.bluetoothchat.domain.chat.BluetoothDeviceDomain
import com.plcoding.bluetoothchat.domain.chat.ConnectionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.util.UUID

private const val TAG = "AndroidBluetoothControl"

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    private var currentServerSocket :BluetoothServerSocket? = null
    private var currentClientSocket :BluetoothSocket? = null

    private val _isConnected = MutableStateFlow(false)
    private val _errors = MutableSharedFlow<String>()

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if(newDevice in devices) devices else devices + newDevice
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver{ conn, device->
        if(bluetoothAdapter?.bondedDevices?.contains(device) == true){
            _isConnected.update {
                conn
            }
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                _errors.tryEmit("Can't connect to non-paired device")
            }
        }
    }

    override fun errors(): SharedFlow<String> {
        return _errors.asSharedFlow()
    }

    init {
        updatePairedDevices()
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(
                    BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED,
                )
                addAction(
                    BluetoothDevice.ACTION_ACL_CONNECTED,
                )
                addAction(
                    BluetoothDevice.ACTION_ACL_DISCONNECTED,
                )
            }
        )
    }

    override fun startDiscovery() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        Log.d(TAG, "startDiscovery: ")
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                throw SecurityException("No bluetooth_connect permission")
            }
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "chat_service",
                UUID.fromString(id),
            )
            var shouldLoop = true
            while (shouldLoop){
                currentClientSocket = try {
                    currentServerSocket?.accept()
                }catch (e: Exception){
                    shouldLoop = false
                    e.printStackTrace()
                    null
                }
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let {
                    // because JOB of currentServer is done, as we have client socket,
                    // and now this would be used for communication
                    currentServerSocket?.close()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                throw SecurityException("No bluetooth_connect permission")
            }
            val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)

            currentClientSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(
                UUID.fromString(id)
            )
            stopDiscovery()
            currentClientSocket?.let {socket->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)
                }catch (e: IOException){
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connect was interrupted"))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun isConnected(): Flow<Boolean> {
        return _isConnected
    }

    private fun updatePairedDevices() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        const val id = "7e1dc73c-4fb2-483f-8d0c-1b0a21cc8cbb"
    }
}