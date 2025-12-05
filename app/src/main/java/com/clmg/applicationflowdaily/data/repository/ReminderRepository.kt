package com.clmg.applicationflowdaily.data.repository

import android.util.Log
import com.clmg.applicationflowdaily.data.firestore.FirebaseModule
import com.clmg.applicationflowdaily.data.models.ReminderModel
import kotlinx.coroutines.tasks.await

class ReminderRepository {

    private val db = FirebaseModule.db
    private val remindersCollection = db.collection("reminders")

    companion object {
        private const val TAG = "ReminderRepository"
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
     * Guarda un nuevo recordatorio
     */
    suspend fun saveReminder(reminder: ReminderModel): Result<Unit> {
        return try {
            Log.d(TAG, "📥 Iniciando guardado de recordatorio...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ ERROR: Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado. Por favor inicia sesión."))
            }

            Log.d(TAG, "✅ Usuario autenticado: $userId")

            val reminderWithUser = reminder.copy(userId = userId)

            Log.d(TAG, "📋 Datos a guardar:")
            Log.d(TAG, "   - ID: ${reminderWithUser.id}")
            Log.d(TAG, "   - UserID: ${reminderWithUser.userId}")
            Log.d(TAG, "   - Title: ${reminderWithUser.title}")
            Log.d(TAG, "   - Date: ${reminderWithUser.date}")
            Log.d(TAG, "   - Time: ${reminderWithUser.time}")

            Log.d(TAG, "💾 Guardando en Firestore...")
            remindersCollection
                .document(reminder.id)
                .set(reminderWithUser)
                .await()

            Log.d(TAG, "✅✅✅ Recordatorio guardado exitosamente: ${reminder.id}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌❌❌ Error guardando recordatorio")
            Log.e(TAG, "❌ Tipo de error: ${e.javaClass.simpleName}")
            Log.e(TAG, "❌ Mensaje: ${e.message}")
            Log.e(TAG, "❌ Stack trace:", e)

            // Errores específicos
            when {
                e.message?.contains("PERMISSION_DENIED") == true -> {
                    Log.e(TAG, "❌ ERROR DE PERMISOS - Revisa las reglas de Firestore")
                }
                e.message?.contains("FAILED_PRECONDITION") == true -> {
                    Log.e(TAG, "❌ ERROR DE ÍNDICE - Necesitas crear un índice compuesto")
                    Log.e(TAG, "❌ Busca el enlace en Logcat para crear el índice")
                }
            }

            Result.failure(e)
        }
    }

    /**
     * ✅ MODIFICADO: Obtiene TODOS los recordatorios del usuario (pasados y futuros)
     */
    suspend fun getReminders(): Result<List<ReminderModel>> {
        return try {
            Log.d(TAG, "📥 Obteniendo recordatorios...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d(TAG, "🔍 Buscando recordatorios para usuario: $userId")

            // ✅ CAMBIO: Ahora NO filtramos por isCompleted - traemos TODOS
            val snapshot = remindersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            var reminders = snapshot.documents.mapNotNull {
                it.toObject(ReminderModel::class.java)
            }

            // Ordenar en memoria por timestamp (más recientes primero)
            reminders = reminders.sortedByDescending { it.timestamp }

            Log.d(TAG, "✅ Recordatorios obtenidos: ${reminders.size}")
            reminders.forEachIndexed { index, reminder ->
                Log.d(TAG, "   ${index + 1}. ${reminder.title} - ${reminder.date} (Completado: ${reminder.isCompleted})")
            }

            Result.success(reminders)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo recordatorios: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza un recordatorio existente
     */
    suspend fun updateReminder(reminder: ReminderModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val reminderWithUser = reminder.copy(userId = userId)

            Log.d(TAG, "🔄 Actualizando recordatorio: ${reminder.id}")

            remindersCollection
                .document(reminder.id)
                .set(reminderWithUser)
                .await()

            Log.d(TAG, "✅ Recordatorio actualizado: ${reminder.id}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error actualizando recordatorio: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Marca un recordatorio como completado (no lo elimina)
     */
    suspend fun markAsCompleted(reminderId: String): Result<Unit> {
        return try {
            Log.d(TAG, "✅ Marcando recordatorio como completado: $reminderId")

            remindersCollection
                .document(reminderId)
                .update("isCompleted", true)
                .await()

            Log.d(TAG, "✅ Recordatorio completado: $reminderId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error marcando como completado: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Pospone un recordatorio 5 minutos (actualiza fecha/hora)
     */
    suspend fun postponeReminder(reminderId: String, newDate: String, newTime: String): Result<Unit> {
        return try {
            Log.d(TAG, "⏰ Posponiendo recordatorio: $reminderId")

            remindersCollection
                .document(reminderId)
                .update(
                    mapOf(
                        "date" to newDate,
                        "time" to newTime,
                        "timestamp" to System.currentTimeMillis()
                    )
                )
                .await()

            Log.d(TAG, "✅ Recordatorio pospuesto: $reminderId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error posponiendo recordatorio: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina un recordatorio (FÍSICAMENTE - solo si el usuario lo borra manualmente)
     */
    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            Log.d(TAG, "🗑️ Eliminando recordatorio: $reminderId")

            remindersCollection
                .document(reminderId)
                .delete()
                .await()

            Log.d(TAG, "✅ Recordatorio eliminado: $reminderId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error eliminando recordatorio: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene un recordatorio específico por ID
     */
    suspend fun getReminderById(reminderId: String): Result<ReminderModel?> {
        return try {
            val snapshot = remindersCollection
                .document(reminderId)
                .get()
                .await()

            val reminder = snapshot.toObject(ReminderModel::class.java)
            Result.success(reminder)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo recordatorio: ${e.message}", e)
            Result.failure(e)
        }
    }
}