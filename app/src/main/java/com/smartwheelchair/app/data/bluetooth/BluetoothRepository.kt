package com.smartwheelchair.app.data.bluetooth

import com.smartwheelchair.app.domain.BtDevice
import com.smartwheelchair.app.domain.Command
import com.smartwheelchair.app.domain.ConnectionState
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {
    val connectionState: StateFlow<ConnectionState>
    val devices: StateFlow<List<BtDevice>>

    suspend fun startScan()
    fun stopScan()
    suspend fun connect(device: BtDevice)
    fun disconnect()
    suspend fun send(command: Command): Result<Unit>
}
