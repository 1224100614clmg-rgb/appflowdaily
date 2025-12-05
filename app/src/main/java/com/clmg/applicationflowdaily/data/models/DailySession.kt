package com.clmg.applicationflowdaily.data.models

data class DailySession(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis()
)
