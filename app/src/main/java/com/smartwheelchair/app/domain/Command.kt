package com.smartwheelchair.app.domain

import java.util.UUID

/**
 * Represents wheelchair movement commands sent over Bluetooth.
 */
enum class Command(val byteCode: Char, val label: String) {
    FORWARD('F', "Forward"),
    BACKWARD('B', "Backward"),
    LEFT('L', "Left"),
    RIGHT('R', "Right"),
    STOP('S', "STOP")
}

/**
 * Bluetooth connection states.
 */
sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data object Scanning : ConnectionState
    data class Connecting(val deviceName: String) : ConnectionState
    data class Connected(val deviceName: String) : ConnectionState
    data class Error(val message: String) : ConnectionState
}

/**
 * Represents a discovered or paired Bluetooth device.
 */
data class BtDevice(
    val name: String,
    val address: String,
    val isBonded: Boolean
) {
    val isHc05: Boolean
        get() = name.uppercase().startsWith("HC-05") || name.uppercase().contains("HC-05")
}

/**
 * Record of a sent command for the history log.
 */
data class CommandRecord(
    val id: Long = System.currentTimeMillis(),
    val command: Command,
    val recognizedText: String?,
    val timestamp: Long = System.currentTimeMillis()
)

val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
