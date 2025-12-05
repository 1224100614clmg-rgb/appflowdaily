package com.clmg.applicationflowdaily.data.models

data class ReminderModel(
    val id: String = "",
    val userId: String = "", // Para asociar con el usuario actual
    val title: String = "",
    val description: String = "",
    val category: String = "", // Guardamos como String para Firestore
    val date: String = "",
    val time: String = "",
    val notificationActive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(), // Para ordenar
    val isCompleted: Boolean = false // ✅ Controla si el usuario lo marcó como completado
)