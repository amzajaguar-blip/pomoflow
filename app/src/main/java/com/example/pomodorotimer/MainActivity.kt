package com.example.pomodorotimer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodorotimer.data.PomodoroPrefs
import com.example.pomodorotimer.ui.components.*
import com.example.pomodorotimer.ui.theme.PomodoroTimerTheme
import com.example.pomodorotimer.utils.Constants
//import com.google.android.gms.ads.MobileAds

/**
 * Activity principale dell'app Pomodoro Pro
 * Gestisce l'inizializzazione dell'app e la configurazione del tema
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity creata")

        try {
            // Inizializzazione Mobile Ads
            //MobileAds.initialize(this) {}

            // Setup del content con ViewModel retainato via Factory
            setContent {
                val prefs = remember { PomodoroPrefs(this@MainActivity) }
                val factory = remember { PomodoroViewModelFactory(prefs) }
                val viewModel: PomodoroViewModel = viewModel(factory = factory)

                PomodoroTimerTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        PomodoroApp(viewModel)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore nell'inizializzazione: ${e.message}", e)
        }
    }
}

/**
 * Factory per creare PomodoroViewModel con dipendenze
 * Necessaria per retainare il ViewModel attraverso i configuration changes
 */
class PomodoroViewModelFactory(
    private val prefs: PomodoroPrefs
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            return PomodoroViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * Composable principale dell'app
 * Organizza tutti i componenti UI
 */
@Composable
fun PomodoroApp(viewModel: PomodoroViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Constants.PADDING_LARGE.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Sezione superiore: Task
        TasksSection(
            tasks = viewModel.tasks,
            onTaskUpdate = { index, task -> viewModel.updateTask(index, task) }
        )

        // Sezione centrale: Timer e controlli
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimerDisplay(
                timeFormatted = viewModel.formatTime(viewModel.timeLeft.value),
                isBreak = viewModel.isBreak.value
            )

            Spacer(modifier = Modifier.height(32.dp))

            TimerControls(
                isRunning = viewModel.isRunning.value,
                onStartFocus = { viewModel.startTimer(Constants.FOCUS_SESSION_MINUTES, false) },
                onStartBreak = { viewModel.startTimer(Constants.BREAK_SESSION_MINUTES, true) },
                onStop = { viewModel.stopTimer() }
            )
        }

        // Sezione inferiore: Statistiche e annunci
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Messaggio di errore
            ErrorMessage(
                message = viewModel.errorMessage.value,
                onDismiss = { viewModel.clearError() }
            )

            Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM.dp))

            // Card statistiche
            StatsCard(pomodorosCompleted = viewModel.pomodorosCompleted.value)

            Spacer(modifier = Modifier.height(Constants.PADDING_LARGE.dp))

            // Banner AdMob
            AdBanner()
        }
    }
}
