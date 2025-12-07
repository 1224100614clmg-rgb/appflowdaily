package com.clmg.applicationflowdaily.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clmg.applicationflowdaily.data.models.TransportModel
import com.clmg.applicationflowdaily.data.repository.TransportRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransportViewModel(
    private val repository: TransportRepository = TransportRepository()
) : ViewModel() {

    private val _origin = MutableStateFlow<LatLng?>(null)
    val origin: StateFlow<LatLng?> = _origin

    private val _destination = MutableStateFlow<LatLng?>(null)
    val destination: StateFlow<LatLng?> = _destination

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm

    private val _durationMinutes = MutableStateFlow(0)
    val durationMinutes: StateFlow<Int> = _durationMinutes

    private val _routePolyline = MutableStateFlow<List<LatLng>>(emptyList())
    val routePolyline: StateFlow<List<LatLng>> = _routePolyline

    private val _transportRecords = MutableStateFlow<List<TransportModel>>(emptyList())
    val transportRecords: StateFlow<List<TransportModel>> = _transportRecords

    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance: StateFlow<Double> = _totalDistance

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess



    init {
        loadUserRecords()
        loadTotalDistance()
    }

    fun setOrigin(point: LatLng) {
        _origin.value = point
        calculateRealRoute()
    }

    fun setDestination(point: LatLng) {
        _destination.value = point
        calculateRealRoute()
    }

    // ✅ Calcular ruta REAL usando Google Directions API
    private fun calculateRealRoute() {
        val o = _origin.value
        val d = _destination.value

        if (o != null && d != null) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null

                val result = repository.calculateRealRoute(o, d)

                result.onSuccess { routeInfo ->
                    _distanceKm.value = routeInfo.distanceKm
                    _durationMinutes.value = routeInfo.durationMinutes
                    _routePolyline.value = routeInfo.polylinePoints
                    android.util.Log.d("TRANSPORT", "✅ Ruta calculada: ${routeInfo.distanceKm} km, ${routeInfo.durationMinutes} min")
                }.onFailure { exception ->
                    _errorMessage.value = "Error al calcular ruta: ${exception.message}"
                    // Fallback a cálculo lineal
                    _distanceKm.value = repository.calculateDistance(o, d)
                    _durationMinutes.value = (_distanceKm.value / 40.0 * 60).toInt()
                    _routePolyline.value = emptyList()
                    android.util.Log.e("TRANSPORT", "❌ Usando cálculo lineal como fallback")
                }

                _isLoading.value = false
            }
        }
    }

    fun saveCurrentRoute(notes: String = "") {
        val o = _origin.value
        val d = _destination.value

        if (o == null || d == null) {
            _errorMessage.value = "Debes seleccionar origen y destino"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.saveTransportRecord(
                origin = o,
                destination = d,
                distanceKm = _distanceKm.value,
                durationMinutes = _durationMinutes.value,
                notes = notes
            )

            result.onSuccess {
                _saveSuccess.value = true
                loadUserRecords()
                loadTotalDistance()
                clearRoute()
            }.onFailure { exception ->
                _errorMessage.value = "Error al guardar: ${exception.message}"
            }

            _isLoading.value = false
        }
    }

    fun loadUserRecords() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getUserTransportRecords()

            result.onSuccess { records ->
                _transportRecords.value = records
            }.onFailure { exception ->
                _errorMessage.value = "Error al cargar registros: ${exception.message}"
            }

            _isLoading.value = false
        }
    }

    fun loadTotalDistance() {
        viewModelScope.launch {
            val result = repository.getTotalDistance()

            result.onSuccess { total ->
                _totalDistance.value = total
            }.onFailure { exception ->
                _errorMessage.value = "Error al cargar distancia total: ${exception.message}"
            }
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            val result = repository.deleteTransportRecord(recordId)

            result.onSuccess {
                loadUserRecords()
                loadTotalDistance()
            }.onFailure { exception ->
                _errorMessage.value = "Error al eliminar: ${exception.message}"
            }
        }
    }

    fun clearRoute() {
        _origin.value = null
        _destination.value = null
        _distanceKm.value = 0.0
        _durationMinutes.value = 0
        _routePolyline.value = emptyList()
    }

    fun clearSaveSuccess() {
        _saveSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}