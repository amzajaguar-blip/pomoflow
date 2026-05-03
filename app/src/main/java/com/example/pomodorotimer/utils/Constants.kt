package com.example.pomodorotimer.utils

import com.example.pomodorotimer.BuildConfig

/**
 * Costanti globali dell'applicazione Pomodoro Pro
 */
object Constants {
    // Timer durations (in minutes)
    const val FOCUS_SESSION_MINUTES = 25
    const val BREAK_SESSION_MINUTES = 5

    // SharedPreferences keys
    const val PREFS_NAME = "pomodoro_prefs"
    const val KEY_LAST_DATE = "last_date"
    const val KEY_POMODORO_COUNT = "count"
    const val KEY_TASKS = "tasks"

    // Task management
    const val DEFAULT_TASK_COUNT = 3
    const val TASK_SEPARATOR = "|"

    // AdMob configuration - usa BuildConfig per switch automatico debug/release
    val ADMOB_BANNER_ID: String = BuildConfig.ADMOB_BANNER_ID
    val ADMOB_APP_ID: String = BuildConfig.ADMOB_APP_ID

    // UI dimensions
    const val PADDING_LARGE = 24
    const val PADDING_MEDIUM = 16
    const val PADDING_SMALL = 8
    const val BUTTON_HEIGHT = 56

    // Date format
    const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
}
