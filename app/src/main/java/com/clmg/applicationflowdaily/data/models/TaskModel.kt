package com.clmg.applicationflowdaily.data.models

data class TaskModel(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val time: String = "",
    val completed: Boolean = false,
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis()
)