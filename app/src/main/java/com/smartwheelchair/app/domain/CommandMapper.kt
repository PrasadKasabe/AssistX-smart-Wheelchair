package com.smartwheelchair.app.domain

/**
 * Pure Kotlin utility to map speech recognition text to wheelchair [Command]s.
 */
object CommandMapper {

    /**
     * Maps raw speech text to a [Command], or null if no match or ambiguous.
     */
    fun map(rawText: String?): Command? {
        if (rawText.isNullOrBlank()) return null

        val text = rawText.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .trim()
            .replace(Regex("\\s+"), " ")

        if (text.isEmpty()) return null

        // Rule 1: Safety first - if text contains any stop keyword, always return STOP
        val stopKeywords = listOf("stop", "halt", "brake", "wait", "pause", "emergency")
        if (stopKeywords.any { hasPhrase(text, it) }) {
            return Command.STOP
        }

        // Check for conflicting directions
        val hasForward = listOf("go forward", "move forward", "forward", "ahead", "straight", "front").any { hasPhrase(text, it) }
        val hasBackward = listOf("go back", "move back", "backward", "backwards", "reverse", "back").any { hasPhrase(text, it) }
        val hasLeft = listOf("turn left", "go left", "left").any { hasPhrase(text, it) }
        val hasRight = listOf("turn right", "go right", "right").any { hasPhrase(text, it) }

        val matches = listOf(hasForward, hasBackward, hasLeft, hasRight).count { it }
        if (matches > 1) {
            return null // Conflicting directions
        }

        return when {
            hasForward -> Command.FORWARD
            hasBackward -> Command.BACKWARD
            hasLeft -> Command.LEFT
            hasRight -> Command.RIGHT
            hasPhrase(text, "go") -> Command.FORWARD
            else -> null
        }
    }

    /**
     * Maps using candidate list from SpeechRecognizer (EXTRA_RESULTS).
     */
    fun mapCandidates(candidates: List<String>): Command? {
        for (candidate in candidates) {
            val cmd = map(candidate)
            if (cmd != null) {
                return cmd
            }
        }
        return null
    }

    private fun hasPhrase(text: String, phrase: String): Boolean {
        if (text == phrase) return true
        if (text.startsWith("$phrase ")) return true
        if (text.endsWith(" $phrase")) return true
        if (text.contains(" $phrase ")) return true
        return false
    }
}
