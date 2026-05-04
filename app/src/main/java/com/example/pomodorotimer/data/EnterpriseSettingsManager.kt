package com.example.pomodorotimer.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

/**
 * EnterpriseSettingsManager: Managing advanced features for power users.
 * High-performance configuration for the successful PomoFlow user.
 */
class EnterpriseSettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("pomoflow_settings", Context.MODE_PRIVATE)

    private val _focusDuration = mutableStateOf(prefs.getInt("focus_min", 25))
    val focusDuration: State<Int> = _focusDuration

    private val _breakDuration = mutableStateOf(prefs.getInt("break_min", 5))
    val breakDuration: State<Int> = _breakDuration

    private val _autoStartNext = mutableStateOf(prefs.getBoolean("auto_start", false))
    val autoStartNext: State<Boolean> = _autoStartNext

    fun updateFocusDuration(min: Int) {
        _focusDuration.value = min
        prefs.edit().putInt("focus_min", min).apply()
    }

    fun updateBreakDuration(min: Int) {
        _breakDuration.value = min
        prefs.edit().putInt("break_min", min).apply()
    }

    fun toggleAutoStart() {
        _autoStartNext.value = !_autoStartNext.value
        prefs.edit().putBoolean("auto_start", _autoStartNext.value).apply()
    }
}
