package com.clmg.applicationflowdaily.ui.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clmg.applicationflowdaily.data.local.PreferencesDataStore
import com.clmg.applicationflowdaily.data.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesDataStore = PreferencesDataStore(application)

    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesDataStore.userPreferencesFlow.collect { preferences ->
                _userPreferences.value = preferences
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_userPreferences.value.isDarkMode
            preferencesDataStore.updateDarkMode(newValue)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            preferencesDataStore.updateLanguage(language)
        }
    }
}