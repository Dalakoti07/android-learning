package com.plcoding.bluetoothchat.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bluetoothchat.domain.chat.BluetoothController
import com.plcoding.bluetoothchat.domain.chat.BluetoothDeviceDomain
import com.plcoding.bluetoothchat.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BluetoothViewModel"

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private var deviceConnectionJob: Job? = null
    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            allMessages = if(state.isConnected) state.allMessages else emptyList(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        bluetoothController.isConnected().onEach { conn ->
            _state.update {
                it.copy(
                    isConnected = conn
                )
            }
        }.launchIn(viewModelScope)
        bluetoothController.errors().onEach { err ->
            _state.update {
                it.copy(
                    errorMessage = err,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null,
                        )
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message,
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded->{
                    _state.update {
                        it.copy(
                            allMessages = it.allMessages + result.message
                        )
                    }
                }
            }
        }.catch {
            Log.d(TAG, "listen: $it")
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isConnecting = false,
                    isConnected = false,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(deviceDomain: BluetoothDeviceDomain) {
        _state.update {
            it.copy(isConnecting = true)
        }
        deviceConnectionJob = bluetoothController.connectToDevice(
            deviceDomain
        ).listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
            )
        }
    }

    fun waitForIncomingConnection(){
        _state.update {
            it.copy(
                isConnecting = true
            )
        }
        deviceConnectionJob = bluetoothController.startBluetoothServer().listen()
    }

    fun sendMessage(message: String){
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message)
            if(bluetoothMessage!=null){
                _state.update {
                    it.copy(
                        allMessages = it.allMessages + bluetoothMessage
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }

}