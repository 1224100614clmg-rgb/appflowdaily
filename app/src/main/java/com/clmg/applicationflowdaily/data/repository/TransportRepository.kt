package com.clmg.applicationflowdaily.data.repository

import com.clmg.applicationflowdaily.data.models.TransportModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.math.*

data class RouteInfo(
    val distanceKm: Double,
    val durationMinutes: Int,
    val polylinePoints: List<LatLng>
)

class TransportRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val transportCollection = firestore.collection("transports")

    // ✅ TU API KEY DE GOOGLE CLOUD
    private val GOOGLE_MAPS_API_KEY = "AIzaSyC_JbOBVVw-UFB2M1iAk6RpGMsmt6ncqa0"

    // ✅ Calcular distancia y tiempo REAL usando Google Directions API
    suspend fun calculateRealRoute(origin: LatLng, destination: LatLng): Result<RouteInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val originStr = "${origin.latitude},${origin.longitude}"
                val destStr = "${destination.latitude},${destination.longitude}"

                val urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=$originStr" +
                        "&destination=$destStr" +
                        "&mode=driving" +
                        "&key=$GOOGLE_MAPS_API_KEY"

                android.util.Log.d("TRANSPORT", "🌐 Llamando a Directions API...")

                val response = URL(urlString).readText()
                val json = JSONObject(response)

                val status = json.getString("status")

                if (status == "OK") {
                    val routes = json.getJSONArray("routes")
                    val route = routes.getJSONObject(0)
                    val legs = route.getJSONArray("legs")
                    val leg = legs.getJSONObject(0)

                    // Obtener distancia en kilómetros
                    val distanceMeters = leg.getJSONObject("distance").getInt("value")
                    val distanceKm = distanceMeters / 1000.0

                    // Obtener duración en minutos
                    val durationSeconds = leg.getJSONObject("duration").getInt("value")
                    val durationMinutes = (durationSeconds / 60.0).toInt()

                    // Decodificar la polilínea (puntos de la ruta)
                    val overviewPolyline = route.getJSONObject("overview_polyline").getString("points")
                    val polylinePoints = decodePolyline(overviewPolyline)

                    android.util.Log.d("TRANSPORT", "✅ Ruta calculada: $distanceKm km, $durationMinutes min")

                    Result.success(RouteInfo(distanceKm, durationMinutes, polylinePoints))
                } else {
                    val errorMessage = json.optString("error_message", "Error: $status")
                    android.util.Log.e("TRANSPORT", "❌ Error API: $errorMessage")
                    Result.failure(Exception("Error de Google Maps: $errorMessage"))
                }
            } catch (e: Exception) {
                android.util.Log.e("TRANSPORT", "❌ Error al calcular ruta: ${e.message}", e)
                Result.failure(e)
            }
        }



    }

    // ✅ Decodificar polilínea de Google Maps
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(latLng)
        }

        return poly
    }

    // Fórmula Haversine para distancia en línea recta (backup)
    fun calculateDistance(origin: LatLng, destination: LatLng): Double {
        val earthRadiusKm = 6371

        val dLat = Math.toRadians(destination.latitude - origin.latitude)
        val dLon = Math.toRadians(destination.longitude - origin.longitude)

        val lat1 = Math.toRadians(origin.latitude)
        val lat2 = Math.toRadians(destination.latitude)

        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }

    // ✅ Guardar registro de transporte en Firebase
    suspend fun saveTransportRecord(
        origin: LatLng,
        destination: LatLng,
        distanceKm: Double,
        durationMinutes: Int,
        notes: String = ""
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )

            val transportData = hashMapOf(
                "userId" to userId,
                "originLat" to origin.latitude,
                "originLng" to origin.longitude,
                "destinationLat" to destination.latitude,
                "destinationLng" to destination.longitude,
                "distanceKm" to distanceKm,
                "durationMinutes" to durationMinutes,
                "timestamp" to System.currentTimeMillis(),
                "notes" to notes
            )

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
                        durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
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