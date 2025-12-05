package com.clmg.applicationflowdaily.data.repository

import android.util.Log
import com.clmg.applicationflowdaily.data.firestore.FirebaseModule
import com.clmg.applicationflowdaily.data.models.BalanceModel
import com.clmg.applicationflowdaily.data.models.ExpenseModel
import kotlinx.coroutines.tasks.await

class ExpenseRepository {

    private val db = FirebaseModule.db
    private val expensesCollection = db.collection("expenses")
    private val balancesCollection = db.collection("balances")

    companion object {
        private const val TAG = "ExpenseRepository"
    }

    /**
     * Obtiene el ID del usuario actual
     */
    private fun getCurrentUserId(): String? {
        val userId = FirebaseModule.getCurrentUserId()
        Log.d(TAG, "🔑 Usuario actual ID: $userId")
        return userId
    }

    // ========== OPERACIONES DE GASTOS ==========

    /**
     * Guarda un nuevo gasto
     */
    suspend fun saveExpense(expense: ExpenseModel): Result<Unit> {
        return try {
            Log.d(TAG, "📥 Iniciando guardado de gasto...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ ERROR: Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado. Por favor inicia sesión."))
            }

            Log.d(TAG, "✅ Usuario autenticado: $userId")

            val expenseWithUser = expense.copy(userId = userId)

            Log.d(TAG, "📋 Datos a guardar:")
            Log.d(TAG, "   - ID: ${expenseWithUser.id}")
            Log.d(TAG, "   - UserID: ${expenseWithUser.userId}")
            Log.d(TAG, "   - Monto: ${expenseWithUser.monto}")
            Log.d(TAG, "   - Descripción: ${expenseWithUser.descripcion}")

            Log.d(TAG, "💾 Guardando en Firestore...")
            expensesCollection
                .document(expense.id)
                .set(expenseWithUser)
                .await()

            Log.d(TAG, "✅✅✅ Gasto guardado exitosamente: ${expense.id}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌❌❌ Error guardando gasto")
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
     * Obtiene todos los gastos del usuario actual
     */
    suspend fun getExpenses(): Result<List<ExpenseModel>> {
        return try {
            Log.d(TAG, "📥 Obteniendo gastos...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            Log.d(TAG, "🔍 Buscando gastos para usuario: $userId")

            val snapshot = expensesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            var expenses = snapshot.documents.mapNotNull {
                it.toObject(ExpenseModel::class.java)
            }

            // Ordenar por timestamp (más reciente primero)
            expenses = expenses.sortedByDescending { it.timestamp }

            Log.d(TAG, "✅ Gastos obtenidos: ${expenses.size}")
            expenses.forEachIndexed { index, expense ->
                Log.d(TAG, "   ${index + 1}. ${expense.descripcion} - \$${expense.monto}")
            }

            Result.success(expenses)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo gastos: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza un gasto existente
     */
    suspend fun updateExpense(expense: ExpenseModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val expenseWithUser = expense.copy(userId = userId)

            Log.d(TAG, "🔄 Actualizando gasto: ${expense.id}")

            expensesCollection
                .document(expense.id)
                .set(expenseWithUser)
                .await()

            Log.d(TAG, "✅ Gasto actualizado: ${expense.id}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error actualizando gasto: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina un gasto
     */
    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            Log.d(TAG, "🗑️ Eliminando gasto: $expenseId")

            expensesCollection
                .document(expenseId)
                .delete()
                .await()

            Log.d(TAG, "✅ Gasto eliminado: $expenseId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error eliminando gasto: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ========== OPERACIONES DE BALANCE ==========

    /**
     * Guarda o actualiza el balance inicial del usuario
     */
    suspend fun saveBalance(balance: BalanceModel): Result<Unit> {
        return try {
            Log.d(TAG, "📥 Guardando balance...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ ERROR: Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val balanceWithUser = balance.copy(
                id = "balance_$userId", // ID único por usuario
                userId = userId
            )

            Log.d(TAG, "💾 Guardando balance: ${balanceWithUser.balanceInicial}")

            balancesCollection
                .document(balanceWithUser.id)
                .set(balanceWithUser)
                .await()

            Log.d(TAG, "✅ Balance guardado exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error guardando balance: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene el balance inicial del usuario
     */
    suspend fun getBalance(): Result<BalanceModel?> {
        return try {
            Log.d(TAG, "📥 Obteniendo balance...")

            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e(TAG, "❌ Usuario no autenticado")
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val documentId = "balance_$userId"
            val snapshot = balancesCollection
                .document(documentId)
                .get()
                .await()

            val balance = snapshot.toObject(BalanceModel::class.java)
            Log.d(TAG, "✅ Balance obtenido: ${balance?.balanceInicial ?: 0.0}")

            Result.success(balance)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo balance: ${e.message}", e)
            Result.failure(e)
        }
    }
}