package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthScreen
import com.example.auth.AuthState
import com.example.auth.AuthViewModel
import com.example.auth.Roles
import com.example.game.GameApp
import com.example.game.GameViewModel
import com.example.materials.MaterialsScreen
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

        try {
            tts = TextToSpeech(this) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    try {
                        val r = tts?.setLanguage(Locale("ar"))
                        if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS_Tlemcen", "Arabic not supported on this device TTS")
                        }
                    } catch (e: Exception) {
                        Log.e("TTS_Tlemcen", "Failed to configure TTS language", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TTS_Tlemcen", "TextToSpeech constructor failed", e)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.speakEvent.collectLatest { text ->
                    if (text.isNotBlank()) {
                        try {
                            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ArabicTlemcenSpeechId")
                        } catch (e: Exception) {
                            Log.e("TTS_Tlemcen", "Failed to speak: $text", e)
                        }
                    }
                }
            }
        }

        setContent {
            MyApplicationTheme {
                AppRoot(gameViewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        try { tts?.stop(); tts?.shutdown() } catch (e: Exception) {
            Log.e("TTS_Tlemcen", "TTS shutdown error", e)
        }
        super.onDestroy()
    }
}

private enum class Tab { LEARN, MATERIALS }

@Composable
private fun AppRoot(gameViewModel: GameViewModel) {
    val authViewModel: AuthViewModel = viewModel()

    when (val s = authViewModel.state) {
        is AuthState.Loading -> Box(
            Modifier.fillMaxSize(), Alignment.Center
        ) { Text("جارٍ التحميل…") }

        is AuthState.SignedOut -> AuthScreen(authViewModel)

        is AuthState.SignedIn -> {
            var tab by remember { mutableStateOf(Tab.LEARN) }
            val isProfessor = s.role == Roles.PROFESSOR

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = tab == Tab.LEARN,
                            onClick = { tab = Tab.LEARN },
                            icon = { Icon(Icons.Filled.School, contentDescription = null) },
                            label = { Text("تعلّم") }
                        )
                        NavigationBarItem(
                            selected = tab == Tab.MATERIALS,
                            onClick = { tab = Tab.MATERIALS },
                            icon = { Icon(Icons.Filled.Folder, contentDescription = null) },
                            label = { Text("المواد") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { authViewModel.signOut() },
                            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                            label = { Text("خروج") }
                        )
                    }
                }
            ) { pad ->
                Surface(
                    modifier = Modifier.fillMaxSize().padding(pad),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (tab) {
                        Tab.LEARN -> GameApp(viewModel = gameViewModel)
                        Tab.MATERIALS -> MaterialsScreen(
                            isProfessor = isProfessor,
                            uploaderEmail = s.email
                        )
                    }
                }
            }
        }
    }
}
