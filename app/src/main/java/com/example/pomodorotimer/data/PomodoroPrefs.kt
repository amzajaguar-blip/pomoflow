package com.example.pomodorotimer.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.pomodorotimer.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository per la gestione della persistenza dati Pomodoro
 * Implementa il pattern Repository per separare la logica di accesso ai dati
 */
class PomodoroPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )
    private val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Locale.getDefault())

    companion object {
        private const val TAG = "PomodoroPrefs"
    }

    /**
     * Recupera il numero di pomodori completati oggi
     * Resetta il contatore se è un nuovo giorno
     */
    fun getPomodorosToday(): Int = try {
        val today = dateFormat.format(Date())
        val lastDate = prefs.getString(Constants.KEY_LAST_DATE, "") ?: ""

        if (lastDate == today) {
            prefs.getInt(Constants.KEY_POMODORO_COUNT, 0)
        } else {
            Log.d(TAG, "Nuovo giorno rilevato. Reset contatore pomodori.")
            0
        }
    } catch (e: Exception) {
        Log.e(TAG, "Errore nel recupero dei pomodori: ${e.message}", e)
        0
    }

    /**
     * Incrementa il contatore dei pomodori completati
     */
    fun incrementPomodoros() {
        try {
            val today = dateFormat.format(Date())
            val currentCount = getPomodorosToday()

            prefs.edit().apply {
                putString(Constants.KEY_LAST_DATE, today)
                putInt(Constants.KEY_POMODORO_COUNT, currentCount + 1)
                apply()
            }
            Log.d(TAG, "Pomodoro incrementato. Totale: ${currentCount + 1}")
        } catch (e: Exception) {
            Log.e(TAG, "Errore nell'incremento dei pomodori: ${e.message}", e)
        }
    }

    /**
     * Salva la lista dei task
     */
    fun saveTasks(tasks: List<String>) {
        try {
            val tasksString = tasks.joinToString(Constants.TASK_SEPARATOR)
            prefs.edit().putString(Constants.KEY_TASKS, tasksString).apply()
            Log.d(TAG, "Task salvati: ${tasks.size} elementi")
        } catch (e: Exception) {
            Log.e(TAG, "Errore nel salvataggio dei task: ${e.message}", e)
        }
    }

    /**
     * Recupera la lista dei task salvati
     * Ritorna una lista vuota di 3 elementi se nessun task è salvato
     */
    fun getTasks(): List<String> {
        return try {
            val saved = prefs.getString(Constants.KEY_TASKS, "") ?: ""
            if (saved.isEmpty()) {
                List(Constants.DEFAULT_TASK_COUNT) { "" }
            } else {
                saved.split(Constants.TASK_SEPARATOR)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore nel recupero dei task: ${e.message}", e)
            List(Constants.DEFAULT_TASK_COUNT) { "" }
        }
    }

    /**
     * Cancella tutti i dati salvati (utile per reset/debug)
     */
    fun clearAllData() {
        try {
            prefs.edit().clear().apply()
            Log.d(TAG, "Tutti i dati cancellati")
        } catch (e: Exception) {
            Log.e(TAG, "Errore nella cancellazione dei dati: ${e.message}", e)
        }
    }
}
