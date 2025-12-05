package com.clmg.applicationflowdaily.data.repository

import android.util.Log
import com.clmg.applicationflowdaily.data.firestore.FirebaseModule
import com.clmg.applicationflowdaily.data.models.TaskModel
import kotlinx.coroutines.tasks.await

class TaskRepository {

    private val db = FirebaseModule.db
    private val tasksCollection = db.collection("tasks")

    companion object {
        private const val TAG = "TaskRepository"
    }

    /**
     * Obtiene el ID del usuario actual
     */
    private fun getCurrentUserId(): String? {
        val userId = FirebaseModule.getCurrentUserId()
        Log.d(TAG, "🔑 Usuario actual ID: $userId")
        return userId
    }

    /**
     * Guarda una nueva tarea
     */
    suspend fun saveTask(task: TaskModel): Result<Unit> {
        return try {
            Log.d(TAG, "📥 Iniciando guardado de tarea...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ ERROR: Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado. Por favor inicia sesión."))
            }

            Log.d(TAG, "✅ Usuario autenticado: $userId")

            val taskWithUser = task.copy(userId = userId)

            Log.d(TAG, "📋 Datos a guardar:")
            Log.d(TAG, "   - ID: ${taskWithUser.id}")
            Log.d(TAG, "   - UserID: ${taskWithUser.userId}")
            Log.d(TAG, "   - Name: ${taskWithUser.name}")
            Log.d(TAG, "   - Time: ${taskWithUser.time}")
            Log.d(TAG, "   - Category: ${taskWithUser.category}")

            Log.d(TAG, "💾 Guardando en Firestore...")
            tasksCollection
                .document(task.id)
                .set(taskWithUser)
                .await()

            Log.d(TAG, "✅✅✅ Tarea guardada exitosamente: ${task.id}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌❌❌ Error guardando tarea")
            Log.e(TAG, "❌ Tipo de error: ${e.javaClass.simpleName}")
            Log.e(TAG, "❌ Mensaje: ${e.message}")
            Log.e(TAG, "❌ Stack trace:", e)

            when {
                e.message?.contains("PERMISSION_DENIED") == true -> {
                    Log.e(TAG, "❌ ERROR DE PERMISOS - Revisa las reglas de Firestore")
                }
                e.message?.contains("FAILED_PRECONDITION") == true -> {
                    Log.e(TAG, "❌ ERROR DE ÍNDICE - Necesitas crear un índice compuesto")
                }
            }

            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las tareas del usuario actual
     */
    suspend fun getTasks(): Result<List<TaskModel>> {
        return try {
            Log.d(TAG, "📥 Obteniendo tareas...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d(TAG, "🔍 Buscando tareas para usuario: $userId")

            val snapshot = tasksCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            var tasks = snapshot.documents.mapNotNull {
                it.toObject(TaskModel::class.java)
            }

            // Ordenar en memoria por timestamp
            tasks = tasks.sortedByDescending { it.timestamp }

            Log.d(TAG, "✅ Tareas obtenidas: ${tasks.size}")
            tasks.forEachIndexed { index, task ->
                Log.d(TAG, "   ${index + 1}. ${task.name} - ${task.time}")
            }

            Result.success(tasks)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo tareas: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza una tarea existente
     */
    suspend fun updateTask(task: TaskModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val taskWithUser = task.copy(userId = userId)

            Log.d(TAG, "🔄 Actualizando tarea: ${task.id}")

            tasksCollection
                .document(task.id)
                .set(taskWithUser)
                .await()

            Log.d(TAG, "✅ Tarea actualizada: ${task.id}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error actualizando tarea: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina una tarea
     */
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            Log.d(TAG, "🗑️ Eliminando tarea: $taskId")

            tasksCollection
                .document(taskId)
                .delete()
                .await()

            Log.d(TAG, "✅ Tarea eliminada: $taskId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error eliminando tarea: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene una tarea específica por ID
     */
    suspend fun getTaskById(taskId: String): Result<TaskModel?> {
        return try {
            val snapshot = tasksCollection
                .document(taskId)
                .get()
                .await()

            val task = snapshot.toObject(TaskModel::class.java)
            Result.success(task)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo tarea: ${e.message}", e)
            Result.failure(e)
        }
    }
}