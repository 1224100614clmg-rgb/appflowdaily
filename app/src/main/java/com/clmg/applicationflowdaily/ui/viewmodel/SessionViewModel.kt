package com.clmg.applicationflowdaily.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clmg.applicationflowdaily.data.models.DailySession
import com.clmg.applicationflowdaily.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    private val _sessions = MutableStateFlow<List<DailySession>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun loadSessions() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _sessions.value = repo.getSessions()
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveSession(session: DailySession) {
        viewModelScope.launch {
            repo.saveSession(session)
            loadSessions()
        }
    }
}
