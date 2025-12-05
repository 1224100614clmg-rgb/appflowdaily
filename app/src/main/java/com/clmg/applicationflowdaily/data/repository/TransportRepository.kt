package com.clmg.applicationflowdaily.data.repository

import com.clmg.applicationflowdaily.data.models.TransportModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class TransportRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val transportCollection = firestore.collection("transports")

    // Fórmula Haversine para calcular distancia entre dos coordenadas
    fun calculateDistance(origin: LatLng, destination: LatLng): Double {
        val earthRadiusKm = 6371

        val dLat = Math.toRadians(destination.latitude - origin.latitude)
        val dLon = Math.toRadians(destination.longitude - origin.longitude)

        val lat1 = Math.toRadians(origin.latitude)
        val lat2 = Math.toRadians(destination.latitude)

        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c // devuelve distancia en km
    }

    // ✅ CORREGIDO: Guardar registro de transporte en Firebase
    suspend fun saveTransportRecord(
        origin: LatLng,
        destination: LatLng,
        distanceKm: Double,
        notes: String = ""
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )

            // ✅ Crear un Map en lugar de usar TransportModel directamente
            val transportData = hashMapOf(
                "userId" to userId,
                "originLat" to origin.latitude,
                "originLng" to origin.longitude,
                "destinationLat" to destination.latitude,
                "destinationLng" to destination.longitude,
                "distanceKm" to distanceKm,
                "timestamp" to System.currentTimeMillis(),
                "notes" to notes
            )

            // ✅ Usar .add() sin ID personalizado - Firestore genera el ID automáticamente
            val documentRef = transportCollection.add(transportData).await()

            android.util.Log.d("TRANSPORT", "✅ Ruta guardada con ID: ${documentRef.id}")
            Result.success(documentRef.id)
        } catch (e: Exception) {
            android.util.Log.e("TRANSPORT", "❌ Error al guardar: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Obtener todos los registros del usuario
    suspend fun getUserTransportRecords(): Result<List<TransportModel>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )

            val snapshot = transportCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val records = snapshot.documents.mapNotNull { doc ->
                try {
                    TransportModel(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        originLat = doc.getDouble("originLat") ?: 0.0,
                        originLng = doc.getDouble("originLng") ?: 0.0,
                        destinationLat = doc.getDouble("destinationLat") ?: 0.0,
                        destinationLng = doc.getDouble("destinationLng") ?: 0.0,
                        distanceKm = doc.getDouble("distanceKm") ?: 0.0,
                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                        notes = doc.getString("notes") ?: ""
                    )
                } catch (e: Exception) {
                    android.util.Log.e("TRANSPORT", "Error al parsear documento: ${e.message}")
                    null
                }
            }

            android.util.Log.d("TRANSPORT", "✅ Registros cargados: ${records.size}")
            Result.success(records)
        } catch (e: Exception) {
            android.util.Log.e("TRANSPORT", "❌ Error al cargar registros: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Eliminar registro
    suspend fun deleteTransportRecord(recordId: String): Result<Unit> {
        return try {
            transportCollection.document(recordId).delete().await()
            android.util.Log.d("TRANSPORT", "✅ Registro eliminado: $recordId")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("TRANSPORT", "❌ Error al eliminar: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Obtener estadísticas del usuario (distancia total)
    suspend fun getTotalDistance(): Result<Double> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )

            val snapshot = transportCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val totalDistance = snapshot.documents.sumOf { doc ->
                doc.getDouble("distanceKm") ?: 0.0
            }

            android.util.Log.d("TRANSPORT", "✅ Distancia total: $totalDistance km")
            Result.success(totalDistance)
        } catch (e: Exception) {
            android.util.Log.e("TRANSPORT", "❌ Error al calcular distancia total: ${e.message}", e)
            Result.failure(e)
        }
    }
}