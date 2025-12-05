package com.clmg.applicationflowdaily.data.models

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val language: String = "es" // "es" para Español, "en" para English
)