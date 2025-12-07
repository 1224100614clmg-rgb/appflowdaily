package com.clmg.applicationflowdaily.data.models

import com.google.firebase.firestore.DocumentId

data class TransportModel(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val originLat: Double = 0.0,
    val originLng: Double = 0.0,
    val destinationLat: Double = 0.0,
    val destinationLng: Double = 0.0,
    val distanceKm: Double = 0.0,
    val durationMinutes: Int = 0, // ✅ Tiempo real de Google Maps
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)