package com.example.pomodorotimer

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodorotimer.data.PomodoroPrefs
import com.example.pomodorotimer.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione della logica dell'app Pomodoro
 * Gestisce il timer, i task e la persistenza dei dati
 */
class PomodoroViewModel(private val prefs: PomodoroPrefs) : ViewModel() {

    companion object {
        private const val TAG = "PomodoroViewModel"
        private const val TIMER_INTERVAL_MS = 1000L
    }

    // State privato
    private val _timeLeft = mutableStateOf(Constants.FOCUS_SESSION_MINUTES * 60)
    val timeLeft: State<Int> = _timeLeft

    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> = _isRunning

    private val _isBreak = mutableStateOf(false)
    val isBreak: State<Boolean> = _isBreak

    private val _pomodorosCompleted = mutableStateOf(prefs.getPomodorosToday())
    val pomodorosCompleted: State<Int> = _pomodorosCompleted

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Task list
    val tasks = mutableStateListOf<String>().apply {
        addAll(prefs.getTasks())
    }

    private var timerJob: Job? = null

    /**
     * Avvia il timer con la durata e il tipo specificati
     */
    fun startTimer(minutes: Int, isBreak: Boolean) {
        try {
            timerJob?.cancel()
            _timeLeft.value = minutes * 60
            _isRunning.value = true
            _isBreak.value = isBreak
            _errorMessage.value = null

            Log.d(TAG, "Timer avviato: ${minutes}min, isBreak=$isBreak")

            timerJob = viewModelScope.launch {
                while (_timeLeft.value > 0) {
                    delay(TIMER_INTERVAL_MS)
                    _timeLeft.value -= 1
                }
                onTimerFinished()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore nell'avvio del timer: ${e.message}", e)
            _errorMessage.value = "Errore nell'avvio del timer"
            _isRunning.value = false
        }
    }

    /**
     * Callback quando il timer termina
     */
    private fun onTimerFinished() {
        try {
            _isRunning.value = false
            if (!_isBreak.value) {
                prefs.incrementPomodoros()
                _pomodorosCompleted.value = prefs.getPomodorosToday()
                Log.d(TAG, "Pomodoro completato. Totale: ${_pomodorosCompleted.value}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore nel completamento del timer: ${e.message}", e)
            _errorMessage.value = "Errore nel salvataggio del pomodoro"
        }
    }

    /**
     * Ferma il timer in corso
     */
    fun stopTimer() {
        try {
            timerJob?.cancel()
            _isRunning.value = false
            Log.d(TAG, "Timer fermato")
        } catch (e: Exception) {
            Log.e(TAG, "Errore nell'arresto del timer: ${e.message}", e)
            _errorMessage.value = "Errore nell'arresto del timer"
        }
    }

    /**
     * Aggiorna un task nella lista
     */
    fun updateTask(index: Int, task: String) {
        try {
            if (index in tasks.indices) {
                tasks[index] = task
                prefs.saveTasks(tasks)
                Log.d(TAG, "Task aggiornato: index=$index")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore nell'aggiornamento del task: ${e.message}", e)
            _errorMessage.value = "Errore nel salvataggio del task"
        }
    }

    /**
     * Formatta i secondi in formato MM:SS
     */
    fun formatTime(seconds: Int): String = try {
        val mins = seconds / 60
        val secs = seconds % 60
        "%02d:%02d".format(mins, secs)
    } catch (e: Exception) {
        Log.e(TAG, "Errore nella formattazione del tempo: ${e.message}", e)
        "00:00"
    }

    /**
     * Cancella il messaggio di errore
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Cleanup quando il ViewModel viene distrutto
     */
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        Log.d(TAG, "ViewModel cleared")
    }
}
