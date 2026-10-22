package com.smartwheelchair.app.ui.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwheelchair.app.data.bluetooth.BluetoothRepository
import com.smartwheelchair.app.domain.BtDevice
import com.smartwheelchair.app.domain.ConnectionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConnectViewModel(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = bluetoothRepository.connectionState
    val devices: StateFlow<List<BtDevice>> = bluetoothRepository.devices

    fun startScan() {
        viewModelScope.launch {
            bluetoothRepository.startScan()
        }
    }

    fun stopScan() {
        bluetoothRepository.stopScan()
    }

    fun connect(device: BtDevice) {
        viewModelScope.launch {
            bluetoothRepository.connect(device)
        }
    }

    fun disconnect() {
        bluetoothRepository.disconnect()
    }
}
