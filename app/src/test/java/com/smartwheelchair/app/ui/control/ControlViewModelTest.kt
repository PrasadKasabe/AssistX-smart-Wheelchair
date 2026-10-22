package com.smartwheelchair.app.ui.control

import com.smartwheelchair.app.data.bluetooth.FakeBluetoothRepository
import com.smartwheelchair.app.domain.BtDevice
import com.smartwheelchair.app.domain.Command
import com.smartwheelchair.app.domain.CommandLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ControlViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeBluetoothRepository
    private lateinit var commandLog: CommandLog
    private lateinit var viewModel: ControlViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeBluetoothRepository()
        commandLog = CommandLog()
        viewModel = ControlViewModel(fakeRepository, commandLog)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSendCommandUpdatesLogWhenConnected() = runTest {
        val device = BtDevice("HC-05", "00:11:22:33:44:55", true)
        fakeRepository.connect(device)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendCommand(Command.FORWARD)
        testDispatcher.scheduler.advanceUntilIdle()

        val recent = viewModel.recentCommands.value
        assertEquals(1, recent.size)
        assertEquals(Command.FORWARD, recent[0].command)
    }

    @Test
    fun testStopCommandIsNeverDropped() = runTest {
        val device = BtDevice("HC-05", "00:11:22:33:44:55", true)
        fakeRepository.connect(device)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendCommand(Command.STOP)
        testDispatcher.scheduler.advanceUntilIdle()

        val recent = viewModel.recentCommands.value
        assertEquals(Command.STOP, recent[0].command)
    }
}
