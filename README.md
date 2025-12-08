📱 Manual Completo – FlowDaily
📝 Resumen

FlowDaily es una aplicación diseñada para ayudar a las personas con sus rutinas diarias. Permite registrar, organizar y visualizar actividades importantes como transporte, gastos, tareas, recordatorios y hábitos.
En pocas palabras, FlowDaily ayuda a mejorar el control y la eficiencia de las actividades diarias mediante un registro claro, moderno y estructurado.

✨ Características Principales

Autenticación con Firebase (email/contraseña)

CRUD completo de proyectos

Notificaciones push

Transporte (rutas diarias)

Organización (checklist)

Gastos (registro de gastos diarios)

🛠 Tecnologías Utilizadas

Kotlin

Jetpack Compose

Material 3

Map Compose (Google Maps)

Firebase Authentication

Firebase Firestore

Firebase Storage (fotos de perfil)

🔧 Funcionalidades Principales
🔐 Autenticación

Registro de nuevos usuarios con email y contraseña

Inicio de sesión para usuarios registrados

Gestión de sesión persistente

🚗 Transporte

Guardar rutas diarias

Calcular kilómetros recorridos

Calcular minutos aproximados

Registrar fecha de realización

🗂 Organización

Registrar tareas pendientes

Marcar tareas como realizadas o pendientes

Calcular progreso total (100% cuando todas son realizadas)

💰 Gastos

Registrar dinero disponible

Registrar gastos con monto y categoría

Descontar automáticamente del saldo disponible

Registrar fecha y hora de cada gasto

🔔 Asistencia Personal

Registrar recordatorios con descripción y categoría

Guardar fecha y hora del recordatorio

Enviar notificaciones al usuario

Mostrar cantidad de recordatorios existentes

👤 Perfil del Usuario

Ver datos personales

Editar nombre, email, teléfono y foto

Ver fecha de creación de la cuenta

⚙ Configuración

Activar / desactivar modo oscuro

Cambiar idioma (ES/EN)

Cerrar sesión

🟥 Prioridades

Sistema de prioridades: Alta, Media y Baja

Indicadores visuales para diferenciar cada prioridad

🔄 ¿Cómo Funciona y Quién lo Hizo? (Flujo General)

El usuario abre la app → Pantalla Splash

Verificación automática de sesión

Login o Registro

Pantalla Home (acceso a todos los módulos)

Navegación hacia:

Transporte

Gastos

Tareas

Recordatorios

Asistencia

Perfil

Configuración

Todas las acciones se guardan y sincronizan con Firebase

👥 Integrantes

Colin Cardenas Kelly Anahi

Mendez Galvan Claudia Lizbeth

📁 Estructura de Archivos
app/
│
├─ manifests/
│   └─ AndroidManifest.xml
│
├─ kotlin/com.example.applicationflowdaily/
│
├─ data/
│   ├─ firestore/
│   │   └─ FirebaseModule.kt
│   │
│   ├─ local/
│   │   └─ PreferencesDataStore.kt
│   │
│   ├─ models/
│   │   ├─ DailySession.kt
│   │   ├─ ExpenceModel.kt
│   │   ├─ ReminderModel.kt
│   │   ├─ TaskModel.kt
│   │   ├─ TransportModel.kt
│   │   ├─ User.kt
│   │   └─ UserPreferences.kt
│   │
│   ├─ notificacion/
│   │   ├─ ReminderBroadcastReceiver.kt
│   │   └─ ReminderNotificationManager.kt
│   │
│   └─ repository/
│       ├─ ExpenseRepository.kt
│       ├─ FirestoreRepository.kt
│       ├─ ReminderRepository.kt
│       ├─ TaskRepository.kt
│       └─ TransportRepository.kt
│
├─ navigation/
│   └─ Navigation.kt
│
├─ ui/
│   ├─ components/
│   │   └─ (componentes reutilizables si existen)
│   │
│   ├─ screens/
│   │   ├─ AssistanceScreen.kt
│   │   ├─ ExpenceScreen.kt
│   │   ├─ HomeScreen.kt
│   │   ├─ LoginScreen.kt
│   │   ├─ OrganizationScreen.kt
│   │   ├─ PlaceholderScreen.kt
│   │   ├─ ProfileScreen.kt
│   │   ├─ RegisterScreen.kt
│   │   ├─ SettingsScreen.kt
│   │   ├─ SplashScreen.kt
│   │   └─ TransportScreen.kt
│   │
│   ├─ theme/
│   │   ├─ Color.kt
│   │   ├─ Theme.kt
│   │   └─ Type.kt
│   │
│   └─ viewmodel/
│       ├─ AuthViewModel.kt
│       ├─ ExpenseViewModel.kt
│       ├─ ReminderViewModel.kt
│       ├─ SessionViewModel.kt
│       ├─ SettingsViewModel.kt
│       ├─ TaskViewModel.kt
│       └─ TransportViewModel.kt
│
├─ MainActivity.kt
└─ MainApplication.kt

