package com.smartwheelchair.app.ui.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwheelchair.app.data.bluetooth.BluetoothRepository
import com.smartwheelchair.app.domain.Command
import com.smartwheelchair.app.domain.CommandLog
import com.smartwheelchair.app.domain.CommandMapper
import com.smartwheelchair.app.domain.CommandRecord
import com.smartwheelchair.app.domain.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VoiceUiState(
    val voiceState: SpeechController.VoiceState = SpeechController.VoiceState.IDLE,
    val recognizedText: String? = null,
    val matchedCommand: Command? = null,
    val isMatched: Boolean = false,
    val errorMessage: String? = null
)

class VoiceViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val commandLog: CommandLog
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = bluetoothRepository.connectionState
    val recentCommands: StateFlow<List<CommandRecord>> = commandLog.records

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    fun onPartialResult(text: String) {
        _uiState.update { it.copy(recognizedText = text) }
    }

    fun onFinalResult(candidates: List<String>) {
        val topCandidate = candidates.firstOrNull()
        val mappedCommand = CommandMapper.mapCandidates(candidates)

        _uiState.update {
            it.copy(
                recognizedText = topCandidate,
                matchedCommand = mappedCommand,
                isMatched = mappedCommand != null
            )
        }

        if (mappedCommand != null) {
            viewModelScope.launch {
                bluetoothRepository.send(mappedCommand)
                commandLog.add(mappedCommand, topCandidate)
            }
        }
    }

    fun onError(message: String) {
        _uiState.update { it.copy(errorMessage = message, voiceState = SpeechController.VoiceState.IDLE) }
    }

    fun onStateChanged(state: SpeechController.VoiceState) {
        _uiState.update { it.copy(voiceState = state) }
    }

    fun sendStop() {
        viewModelScope.launch {
            bluetoothRepository.send(Command.STOP)
            commandLog.add(Command.STOP, "Emergency Stop")
            _uiState.update {
                it.copy(
                    recognizedText = "stop",
                    matchedCommand = Command.STOP,
                    isMatched = true,
                    voiceState = SpeechController.VoiceState.IDLE
                )
            }
        }
    }

    fun disconnect() {
        bluetoothRepository.disconnect()
    }
}
