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
import com.example.pomodorotimer.data.AdvancedBillingManager
import com.example.pomodorotimer.data.EnterpriseSettingsManager
import com.example.pomodorotimer.data.PomodoroPrefs
import com.example.pomodorotimer.ui.components.*
import com.example.pomodorotimer.ui.theme.PomodoroTimerTheme
import com.example.pomodorotimer.utils.Constants

/**
 * Activity principale dell'app PomoFlow Pro Enterprise
 * Gestisce l'inizializzazione dell'app, billing e tema
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "PomoFlow Pro Enterprise avviato")

        try {
            setContent {
                val prefs = remember { PomodoroPrefs(this@MainActivity) }
                val billing = remember { AdvancedBillingManager(this@MainActivity) }
                val settings = remember { EnterpriseSettingsManager(this@MainActivity) }
                val factory = remember { PomodoroViewModelFactory(prefs, billing, settings) }
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
 * Factory enterprise per creare PomodoroViewModel con tutte le dipendenze
 */
class PomodoroViewModelFactory(
    private val prefs: PomodoroPrefs,
    private val billing: AdvancedBillingManager,
    private val settings: EnterpriseSettingsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            return PomodoroViewModel(prefs, billing, settings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * Composable principale dell'app enterprise
 */
@Composable
fun PomodoroApp(viewModel: PomodoroViewModel) {
    val isPremium by viewModel.isPremium.collectAsState()

    Scaffold(
        bottomBar = {
            if (!isPremium) {
                PremiumBottomBar(onUpgrade = { viewModel.upgradeToLifetime() })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Constants.PADDING_LARGE.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Enterprise Header
            EnterpriseHeader(pomodorosCompleted = viewModel.pomodorosCompleted.value)

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

            // Enterprise Features Grid
            EnterpriseFeaturesGrid(isPremium = isPremium)

            // Tasks Section
            TasksSection(
                tasks = viewModel.tasks,
                onTaskUpdate = { index, task -> viewModel.updateTask(index, task) }
            )

            // Sezione inferiore
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ErrorMessage(
                    message = viewModel.errorMessage.value,
                    onDismiss = { viewModel.clearError() }
                )

                Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM.dp))

                StatsCard(pomodorosCompleted = viewModel.pomodorosCompleted.value)

                Spacer(modifier = Modifier.height(Constants.PADDING_LARGE.dp))

                if (!isPremium) {
                    AdBanner()
                }
            }
        }
    }
}
