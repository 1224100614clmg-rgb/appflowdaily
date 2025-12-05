package com.clmg.applicationflowdaily.data.models

data class ExpenseModel(
    val id: String = "",
    val userId: String = "",
    val monto: Double = 0.0,
    val descripcion: String = "",
    val categoria: String = "",
    val fecha: Long = System.currentTimeMillis(), // Timestamp en milisegundos
    val tipo: String = "GASTO", // "GASTO" o "INGRESO"
    val emoji: String = "💰",
    val timestamp: Long = System.currentTimeMillis()
)

data class BalanceModel(
    val id: String = "balance_inicial",
    val userId: String = "",
    val balanceInicial: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)