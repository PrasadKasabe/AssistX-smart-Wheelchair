package com.smartwheelchair.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CommandMapperTest {

    @Test
    fun testForwardSynonyms() {
        assertEquals(Command.FORWARD, CommandMapper.map("forward"))
        assertEquals(Command.FORWARD, CommandMapper.map("GO FORWARD"))
        assertEquals(Command.FORWARD, CommandMapper.map("move forward!"))
        assertEquals(Command.FORWARD, CommandMapper.map("ahead"))
        assertEquals(Command.FORWARD, CommandMapper.map("straight"))
        assertEquals(Command.FORWARD, CommandMapper.map("front"))
        assertEquals(Command.FORWARD, CommandMapper.map("go"))
    }

    @Test
    fun testBackwardSynonyms() {
        assertEquals(Command.BACKWARD, CommandMapper.map("backward"))
        assertEquals(Command.BACKWARD, CommandMapper.map("backwards"))
        assertEquals(Command.BACKWARD, CommandMapper.map("GO BACK"))
        assertEquals(Command.BACKWARD, CommandMapper.map("move back."))
        assertEquals(Command.BACKWARD, CommandMapper.map("reverse"))
    }

    @Test
    fun testLeftSynonyms() {
        assertEquals(Command.LEFT, CommandMapper.map("left"))
        assertEquals(Command.LEFT, CommandMapper.map("TURN LEFT!"))
        assertEquals(Command.LEFT, CommandMapper.map("go left"))
    }

    @Test
    fun testRightSynonyms() {
        assertEquals(Command.RIGHT, CommandMapper.map("right"))
        assertEquals(Command.RIGHT, CommandMapper.map("turn right."))
        assertEquals(Command.RIGHT, CommandMapper.map("GO RIGHT"))
    }

    @Test
    fun testStopSynonymsAndPriority() {
        assertEquals(Command.STOP, CommandMapper.map("stop"))
        assertEquals(Command.STOP, CommandMapper.map("HALT!"))
        assertEquals(Command.STOP, CommandMapper.map("brake"))
        assertEquals(Command.STOP, CommandMapper.map("wait"))
        assertEquals(Command.STOP, CommandMapper.map("pause"))
        assertEquals(Command.STOP, CommandMapper.map("emergency"))
        // Safety rule: stop takes precedence even if forward is present
        assertEquals(Command.STOP, CommandMapper.map("go forward stop"))
        assertEquals(Command.STOP, CommandMapper.map("stop moving forward"))
    }

    @Test
    fun testConflictingDirectionsReturnNull() {
        assertNull(CommandMapper.map("go forward and left"))
        assertNull(CommandMapper.map("left right"))
    }

    @Test
    fun testEmptyOrBlankReturnsNull() {
        assertNull(CommandMapper.map(""))
        assertNull(CommandMapper.map("   "))
        assertNull(CommandMapper.map(null))
    }

    @Test
    fun testMapCandidates() {
        val candidates = listOf("random gibberish", "turn left please", "stop")
        assertEquals(Command.LEFT, CommandMapper.mapCandidates(candidates))
    }
}
