package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.game.GameApp
import com.example.game.GameViewModel
import com.example.ui.theme.MyApplicationTheme
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Android Arabic Native Text To Speech engine
        try {
            tts = TextToSpeech(this) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    try {
                        // Try setting Arabic locale
                        val arabicResult = tts?.setLanguage(Locale("ar"))
                        if (arabicResult == TextToSpeech.LANG_MISSING_DATA || arabicResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS_Tlemcen", "Arabic language is not supported or missing data on this device TTS engine")
                        } else {
                            Log.i("TTS_Tlemcen", "Arabic engine is ready!")
                        }
                    } catch (e: Exception) {
                        Log.e("TTS_Tlemcen", "Failed to configure language for TTS", e)
                    }
                } else {
                    Log.e("TTS_Tlemcen", "Initialization failed with status: $status")
                }
            }
        } catch (e: Exception) {
            Log.e("TTS_Tlemcen", "TextToSpeech constructor threw exception", e)
        }

        // Collect and speak text spoken events from ViewModel reactively
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.speakEvent.collectLatest { text ->
                    if (text.isNotBlank()) {
                        try {
                            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ArabicTlemcenSpeechId")
                        } catch (e: Exception) {
                            Log.e("TTS_Tlemcen", "Failed to speak text: $text", e)
                        }
                    }
                }
            }
        }

        setContent {
            MyApplicationTheme {
                GameApp(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("TTS_Tlemcen", "Error during TTS shutdown", e)
        }
        super.onDestroy()
    }
}
