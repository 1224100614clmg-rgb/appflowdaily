package com.clmg.applicationflowdaily.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clmg.applicationflowdaily.data.models.TaskModel
import com.clmg.applicationflowdaily.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _tasks = MutableStateFlow<List<TaskModel>>(emptyList())
    val tasks: StateFlow<List<TaskModel>> = _tasks.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    companion object {
        private const val TAG = "TaskViewModel"
    }

    init {
        loadTasks()
    }

    /**
     * Carga todas las tareas del usuario
     */
    fun loadTasks() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🔄 Cargando tareas...")

                repository.getTasks().fold(
                    onSuccess = { tasks ->
                        _tasks.value = tasks
                        Log.d(TAG, "✅ Tareas cargadas: ${tasks.size}")
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error desconocido"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error cargando tareas: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Guarda una nueva tarea
     */
    fun saveTask(task: TaskModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "💾 Guardando tarea: ${task.name}")

                repository.saveTask(task).fold(
                    onSuccess = {
                        _operationSuccess.value = "Tarea guardada exitosamente"
                        Log.d(TAG, "✅ Tarea guardada correctamente")
                        loadTasks()
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
     * Actualiza una tarea existente
     */
    fun updateTask(task: TaskModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🔄 Actualizando tarea: ${task.id}")

                repository.updateTask(task).fold(
                    onSuccess = {
                        _operationSuccess.value = "Tarea actualizada"
                        Log.d(TAG, "✅ Tarea actualizada correctamente")
                        loadTasks()
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
     * Elimina una tarea
     */
    fun deleteTask(taskId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🗑️ Eliminando tarea: $taskId")

                repository.deleteTask(taskId).fold(
                    onSuccess = {
                        _operationSuccess.value = "Tarea eliminada"
                        Log.d(TAG, "✅ Tarea eliminada correctamente")
                        loadTasks()
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