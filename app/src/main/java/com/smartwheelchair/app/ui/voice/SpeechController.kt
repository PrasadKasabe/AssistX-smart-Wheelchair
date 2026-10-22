package com.smartwheelchair.app.ui.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechController(
    private val context: Context,
    private val onPartialResult: (String) -> Unit,
    private val onFinalResult: (List<String>) -> Unit,
    private val onError: (String) -> Unit,
    private val onStateChanged: (VoiceState) -> Unit
) {
    enum class VoiceState {
        IDLE, LISTENING, PROCESSING
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        }
    }

    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context) && speechRecognizer != null

    fun startListening() {
        if (!isAvailable) {
            onError("Voice recognition is not available on this device.")
            return
        }
        if (isListening) return

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }

        try {
            speechRecognizer?.startListening(intent)
            isListening = true
            onStateChanged(VoiceState.LISTENING)
        } catch (e: Exception) {
            Log.e("SpeechController", "Error starting listening", e)
            isListening = false
            onStateChanged(VoiceState.IDLE)
            onError("Could not start voice recognition.")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechController", "Error stopping listening", e)
        }
        isListening = false
        onStateChanged(VoiceState.IDLE)
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e("SpeechController", "Error destroying speech recognizer", e)
        }
        isListening = false
    }

    private fun createListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            onStateChanged(VoiceState.PROCESSING)
        }

        override fun onError(error: Int) {
            isListening = false
            onStateChanged(VoiceState.IDLE)
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No command matched. Try again."
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Speech recognition error"
            }
            if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                onError(message)
            } else {
                onFinalResult(emptyList())
            }
        }

        override fun onResults(results: Bundle?) {
            isListening = false
            onStateChanged(VoiceState.IDLE)
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList()
            onFinalResult(matches)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList()
            if (matches.isNotEmpty()) {
                onPartialResult(matches[0])
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}
