package com.smartwheelchair.app.data.bluetooth

import com.smartwheelchair.app.domain.BtDevice
import com.smartwheelchair.app.domain.Command
import com.smartwheelchair.app.domain.ConnectionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeBluetoothRepository : BluetoothRepository {
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _devices = MutableStateFlow<List<BtDevice>>(
        listOf(
            BtDevice("HC-05-Wheelchair", "00:11:22:33:44:55", true),
            BtDevice("Speaker-BT", "AA:BB:CC:DD:EE:FF", true),
            BtDevice("HC-05-Demo", "11:22:33:44:55:66", false)
        )
    )
    override val devices: StateFlow<List<BtDevice>> = _devices.asStateFlow()

    override suspend fun startScan() {
        _connectionState.value = ConnectionState.Scanning
        delay(100)
        _connectionState.value = ConnectionState.Disconnected
    }

    override fun stopScan() {
        if (_connectionState.value is ConnectionState.Scanning) {
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    override suspend fun connect(device: BtDevice) {
        _connectionState.value = ConnectionState.Connecting(device.name)
        delay(100)
        _connectionState.value = ConnectionState.Connected(device.name)
    }

    override fun disconnect() {
        _connectionState.value = ConnectionState.Disconnected
    }

    override suspend fun send(command: Command): Result<Unit> {
        return if (_connectionState.value is ConnectionState.Connected) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Not connected"))
        }
    }
}