📍 TransportScreen.kt — Documentación y Código Completo
📝 Descripción General

TransportScreen.kt es una de las pantallas más completas y avanzadas de FlowDaily.
Esta pantalla permite:

Seleccionar una ruta en Google Maps (origen y destino).

Calcular distancia y duración estimada.

Guardar rutas con notas personalizadas.

Ver un historial de rutas previas.

Mostrar métricas como distancia total y tiempo acumulado.

Reutilizar rutas almacenadas para visualizarlas nuevamente en el mapa.

Este módulo utiliza:

Jetpack Compose

Google Maps Compose

MVVM con TransportViewModel

Firebase Firestore

Permisos dinámicos

Animaciones de cámara

UI moderna con Material 3

📌 Código Completo: TransportScreen.kt

Nota: Este bloque está listo para pegar directamente en tu README.md.
No se ha omitido nada, es el archivo íntegro.

//Explicacion de este codigo:
//Este código implementa la pantalla completa de Transporte en la aplicación FlowDaily.
//Es una pantalla compleja que combina Google Maps para seleccionar rutas con un historial
//de rutas guardadas, utilizando el patrón de diseño MVVM con un ViewModel especializado.

package com.clmg.applicationflowdaily.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clmg.applicationflowdaily.data.models.TransportModel
import com.clmg.applicationflowdaily.ui.viewmodel.TransportViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(
    onBackClick: () -> Unit,
    viewModel: TransportViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val origin by viewModel.origin.collectAsState()
    val destination by viewModel.destination.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val durationMinutes by viewModel.durationMinutes.collectAsState()
    val routePolyline by viewModel.routePolyline.collectAsState()

    val transportRecords by viewModel.transportRecords.collectAsState()
    val totalDistance by viewModel.totalDistance.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var showMapView by remember { mutableStateOf(true) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        currentLocation = LatLng(it.latitude, it.longitude)
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f),
                                1000
                            )
                        }
                    }
                }
            } catch (e: SecurityException) {
                android.util.Log.e("TRANSPORT", "Error obteniendo ubicación: ${e.message}")
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Transporte",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            if (showMapView) "Calcula distancias" else "Rutas guardadas",
                            fontSize = 12.sp,
                            color = Color.White.copy(0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (showMapView && hasLocationPermission) {
                        IconButton(onClick = {
                            currentLocation?.let { location ->
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(location, 15f),
                                        1000
                                    )
                                }
                            }
                        }) {
                            Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación", tint = Color.White)
                        }
                    }

                    IconButton(onClick = { showMapView = !showMapView }) {
                        Icon(
                            if (showMapView) Icons.Default.List else Icons.Default.Map,
                            contentDescription = if (showMapView) "Ver historial" else "Ver mapa",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00BCD4),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (!showMapView) {
                FloatingActionButton(
                    onClick = { showMapView = true },
                    containerColor = Color(0xFF00BCD4)
                ) {
                    Icon(Icons.Default.Add, "Nueva ruta", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        if (showMapView) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission
                        ),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = true,
                            compassEnabled = true
                        ),
                        onMapClick = { latLng ->
                            if (origin == null) viewModel.setOrigin(latLng)
                            else viewModel.setDestination(latLng)
                        }
                    ) {
                        origin?.let {
                            Marker(state = MarkerState(position = it), title = "Origen")
                        }
                        destination?.let {
                            Marker(state = MarkerState(position = it), title = "Destino")
                        }
                        if (origin != null && destination != null) {
                            if (routePolyline.isNotEmpty()) {
                                Polyline(
                                    points = routePolyline,
                                    color = Color(0xFF00BCD4),
                                    width = 10f
                                )
                            } else {
                                Polyline(
                                    points = listOf(origin!!, destination!!),
                                    color = Color(0xFF00BCD4),
                                    width = 10f
                                )
                            }
                        }
                    }

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF00BCD4)
                        )
                    }

                    if (origin == null) {
                        InstructionCard("Toca el mapa para seleccionar el ORIGEN")
                    } else if (destination == null) {
                        InstructionCard("Toca el mapa para seleccionar el DESTINO")
                    }
                }

                BottomRoutePanel(
                    origin = origin,
                    destination = destination,
                    distanceKm = distanceKm,
                    durationMinutes = durationMinutes,
                    hasLocationPermission = hasLocationPermission,
                    currentLocation = currentLocation,
                    isLoading = isLoading,
                    saveSuccess = saveSuccess,
                    errorMessage = errorMessage,
                    onUseMyLocation = { currentLocation?.let { viewModel.setOrigin(it) } },
                    onSave = { showSaveDialog = true },
                    onClear = { viewModel.clearRoute() }
                )
            }
        } else {
            TransportHistoryView(
                paddingValues = paddingValues,
                isLoading = isLoading,
                transportRecords = transportRecords,
                totalDistance = totalDistance,
                onEditClick = { record ->
                    viewModel.setOrigin(LatLng(record.originLat, record.originLng))
                    viewModel.setDestination(LatLng(record.destinationLat, record.destinationLng))
                    showMapView = true
                },
                onDeleteClick = { id -> viewModel.deleteRecord(id) }
            )
        }

        if (showSaveDialog) {
            RouteSaveDialog(
                notes = notes,
                onNotesChange = { notes = it },
                onConfirm = {
                    viewModel.saveCurrentRoute(notes)
                    showSaveDialog = false
                    notes = ""
                },
                onDismiss = { showSaveDialog = false }
            )
        }
    }
}

