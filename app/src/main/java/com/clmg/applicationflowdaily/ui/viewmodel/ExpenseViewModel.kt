package com.clmg.applicationflowdaily.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clmg.applicationflowdaily.data.models.BalanceModel
import com.clmg.applicationflowdaily.data.models.ExpenseModel
import com.clmg.applicationflowdaily.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel : ViewModel() {

    private val repository = ExpenseRepository()

    private val _expenses = MutableStateFlow<List<ExpenseModel>>(emptyList())
    val expenses: StateFlow<List<ExpenseModel>> = _expenses.asStateFlow()

    private val _balance = MutableStateFlow<BalanceModel?>(null)
    val balance: StateFlow<BalanceModel?> = _balance.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    companion object {
        private const val TAG = "ExpenseViewModel"
    }

    init {
        loadExpenses()
        loadBalance()
    }

    /**
     * Carga todos los gastos del usuario
     */
    fun loadExpenses() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🔄 Cargando gastos...")

                repository.getExpenses().fold(
                    onSuccess = { expenses ->
                        _expenses.value = expenses
                        Log.d(TAG, "✅ Gastos cargados: ${expenses.size}")
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error desconocido"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error cargando gastos: $errorMsg", exception)
                    }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Carga el balance del usuario
     */
    fun loadBalance() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "🔄 Cargando balance...")

                repository.getBalance().fold(
                    onSuccess = { balance ->
                        _balance.value = balance
                        Log.d(TAG, "✅ Balance cargado: ${balance?.balanceInicial ?: 0.0}")
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ Error cargando balance: ${exception.message}", exception)
                        // No mostramos error al usuario, simplemente usamos balance 0
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción cargando balance: ${e.message}", e)
            }
        }
    }

    /**
     * Guarda un nuevo gasto
     */
    fun saveExpense(expense: ExpenseModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "💾 Guardando gasto: ${expense.descripcion}")

                repository.saveExpense(expense).fold(
                    onSuccess = {
                        _operationSuccess.value = "Gasto guardado exitosamente"
                        Log.d(TAG, "✅ Gasto guardado correctamente")
                        loadExpenses()
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
     * Actualiza un gasto existente
     */
    fun updateExpense(expense: ExpenseModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🔄 Actualizando gasto: ${expense.id}")

                repository.updateExpense(expense).fold(
                    onSuccess = {
                        _operationSuccess.value = "Gasto actualizado"
                        Log.d(TAG, "✅ Gasto actualizado correctamente")
                        loadExpenses()
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
     * Elimina un gasto
     */
    fun deleteExpense(expenseId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "🗑️ Eliminando gasto: $expenseId")

                repository.deleteExpense(expenseId).fold(
                    onSuccess = {
                        _operationSuccess.value = "Gasto eliminado"
                        Log.d(TAG, "✅ Gasto eliminado correctamente")
                        loadExpenses()
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
     * Guarda el balance inicial
     */
    fun saveBalance(balanceInicial: Double, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                Log.d(TAG, "💾 Guardando balance: $balanceInicial")

                val balanceModel = BalanceModel(
                    balanceInicial = balanceInicial
                )

                repository.saveBalance(balanceModel).fold(
                    onSuccess = {
                        _operationSuccess.value = "Balance actualizado"
                        Log.d(TAG, "✅ Balance guardado correctamente")
                        loadBalance()
                        onSuccess()
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Error al guardar balance"
                        _error.value = errorMsg
                        Log.e(TAG, "❌ Error guardando balance: $errorMsg", exception)
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