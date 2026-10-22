package com.smartwheelchair.app.ui.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwheelchair.app.data.bluetooth.BluetoothRepository
import com.smartwheelchair.app.domain.Command
import com.smartwheelchair.app.domain.CommandLog
import com.smartwheelchair.app.domain.CommandRecord
import com.smartwheelchair.app.domain.ConnectionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ControlViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val commandLog: CommandLog
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = bluetoothRepository.connectionState
    val recentCommands: StateFlow<List<CommandRecord>> = commandLog.records

    fun sendCommand(command: Command) {
        viewModelScope.launch {
            val result = bluetoothRepository.send(command)
            if (result.isSuccess) {
                commandLog.add(command)
            }
        }
    }

    fun disconnect() {
        bluetoothRepository.disconnect()
    }
}
