package com.example.brainnode.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.*

class TTSService(private val context: Context) : TextToSpeech.OnInitListener {
    
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var onTTSReadyCallback: (() -> Unit)? = null
    
    companion object {
        private const val TAG = "TTSService"
        private const val UTTERANCE_ID = "BrainNODE_TTS"
    }
    
    init {
        initializeTTS()
    }
    
    private fun initializeTTS() {
        try {
            textToSpeech = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TTS: ${e.message}")
            showError("Failed to initialize Text-to-Speech")
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                // Set language to English (US)
                val result = tts.setLanguage(Locale.US)
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported")
                    showError("English language not supported for Text-to-Speech")
                } else {
                    isInitialized = true
                    Log.d(TAG, "TTS initialized successfully")
                    
                    // Set speech rate and pitch for better readability
                    tts.setSpeechRate(0.8f) // Slightly slower for better comprehension
                    tts.setPitch(1.0f) // Normal pitch
                    
                    // Set up utterance progress listener
                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            Log.d(TAG, "TTS started speaking")
                        }
                        
                        override fun onDone(utteranceId: String?) {
                            Log.d(TAG, "TTS finished speaking")
                        }
                        
                        override fun onError(utteranceId: String?) {
                            Log.e(TAG, "TTS error occurred")
                        }
                    })
                    
                    // Execute callback if waiting
                    onTTSReadyCallback?.invoke()
                    onTTSReadyCallback = null
                }
            }
        } else {
            Log.e(TAG, "TTS initialization failed")
            showError("Failed to initialize Text-to-Speech")
        }
    }
    
    fun speak(text: String, onReady: (() -> Unit)? = null) {
        if (text.isBlank()) {
            showError("No text to read")
            return
        }
        
        if (isInitialized) {
            performSpeak(text)
        } else {
            // Wait for TTS to be ready
            onTTSReadyCallback = {
                performSpeak(text)
                onReady?.invoke()
            }
            showInfo("Preparing Text-to-Speech...")
        }
    }
    
    private fun performSpeak(text: String) {
        try {
            textToSpeech?.let { tts ->
                // Stop any current speech
                if (tts.isSpeaking) {
                    tts.stop()
                }
                
                // Clean text for better speech
                val cleanText = cleanTextForSpeech(text)
                
                // Speak the text
                val result = tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
                
                if (result == TextToSpeech.ERROR) {
                    Log.e(TAG, "Error in speaking text")
                    showError("Error occurred while reading text")
                } else {
                    Log.d(TAG, "Started speaking text of length: ${cleanText.length}")
                    showInfo("Reading text...")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in performSpeak: ${e.message}")
            showError("Error occurred while reading text")
        }
    }
    
    private fun cleanTextForSpeech(text: String): String {
        return text
            .replace("\n\n", ". ") // Replace double newlines with periods
            .replace("\n", " ") // Replace single newlines with spaces
            .replace("  ", " ") // Replace double spaces with single spaces
            .trim()
    }
    
    fun stop() {
        try {
            textToSpeech?.let { tts ->
                if (tts.isSpeaking) {
                    tts.stop()
                    Log.d(TAG, "TTS stopped")
                    showInfo("Reading stopped")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS: ${e.message}")
        }
    }
    
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }
    
    fun shutdown() {
        try {
            textToSpeech?.let { tts ->
                tts.stop()
                tts.shutdown()
                Log.d(TAG, "TTS shutdown")
            }
            textToSpeech = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS: ${e.message}")
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    private fun showInfo(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
