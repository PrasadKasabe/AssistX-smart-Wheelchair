package com.smartwheelchair.app.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Shared in-memory log of recent commands sent (max 5 items).
 */
class CommandLog {
    private val _records = MutableStateFlow<List<CommandRecord>>(emptyList())
    val records: StateFlow<List<CommandRecord>> = _records.asStateFlow()

    fun add(command: Command, recognizedText: String? = null) {
        val record = CommandRecord(
            command = command,
            recognizedText = recognizedText
        )
        _records.update { current ->
            listOf(record) + current.take(4) // Keep max 5
        }
    }

    fun clear() {
        _records.value = emptyList()
    }
}
