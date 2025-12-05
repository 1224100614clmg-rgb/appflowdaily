package com.clmg.applicationflowdaily.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clmg.applicationflowdaily.data.models.ReminderModel
import com.clmg.applicationflowdaily.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel : ViewModel() {

    private val repository = ReminderRepository()

    private val _reminders = MutableStateFlow<List<ReminderModel>>(emptyList())
    val reminders: StateFlow<List<ReminderModel>> = _reminders.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    companion object {
        private const val TAG = "ReminderViewModel"
    }

    init {
        loadReminders()
    }

    /**
     * Carga todos los recordatorios del usuario
     */
    fun loadReminders() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🔄 Cargando recordatorios...")

                repository.getReminders().fold(
                    onSuccess = { reminders ->
                        _reminders.value = reminders
                        Log.d(TAG, "✅ Recordatorios cargados: ${reminders.size}")
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error desconocido"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error cargando recordatorios: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Guarda un nuevo recordatorio
     */
    fun saveReminder(reminder: ReminderModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "💾 Guardando recordatorio: ${reminder.title}")

                repository.saveReminder(reminder).fold(
                    onSuccess = {
                        _operationSuccess.value = "Recordatorio guardado exitosamente"
                        Log.d(TAG, "✅ Recordatorio guardado correctamente")
                        loadReminders() // ✅ RECARGAR LISTA
                        onSuccess()
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error al guardar"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error guardando: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Actualiza un recordatorio existente
     */
    fun updateReminder(reminder: ReminderModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🔄 Actualizando recordatorio: ${reminder.id}")

                repository.updateReminder(reminder).fold(
                    onSuccess = {
                        _operationSuccess.value = "Recordatorio actualizado"
                        Log.d(TAG, "✅ Recordatorio actualizado correctamente")
                        loadReminders()
                        onSuccess()
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error al actualizar"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error actualizando: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * ✅ NUEVO: Marca un recordatorio como completado
     */
    fun markAsCompleted(reminderId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "✅ Completando recordatorio: $reminderId")

                repository.markAsCompleted(reminderId).fold(
                    onSuccess = {
                        _operationSuccess.value = "Recordatorio completado"
                        Log.d(TAG, "✅ Recordatorio completado correctamente")
                        loadReminders()
                        onSuccess()
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error al completar"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error completando: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * ✅ NUEVO: Pospone un recordatorio 5 minutos
     */
    fun postponeReminder(reminderId: String, newDate: String, newTime: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "⏰ Posponiendo recordatorio: $reminderId")

                repository.postponeReminder(reminderId, newDate, newTime).fold(
                    onSuccess = {
                        _operationSuccess.value = "Recordatorio pospuesto 5 minutos"
                        Log.d(TAG, "✅ Recordatorio pospuesto correctamente")
                        loadReminders()
                        onSuccess()
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error al posponer"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error posponiendo: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Elimina un recordatorio (solo borrado manual)
     */
    fun deleteReminder(reminderId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🗑️ Eliminando recordatorio: $reminderId")

                repository.deleteReminder(reminderId).fold(
                    onSuccess = {
                        _operationSuccess.value = "Recordatorio eliminado"
                        Log.d(TAG, "✅ Recordatorio eliminado correctamente")
                        loadReminders()
                        onSuccess()
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error al eliminar"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error eliminando: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccess() {
        _operationSuccess.value = null
    }
}