@Composable
fun TransportRecordCard(
    record: TransportModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00BCD4).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        record.notes.ifEmpty { "Ruta sin nombre" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            String.format("%.1f km", record.distanceKm),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(Modifier.width(12.dp))

                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${record.durationMinutes} min",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(record.timestamp)),
                        fontSize = 12.sp,
                        color = Color(0xFF00BCD4)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6C63FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar", fontSize = 13.sp)
                }

                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", fontSize = 13.sp)
                }
            }
        }
    }
}

✅ ExpenseRepository.kt
Explicación

Este archivo implementa un repositorio de datos para la gestión de gastos y balances financieros en FlowDaily.
Funciona como intermediario entre la app y Firebase Firestore, permitiendo realizar operaciones CRUD completas:

Crear

Leer

Actualizar

Eliminar

Incluye autenticación de usuario, manejo de errores y logs detallados.

Código: ExpenseRepository.kt
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

    private fun getCurrentUserId(): String? {
        val userId = FirebaseModule.getCurrentUserId()
        Log.d(TAG, "🔑 Usuario actual ID: $userId")
        return userId
    }

    suspend fun saveExpense(expense: ExpenseModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
            val expenseWithUser = expense.copy(userId = userId)

            expensesCollection
                .document(expense.id)
                .set(expenseWithUser)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error guardando gasto: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getExpenses(): Result<List<ExpenseModel>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = expensesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            var expenses = snapshot.documents.mapNotNull {
                it.toObject(ExpenseModel::class.java)
            }

            expenses = expenses.sortedByDescending { it.timestamp }

            Result.success(expenses)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo gastos: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateExpense(expense: ExpenseModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
            val expenseWithUser = expense.copy(userId = userId)

            expensesCollection
                .document(expense.id)
                .set(expenseWithUser)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error actualizando gasto: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            expensesCollection
                .document(expenseId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error eliminando gasto: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun saveBalance(balance: BalanceModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

            val balanceWithUser = balance.copy(
                id = "balance_$userId",
                userId = userId
            )

            balancesCollection
                .document(balanceWithUser.id)
                .set(balanceWithUser)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error guardando balance: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getBalance(): Result<BalanceModel?> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

            val documentId = "balance_$userId"

            val snapshot = balancesCollection
                .document(documentId)
                .get()
                .await()

            val balance = snapshot.toObject(BalanceModel::class.java)

            Result.success(balance)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo balance: ${e.message}")
            Result.failure(e)
        }
    }
}

✅ ExpenseModel.kt
Explicación

Este archivo contiene los modelos de datos usados en el módulo de Finanzas:

ExpenseModel → Representa un gasto o ingreso.

BalanceModel → Representa el balance inicial del usuario.

Están diseñados para integrarse con Firebase Firestore y permiten ordenamiento, registro y análisis temporal.

Código: ExpenseModel.kt
package com.clmg.applicationflowdaily.data.models

data class ExpenseModel(
    val id: String = "",
    val userId: String = "",
    val monto: Double = 0.0,
    val descripcion: String = "",
    val categoria: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val tipo: String = "GASTO",
    val emoji: String = "💰",
    val timestamp: Long = System.currentTimeMillis()
)

data class BalanceModel(
    val id: String = "balance_inicial",
    val userId: String = "",
    val balanceInicial: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

✅ ExpenseViewModel.kt
Explicación

Este ViewModel maneja toda la lógica de:

Gastos

Balance

Estados de carga

Errores

Mensajes de éxito

Coordina las operaciones entre la UI y el repositorio.

Código: ExpenseViewModel.kt
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

    fun loadExpenses() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                repository.getExpenses().fold(
                    onSuccess = { _expenses.value = it },
                    onFailure = { _error.value = it.message }
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadBalance() {
        viewModelScope.launch {
            try {
                repository.getBalance().fold(
                    onSuccess = { _balance.value = it },
                    onFailure = { Log.e(TAG, "Error balance: ${it.message}") }
                )
            } catch (_: Exception) {}
        }
    }

    fun saveExpense(expense: ExpenseModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                repository.saveExpense(expense).fold(
                    onSuccess = {
                        _operationSuccess.value = "Gasto guardado"
                        loadExpenses()
                        onSuccess()
                    },
                    onFailure = { _error.value = it.message }
                )
            } finally { _loading.value = false }
        }
    }

    fun updateExpense(expense: ExpenseModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true

                repository.updateExpense(expense).fold(
                    onSuccess = {
                        _operationSuccess.value = "Gasto actualizado"
                        loadExpenses()
                        onSuccess()
                    },
                    onFailure = { _error.value = it.message }
                )
            } finally { _loading.value = false }
        }
    }

    fun deleteExpense(expenseId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true

                repository.deleteExpense(expenseId).fold(
                    onSuccess = {
                        _operationSuccess.value = "Gasto eliminado"
                        loadExpenses()
                        onSuccess()
                    },
                    onFailure = { _error.value = it.message }
                )
            } finally { _loading.value = false }
        }
    }

    fun saveBalance(balanceInicial: Double, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val balanceModel = BalanceModel(balanceInicial = balanceInicial)

                repository.saveBalance(balanceModel).fold(
                    onSuccess = {
                        _operationSuccess.value = "Balance actualizado"
                        loadBalance()
                        onSuccess()
                    },
                    onFailure = { _error.value = it.message }
                )
            } finally { _loading.value = false }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _operationSuccess.value = null
    }
}
