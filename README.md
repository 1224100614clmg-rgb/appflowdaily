# 📱 Manual Completo – FlowDaily

## 📝 Resumen
FlowDaily es una aplicación diseñada para ayudar a las personas con sus rutinas diarias.  
Permite registrar, organizar y visualizar actividades importantes como transporte, gastos, tareas, recordatorios y hábitos.  

En pocas palabras, FlowDaily ayuda a mejorar el control y la eficiencia de las actividades diarias mediante un registro claro, moderno y estructurado.

---

## ✨ Características Principales
- Autenticación con Firebase (email/contraseña)  
- CRUD completo de proyectos  
- Notificaciones push  
- Transporte (rutas diarias)  
- Organización (checklist)  
- Gastos (registro de gastos diarios)  

---

## 🛠 Tecnologías Utilizadas
- Kotlin  
- Jetpack Compose  
- Material 3  
- Map Compose (Google Maps)  
- Firebase Authentication  
- Firebase Firestore  
- Firebase Storage (fotos de perfil)  

---

## 🔧 Funcionalidades Principales

### 🔐 Autenticación
- Registro de nuevos usuarios con email y contraseña  
- Inicio de sesión para usuarios registrados  
- Gestión de sesión persistente  

### 🚗 Transporte
- Guardar rutas diarias  
- Calcular kilómetros recorridos  
- Calcular minutos aproximados  
- Registrar fecha de realización  

### 🗂 Organización
- Registrar tareas pendientes  
- Marcar tareas como realizadas o pendientes  
- Calcular progreso total (100% cuando todas son realizadas)  

### 💰 Gastos
- Registrar dinero disponible  
- Registrar gastos con monto y categoría  
- Descontar automáticamente del saldo disponible  
- Registrar fecha y hora de cada gasto  

### 🔔 Asistencia Personal
- Registrar recordatorios con descripción y categoría  
- Guardar fecha y hora del recordatorio  
- Enviar notificaciones al usuario  
- Mostrar cantidad de recordatorios existentes  

### 👤 Perfil del Usuario
- Ver datos personales  
- Editar nombre, email, teléfono y foto  
- Ver fecha de creación de la cuenta  

### ⚙ Configuración
- Activar / desactivar modo oscuro  
- Cambiar idioma (ES/EN)  
- Cerrar sesión  

### 🟥 Prioridades
- Sistema de prioridades: Alta, Media y Baja  
- Indicadores visuales para diferenciar cada prioridad  

---

## 🔄 ¿Cómo Funciona y Quién lo Hizo? (Flujo General)
1. El usuario abre la app → Pantalla Splash  
2. Verificación automática de sesión  
3. Login o Registro  
4. Pantalla Home (acceso a todos los módulos)  
5. Navegación hacia:  
   - Transporte  
   - Gastos  
   - Tareas  
   - Recordatorios  
   - Asistencia  
   - Perfil  
   - Configuración  
6. Todas las acciones se guardan y sincronizan con Firebase  

---

## 👥 Integrantes
- Colin Cardenas Kelly Anahi  
- Mendez Galvan Claudia Lizbeth  

---
# 🎥 Video de YouTube – FlowDaily

## 📌 Información
Este repositorio incluye un recurso audiovisual relacionado con **FlowDaily**.  
El video explica y muestra el funcionamiento general de la aplicación, sus características principales y cómo utilizarla de manera eficiente.

---

## 🔗 Enlace al Video
Puedes ver el contenido completo en YouTube aquí:  
[👉 Ver Video](https://youtu.be/721Orkad5Tk?si=regTNXJFYAsZ2__E)

---
# 📘 Manual Completo – FlowDaily

## 📌 Información
Este repositorio incluye el **Manual Completo** de la aplicación **FlowDaily**.  
El manual contiene la documentación detallada sobre el uso de la app, sus módulos principales y las instrucciones necesarias para aprovechar todas sus funcionalidades.

---

## 🔗 Enlace al Manual
Puedes acceder al documento completo en Google Drive aquí:  
[👉 Ver Manual Completo](https://drive.google.com/drive/folders/1lipfKdzfAjJWduT0_KrKS_duBAIE3-t1?usp=sharing)


---

## 📁 Estructura de Archivos
```bash
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
# ✅ ExpenseViewModel.kt

## 📌 Explicación
Este **ViewModel** maneja toda la lógica relacionada con:

- 💰 Gastos  
- 💵 Balance  
- ⏳ Estados de carga  
- ⚠️ Errores  
- ✅ Mensajes de éxito  

Su función principal es **coordinar las operaciones entre la UI y el repositorio**, asegurando que los datos se gestionen de manera reactiva y eficiente.

---

## 🛠 Funcionalidades Clave
- Cargar lista de gastos desde el repositorio.  
- Cargar balance actual del usuario.  
- Guardar, actualizar y eliminar gastos.  
- Guardar balance inicial o actualizado.  
- Manejo de estados de carga (`loading`).  
- Manejo de errores (`error`).  
- Mensajes de éxito (`operationSuccess`).  
- Limpieza de estados (`clearError`, `clearSuccess`).  

---

## 📂 Código: `ExpenseViewModel.kt`

```kotlin
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

# ✅ ExpenseModel.kt

## 📌 Explicación
Este archivo contiene los **modelos de datos** usados en el módulo de **Finanzas**:

- **ExpenseModel** → Representa un gasto o ingreso.  
- **BalanceModel** → Representa el balance inicial del usuario.  

Ambos modelos están diseñados para integrarse con **Firebase Firestore** y permiten:  
- 📊 Ordenamiento de registros.  
- 📝 Registro de gastos/ingresos.  
- ⏱️ Análisis temporal mediante `timestamp` y `fecha`.  

---

## 🛠 Funcionalidades Clave
- **ExpenseModel**  
  - ID único del gasto.  
  - Usuario asociado (`userId`).  
  - Monto y descripción.  
  - Categoría del gasto.  
  - Fecha y hora del registro.  
  - Tipo de movimiento (`GASTO` o `INGRESO`).  
  - Emoji representativo.  
  - Timestamp para ordenamiento temporal.  

- **BalanceModel**  
  - ID fijo (`balance_inicial`).  
  - Usuario asociado (`userId`).  
  - Balance inicial del usuario.  
  - Timestamp para control histórico.  

---

## 📂 Código: `ExpenseModel.kt`

```kotlin
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

# ✅ ExpenseRepository.kt

## 📌 Explicación
Este archivo implementa un **repositorio de datos** para la gestión de **gastos y balances financieros** en **FlowDaily**.  
Funciona como **intermediario entre la app y Firebase Firestore**, permitiendo realizar operaciones CRUD completas:

- ➕ Crear  
- 📖 Leer  
- ✏️ Actualizar  
- 🗑️ Eliminar  

Además, incluye:  
- 🔐 Autenticación de usuario  
- ⚠️ Manejo de errores  
- 📝 Logs detallados para depuración  

---

## 🛠 Funcionalidades Clave
- Guardar nuevos gastos en Firestore.  
- Obtener lista de gastos del usuario autenticado.  
- Actualizar gastos existentes.  
- Eliminar gastos por ID.  
- Guardar balance inicial o actualizado.  
- Obtener balance actual del usuario.  
- Manejo de excepciones con `Result` para mayor robustez.  
- Logs (`Log.d`, `Log.e`) para seguimiento de operaciones.  

---

## 📂 Código: `ExpenseRepository.kt`

```kotlin
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
---

##📌 TransportScreen.kt

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

Centraliza la configuración de Firebase en un único módulo (FirebaseModule) que:
- Inicializa Firestore y Auth con instancias únicas (patrón singleton).

package com.clmg.applicationflowdaily.data.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// FirebaseModule que centraliza y gestiona las instancias de Firebase:
// > Proporciona acceso global y lazy a FirebaseFirestore y FirebaseAuth
// > Implementa patrón singleton para instancias únicas en toda la app
// > Incluye funciones helper para obtener información del usuario autenticado
// > Agrega logging detallado para diagnóstico y debugging
// > Sirve como punto único de configuración para servicios de Firebase

object FirebaseModule {

    private const val TAG = "FirebaseModule"

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            Log.d(TAG, "✅ Firestore inicializado")
        }
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance().apply {
            Log.d(TAG, "✅ Auth inicializado")
        }
    }

    fun getCurrentUserId(): String? {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        Log.d(TAG, "=================================")
        Log.d(TAG, "🔑 getCurrentUserId() llamado")
        Log.d(TAG, "🔑 FirebaseAuth.currentUser: $currentUser")
        Log.d(TAG, "🔑 User ID: $userId")
        Log.d(TAG, "🔑 Email: ${currentUser?.email}")
        Log.d(TAG, "🔑 Autenticado: ${userId != null}")
        Log.d(TAG, "=================================")

        return userId
    }

    fun isUserAuthenticated(): Boolean {
        val isAuth = auth.currentUser != null
        Log.d(TAG, "✅ Usuario autenticado: $isAuth")
        return isAuth
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}

Crea una clase (PreferencesDataStore) que maneje el almacenamiento local de preferencias de usuario usando Jetpack DataStore

package com.clmg.applicationflowdaily.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clmg.applicationflowdaily.data.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Clase PreferencesDataStore que maneja el almacenamiento local de preferencias de usuario
// usando Jetpack DataStore:
// > Implementa un sistema de persistencia local para configuraciones de la aplicación
// > Usa DataStore para almacenamiento tipo clave-valor
// > Almacena preferencias como UserPreferences (modo oscuro e idioma)
// > Proporciona flujos reactivos (Flow) para observar cambios en tiempo real
// > Diseñada para trabajar offline y sincronizar con Firebase opcionalmente

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesDataStore(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    // Leer preferencias como Flow
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                isDarkMode = preferences[DARK_MODE_KEY] ?: false,
                language = preferences[LANGUAGE_KEY] ?: "es"
            )
        }

    // Guardar modo oscuro
    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    // Guardar idioma
    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}

Define un modelo de datos (DailySession) que represente una sesión diaria o registro de actividad, con las siguientes características:
package com.clmg.applicationflowdaily.data.models

// Modelo de datos DailySession que representa una sesión diaria o registro de actividad:
// > Estructura simple de datos para almacenar información de sesiones/actividades diarias
// > Pensado para ser usado con Firebase Firestore (por el campo id vacío por defecto)
// > Almacena información básica: título, descripción y fecha de la sesión
// > Usa timestamp en milisegundos para la fecha para facilitar ordenamiento y consultas

data class DailySession(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis()
)

Define un modelo de datos (ReminderModel) que represente un recordatorio o tarea programada
package com.clmg.applicationflowdaily.data.models

// Modelo de datos ReminderModel que representa un recordatorio o tarea programada:
// > Estructura para gestionar recordatorios con fechas, horas y notificaciones
// > Diseñado para integración con Firebase Firestore y sistema de notificaciones
// > Incluye campos para categorización, activación de notificaciones y estado de completado
// > Pensado para usar en conjunto con AlarmManager/WorkManager para notificaciones push

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

📌 Instrucción
Implementa un BroadcastReceiver (ReminderBroadcastReceiver) para gestionar el ciclo completo de notificaciones programadas
package com.clmg.applicationflowdaily.data.notificacion

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clmg.applicationflowdaily.R
import com.clmg.applicationflowdaily.MainActivity
import java.text.SimpleDateFormat
import java.util.*

// > Recibe y procesa notificaciones programadas del sistema
// > Implementa acciones interactivas (Posponer 5 min, Aceptar)
// > Reprograma recordatorios cuando se posponen
// > Gestiona el ciclo completo de notificaciones: mostrar, posponer, aceptar
// > Integra con ReminderNotificationManager para reprogramación

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderBroadcast"
        private const val CHANNEL_ID = "reminder_channel"
        const val ACTION_POSTPONE = "com.clmg.applicationflowdaily.ACTION_POSTPONE"
        const val ACTION_ACCEPT = "com.clmg.applicationflowdaily.ACTION_ACCEPT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Notificacion recibida - Action: ${intent.action}")

        val reminderId = intent.getStringExtra("reminderId") ?: return
        val title = intent.getStringExtra("title") ?: "Recordatorio"
        val description = intent.getStringExtra("description") ?: ""

        when (intent.action) {
            ACTION_POSTPONE -> {
                Log.d(TAG, "Posponiendo recordatorio: $reminderId")
                postponeReminder(context, reminderId, title, description)
            }
            ACTION_ACCEPT -> {
                Log.d(TAG, "Aceptando recordatorio: $reminderId")
                dismissNotification(context, reminderId)
            }
            else -> {
                // Primera notificacion - mostrar con botones
                showNotificationWithActions(context, reminderId, title, description)
            }
        }
    }

    /** Muestra la notificacion con botones Posponer/Aceptar */
    private fun showNotificationWithActions(
        context: Context,
        reminderId: String,
        title: String,
        description: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val postponeIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_POSTPONE
            putExtra("reminderId", reminderId)
            putExtra("title", title)
            putExtra("description", description)
        }
        val postponePendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode() + 1,
            postponeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val acceptIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_ACCEPT
            putExtra("reminderId", reminderId)
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode() + 2,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, "Posponer 5 min", postponePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Aceptar", acceptPendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(reminderId.hashCode(), notification)
        Log.d(TAG, "Notificacion mostrada: $title")
    }

    /** Pospone el recordatorio 5 minutos */
    private fun postponeReminder(
        context: Context,
        reminderId: String,
        title: String,
        description: String
    ) {
        val newCalendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }

        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

        val newDate = dateFormatter.format(newCalendar.time)
        val newTime = timeFormatter.format(newCalendar.time)

        Log.d(TAG, "Nueva fecha/hora: $newDate - $newTime")

        val notificationManager = ReminderNotificationManager(context)
        notificationManager.scheduleReminder(reminderId, title, description, newDate, newTime)

        dismissNotification(context, reminderId)
        Log.d(TAG, "Recordatorio pospuesto 5 minutos")
    }

    /** Cancela/cierra la notificacion */
    private fun dismissNotification(context: Context, reminderId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(reminderId.hashCode())
        Log.d(TAG, "Notificacion cerrada: $reminderId")
    }
}


Implementa un gestor de notificaciones (ReminderNotificationManager) que permita programar, actualizar y cancelar recordatorios

package com.clmg.applicationflowdaily.data.notificacion

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clmg.applicationflowdaily.R
import java.text.SimpleDateFormat
import java.util.*

// Este código implementa un gestor de notificaciones programadas (recordatorios) para Android.
// Permite crear, actualizar y cancelar notificaciones que se mostrarán en momentos específicos,
// utilizando el AlarmManager para programar las alarmas en segundo plano.

class ReminderNotificationManager(private val context: Context) {

    companion object {
        private const val TAG = "ReminderNotification"
        private const val CHANNEL_ID = "reminder_channel"
        private const val CHANNEL_NAME = "Recordatorios"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    /** Crea el canal de notificaciones (Android 8+) */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de recordatorios"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "✅ Canal de notificación creado")
        }
    }

    /** Programa un recordatorio */
    fun scheduleReminder(
        reminderId: String,
        title: String,
        description: String,
        dateStr: String,
        timeStr: String
    ) {
        try {
            val triggerTime = parseDateAndTime(dateStr, timeStr)

            if (triggerTime <= System.currentTimeMillis()) {
                Log.w(TAG, "⚠️ Fecha en el pasado, no se programa: $title")
                return
            }

            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("reminderId", reminderId)
                putExtra("title", title)
                putExtra("description", description)
                action = "com.clmg.applicationflowdaily.REMINDER_NOTIFICATION"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d(TAG, "✅ Notificación programada: $title - ${Date(triggerTime)}")
                } else {
                    Log.e(TAG, "❌ No hay permiso para alarmas exactas")
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d(TAG, "✅ Notificación programada: $title - ${Date(triggerTime)}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error programando notificación: ${e.message}", e)
        }
    }

    /** Actualiza un recordatorio (cancela el anterior y programa uno nuevo) */
    fun updateReminder(
        reminderId: String,
        title: String,
        description: String,
        dateStr: String,
        timeStr: String
    ) {
        cancelReminder(reminderId)
        scheduleReminder(reminderId, title, description, dateStr, timeStr)
    }

    /** Cancela un recordatorio programado */
    fun cancelReminder(reminderId: String) {
        try {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "✅ Notificación cancelada: $reminderId")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cancelando notificación: ${e.message}", e)
        }
    }

    /** Convierte fecha y hora en timestamp */
    private fun parseDateAndTime(dateStr: String, timeStr: String): Long {
        return try {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

            val parsedDate = dateFormatter.parse(dateStr)
            val parsedTime = timeFormatter.parse(timeStr)

            if (parsedDate != null && parsedTime != null) {
                val calendar = Calendar.getInstance().apply {
                    time = parsedDate
                    val timeCalendar = Calendar.getInstance().apply {
                        time = parsedTime
                    }
                    set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                calendar.timeInMillis
            } else {
                Log.e(TAG, "❌ Error parseando fecha/hora")
                0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción parseando fecha: ${e.message}")
            0L
        }
    }
}

- Crea un repositorio (ReminderRepository) que maneje los recordatorios en Firestore con operaciones CRUD: guardar, obtener, actualizar, marcar completado, posponer y eliminar.

package com.clmg.applicationflowdaily.data.repository

import android.util.Log
import com.clmg.applicationflowdaily.data.firestore.FirebaseModule
import com.clmg.applicationflowdaily.data.models.ReminderModel
import kotlinx.coroutines.tasks.await

// Este código implementa un repositorio para la gestión de recordatorios en una aplicación
// Android, usando Firebase Firestore como backend. Maneja operaciones CRUD completas para
// recordatorios con características específicas como marcar como completados, posponer, y
// filtrado por usuario

class ReminderRepository {

    private val db = FirebaseModule.db
    private val remindersCollection = db.collection("reminders")

    companion object {
        private const val TAG = "ReminderRepository"
    }

    /** Obtiene el ID del usuario actual */
    private fun getCurrentUserId(): String? {
        val userId = FirebaseModule.getCurrentUserId()
        Log.d(TAG, "🔑 Usuario actual ID: $userId")
        return userId
    }

    /** Guarda un nuevo recordatorio */
    suspend fun saveReminder(reminder: ReminderModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
            val reminderWithUser = reminder.copy(userId = userId)

            remindersCollection
                .document(reminder.id)
                .set(reminderWithUser)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene TODOS los recordatorios del usuario */
    suspend fun getReminders(): Result<List<ReminderModel>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = remindersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            var reminders = snapshot.documents.mapNotNull {
                it.toObject(ReminderModel::class.java)
            }

            reminders = reminders.sortedByDescending { it.timestamp }
            Result.success(reminders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Actualiza un recordatorio existente */
    suspend fun updateReminder(reminder: ReminderModel): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
            val reminderWithUser = reminder.copy(userId = userId)

            remindersCollection
                .document(reminder.id)
                .set(reminderWithUser)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Marca un recordatorio como completado */
    suspend fun markAsCompleted(reminderId: String): Result<Unit> {
        return try {
            remindersCollection
                .document(reminderId)
                .update("isCompleted", true)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Pospone un recordatorio (actualiza fecha/hora) */
    suspend fun postponeReminder(reminderId: String, newDate: String, newTime: String): Result<Unit> {
        return try {
            remindersCollection
                .document(reminderId)
                .update(
                    mapOf(
                        "date" to newDate,
                        "time" to newTime,
                        "timestamp" to System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Elimina un recordatorio */
    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            remindersCollection
                .document(reminderId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene un recordatorio específico por ID */
    suspend fun getReminderById(reminderId: String): Result<ReminderModel?> {
        return try {
            val snapshot = remindersCollection
                .document(reminderId)
                .get()
                .await()

            val reminder = snapshot.toObject(ReminderModel::class.java)
            Result.success(reminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

# AsistenciaScreen - Gestión de Recordatorios

Pantalla para gestionar recordatorios con notificaciones. Permite crear, editar y eliminar recordatorios categorizados (trabajo, personal, salud, etc.) con fechas y horas específicas.

## Código Principal
```kotlin

package com.clmg.applicationflowdaily.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clmg.applicationflowdaily.data.models.ReminderModel
import com.clmg.applicationflowdaily.data.notificacion.ReminderNotificationManager
import com.clmg.applicationflowdaily.ui.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.provider.Settings

//Pantalla para gestionar recordatorios con notificaciones. Permite crear, editar y eliminar
//recordatorios categorizados (trabajo, personal, salud, etc.) con fechas y horas específicas.
//Programa notificaciones automáticas para recordatorios futuros

data class Reminder(
    val id: String,
    val title: String,
    val description: String,
    val category: ReminderCategory,
    val date: String,
    val time: String,
    val notificationActive: Boolean = true
)

enum class ReminderCategory(val displayName: String, val emoji: String, val color: Color) {
    TRABAJO("Trabajo", "💼", Color(0xFF5C6BC0)),
    PERSONAL("Personal", "🔥", Color(0xFFEF5350)),
    SALUD("Salud", "🏥", Color(0xFF26A69A)),
    COMPRAS("Compras", "🛒", Color(0xFFFF9800)),
    VIAJES("Viajes", "✈️", Color(0xFF42A5F5));

    companion object {
        fun fromString(value: String): ReminderCategory {
            return values().find { it.displayName == value } ?: PERSONAL
        }
    }
}

fun Reminder.toReminderModel(): ReminderModel {
    return ReminderModel(
        id = this.id,
        userId = "",
        title = this.title,
        description = this.description,
        category = this.category.displayName,
        date = this.date,
        time = this.time,
        notificationActive = this.notificationActive,
        timestamp = System.currentTimeMillis()
    )
}

fun ReminderModel.toReminder(): Reminder {
    return Reminder(
        id = this.id,
        title = this.title,
        description = this.description,
        category = ReminderCategory.fromString(this.category),
        date = this.date,
        time = this.time,
        notificationActive = this.notificationActive
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistenciaScreen(
    onBackClick: () -> Unit,
    viewModel: ReminderViewModel = viewModel() // 👈 Usa el ViewModel
) {
    val context = LocalContext.current
    val notificationManager = remember { ReminderNotificationManager(context) }

    // Estados del ViewModel
    val reminderModels by viewModel.reminders.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.operationSuccess.collectAsState()

    // Convertir ReminderModel a Reminder para la UI
    val reminders = remember(reminderModels) {
        reminderModels.map { it.toReminder() }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var reminderToEdit by remember { mutableStateOf<Reminder?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var reminderToDelete by remember { mutableStateOf<Reminder?>(null) }

    // Manejo de permisos de notificaciones
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "✅ Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "❌ Permiso denegado - No llegarán notificaciones", Toast.LENGTH_LONG).show()
        }
    }

// ✅ UNIFICADO: Solicitar TODOS los permisos en un solo LaunchedEffect
    LaunchedEffect(Unit) {
        android.util.Log.d("DIAGNOSTICO", "====== VERIFICANDO PERMISOS ======")

        // 1. Permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            android.util.Log.d("DIAGNOSTICO", "POST_NOTIFICATIONS: $hasPermission")

            if (!hasPermission) {
                notificationPermissionLauncher.launch(permission)
            }
        }

        // 2. Permiso de alarmas exactas (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val canSchedule = alarmManager.canScheduleExactAlarms()
            android.util.Log.d("DIAGNOSTICO", "SCHEDULE_EXACT_ALARM: $canSchedule")

            if (!canSchedule) {
                Toast.makeText(
                    context,
                    "⚠️ IMPORTANTE: Se necesita permiso de alarmas exactas",
                    Toast.LENGTH_LONG
                ).show()

                kotlinx.coroutines.delay(1000) // Espera 1 segundo

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        }

        android.util.Log.d("DIAGNOSTICO", "==================================")
    }

    // ✅ CORREGIDO: Programar notificaciones SOLO para fechas futuras
    LaunchedEffect(reminderModels) {
        reminderModels.forEach { reminderModel ->
            if (reminderModel.notificationActive) {
                // Verificar si la fecha es futura antes de programar
                val isFutureDate = isReminderInFuture(reminderModel.date, reminderModel.time)
                if (isFutureDate) {
                    notificationManager.scheduleReminder(
                        reminderModel.id,
                        reminderModel.title,
                        reminderModel.description,
                        reminderModel.date,
                        reminderModel.time
                    )
                } else {
                    android.util.Log.w("NOTIFICACION", "⚠️ Recordatorio en pasado, no se programa: ${reminderModel.title}")
                }
            }
        }
    }

    // Mostrar mensajes de error
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, "❌ Error: $it", Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Mostrar mensajes de éxito
    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, "✅ $it", Toast.LENGTH_SHORT).show()
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Asistencia", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Recordatorios", fontSize = 12.sp, color = Color.White.copy(0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFAB47BC),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFAB47BC),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Agregar", tint = Color.White)
            }
        }

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFAB47BC)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFAB47BC))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Próximos eventos",
                                    color = Color.White.copy(0.9f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    "${reminders.size} recordatorios",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        "Recordatorios activos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    if (reminders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.NotificationsNone,
                                    null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No hay recordatorios", fontSize = 18.sp, color = Color.Gray)
                                Text(
                                    "Toca el botón + para crear uno",
                                    fontSize = 14.sp,
                                    color = Color.Gray.copy(0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(reminders) { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    onEdit = {
                                        reminderToEdit = reminder
                                        showEditDialog = true
                                    },
                                    onDelete = {
                                        reminderToDelete = reminder
                                        showDeleteConfirmation = true
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }

        // Diálogo Agregar
        if (showAddDialog) {
            NewReminderDialog(
                onDismiss = { showAddDialog = false },
                onSave = { newReminder ->
                    viewModel.saveReminder(newReminder.toReminderModel()) {
                        if (newReminder.notificationActive) {
                            // ✅ CORREGIDO: Verificar si es fecha futura antes de programar
                            val isFutureDate = isReminderInFuture(newReminder.date, newReminder.time)
                            if (isFutureDate) {
                                notificationManager.scheduleReminder(
                                    newReminder.id,
                                    newReminder.title,
                                    newReminder.description,
                                    newReminder.date,
                                    newReminder.time
                                )
                                Toast.makeText(context, "✅ Recordatorio guardado - Notificación programada", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "⚠️ La fecha y hora deben ser posteriores al momento actual", Toast.LENGTH_LONG).show()
                            }
                        }
                        showAddDialog = false
                    }
                }
            )
        }

        // Diálogo Editar
        if (showEditDialog && reminderToEdit != null) {
            EditReminderDialog(
                reminder = reminderToEdit!!,
                onDismiss = {
                    showEditDialog = false
                    reminderToEdit = null
                },
                onSave = { updatedReminder ->
                    viewModel.updateReminder(updatedReminder.toReminderModel()) {
                        if (updatedReminder.notificationActive) {
                            // ✅ CORREGIDO: Verificar si es fecha futura antes de programar
                            val isFutureDate = isReminderInFuture(updatedReminder.date, updatedReminder.time)
                            if (isFutureDate) {
                                notificationManager.updateReminder(
                                    updatedReminder.id,
                                    updatedReminder.title,
                                    updatedReminder.description,
                                    updatedReminder.date,
                                    updatedReminder.time
                                )
                                Toast.makeText(context, "✅ Recordatorio actualizado - Notificación reprogramada", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "⚠️ La fecha y hora deben ser posteriores al momento actual", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            notificationManager.cancelReminder(updatedReminder.id)
                        }
                        showEditDialog = false
                        reminderToEdit = null
                    }
                }
            )
        }

        // Diálogo Eliminar
        if (showDeleteConfirmation && reminderToDelete != null) {
            DeleteConfirmationDialog(
                reminder = reminderToDelete!!,
                onDismiss = {
                    showDeleteConfirmation = false
                    reminderToDelete = null
                },
                onConfirm = {
                    viewModel.deleteReminder(reminderToDelete!!.id) {
                        notificationManager.cancelReminder(reminderToDelete!!.id)
                        showDeleteConfirmation = false
                        reminderToDelete = null
                    }
                }
            )
        }
    }
}

// ✅ MÉTODO CORREGIDO - Versión mejorada
private fun isReminderInFuture(dateStr: String, timeStr: String): Boolean {
    return try {
        // Usar el mismo Locale.ENGLISH que usas en los diálogos
        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

        val parsedDate = dateFormatter.parse(dateStr)
        val parsedTime = timeFormatter.parse(timeStr)

        if (parsedDate != null && parsedTime != null) {
            // Crear calendario para el recordatorio
            val reminderCalendar = Calendar.getInstance().apply {
                // Primero establecer la fecha
                time = parsedDate

                // Luego establecer la hora desde el tiempo parseado
                val timeCalendar = Calendar.getInstance().apply {
                    time = parsedTime
                }

                set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Crear calendario actual (sin milisegundos para comparación justa)
            val currentCalendar = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // DEBUG: Mostrar las fechas comparadas
            android.util.Log.d("FECHA_DEBUG", "🔍 Comparando fechas:")
            android.util.Log.d("FECHA_DEBUG", "   Recordatorio: ${reminderCalendar.time}")
            android.util.Log.d("FECHA_DEBUG", "   Actual: ${currentCalendar.time}")
            android.util.Log.d("FECHA_DEBUG", "   Es futuro: ${reminderCalendar.after(currentCalendar)}")

            // Verificar si el recordatorio es después del momento actual
            reminderCalendar.after(currentCalendar)
        } else {
            android.util.Log.e("FECHA_DEBUG", "❌ Error parseando fecha/hora")
            false
        }
    } catch (e: Exception) {
        android.util.Log.e("FECHA_DEBUG", "❌ Excepción en isReminderInFuture: ${e.message}")
        false
    }
}

@Composable
fun ReminderCard(reminder: Reminder, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(reminder.category.color.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    null,
                    tint = reminder.category.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    reminder.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    reminder.description,
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        null,
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${reminder.date} • ${reminder.time}",
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                    if (reminder.notificationActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.NotificationsActive,
                            "Activa",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    "Editar",
                    tint = Color(0xFF5C6BC0),
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    "Eliminar",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


// RESTO DEL CÓDIGO (ReminderCard, NewReminderDialog, etc.) - SE MANTIENE IGUAL
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReminderDialog(
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ReminderCategory?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var notificationActive by remember { mutableStateOf(true) }

    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var selectedTime by remember { mutableStateOf(calendar.time) }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Nuevo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Recordatorio",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                Text(
                    "Crea un recordatorio",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column {
                            Text(
                                "Título del recordatorio",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("Escribe el título...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    item {
                        Column {
                            Text(
                                "Descripción",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Describe tu recordatorio...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    item {
                        Column {
                            Text(
                                "Categoría",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCategoryMenu = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        selectedCategory?.let { "${it.emoji} ${it.displayName}" }
                                            ?: "Selecciona una categoría",
                                        color = if (selectedCategory == null) Color(0xFF999999) else Color.Black
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Fecha",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDatePicker = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            dateFormatter.format(selectedDate),
                                            color = Color.Black
                                        )
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "Seleccionar fecha",
                                            tint = Color(0xFFAB47BC)
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Hora",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTimePicker = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            timeFormatter.format(selectedTime),
                                            color = Color.Black
                                        )
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = "Seleccionar hora",
                                            tint = Color(0xFFAB47BC)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Notificación activa",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Recibirás una alerta a la hora programada",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                            Switch(
                                checked = notificationActive,
                                onCheckedChange = { notificationActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFAB47BC)
                                )
                            )
                        }
                    }
                }

                // Botón Guardar
                Button(
                    onClick = {
                        if (title.isNotEmpty() && selectedCategory != null) {
                            onSave(
                                Reminder(
                                    id = UUID.randomUUID().toString(),
                                    title = title,
                                    description = description,
                                    category = selectedCategory!!,
                                    date = dateFormatter.format(selectedDate),
                                    time = timeFormatter.format(selectedTime),
                                    notificationActive = notificationActive
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFAB47BC)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = title.isNotEmpty() && selectedCategory != null
                ) {
                    Text(
                        "Guardar Recordatorio",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Diálogo de categorías
        if (showCategoryMenu) {
            CategorySelectionDialog(
                onDismiss = { showCategoryMenu = false },
                onSelect = { category ->
                    selectedCategory = category
                    showCategoryMenu = false
                }
            )
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.time
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDate = Date(millis)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Aceptar", color = Color(0xFFAB47BC))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color(0xFFAB47BC),
                        todayContentColor = Color(0xFFAB47BC),
                        todayDateBorderColor = Color(0xFFAB47BC)
                    )
                )
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            val calendarForTime = Calendar.getInstance().apply { time = selectedTime }
            val timePickerState = rememberTimePickerState(
                initialHour = calendarForTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = calendarForTime.get(Calendar.MINUTE),
                is24Hour = false
            )

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val newCalendar = Calendar.getInstance().apply {
                                time = selectedTime
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                            }
                            selectedTime = newCalendar.time
                            showTimePicker = false
                        }
                    ) {
                        Text("Aceptar", color = Color(0xFFAB47BC))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                text = {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = Color.Black,
                            selectorColor = Color(0xFFAB47BC),
                            periodSelectorSelectedContainerColor = Color(0xFFAB47BC),
                            timeSelectorSelectedContainerColor = Color(0xFFAB47BC),
                            timeSelectorSelectedContentColor = Color.White
                        )
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderDialog(
    reminder: Reminder,
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit
) {
    var title by remember { mutableStateOf(reminder.title) }
    var description by remember { mutableStateOf(reminder.description) }
    var selectedCategory by remember { mutableStateOf<ReminderCategory?>(reminder.category) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var notificationActive by remember { mutableStateOf(reminder.notificationActive) }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH) // ✅ CORREGIDO: Usar ENGLISH
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH) // ✅ CORREGIDO: Usar ENGLISH

    var selectedDate by remember {
        mutableStateOf(
            try {
                dateFormatter.parse(reminder.date) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        )
    }
    var selectedTime by remember {
        mutableStateOf(
            try {
                timeFormatter.parse(reminder.time) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Editar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Recordatorio",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                Text(
                    "Modifica tu recordatorio",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column {
                            Text(
                                "Título del recordatorio",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("Escribe el título...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    item {
                        Column {
                            Text(
                                "Descripción",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Describe tu recordatorio...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    item {
                        Column {
                            Text(
                                "Categoría",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCategoryMenu = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        selectedCategory?.let { "${it.emoji} ${it.displayName}" }
                                            ?: "Selecciona una categoría",
                                        color = if (selectedCategory == null) Color(0xFF999999) else Color.Black
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Fecha",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDatePicker = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            dateFormatter.format(selectedDate),
                                            color = Color.Black
                                        )
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "Seleccionar fecha",
                                            tint = Color(0xFFAB47BC)
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Hora",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTimePicker = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            timeFormatter.format(selectedTime),
                                            color = Color.Black
                                        )
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = "Seleccionar hora",
                                            tint = Color(0xFFAB47BC)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Notificación activa",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Recibirás una alerta a la hora programada",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                            Switch(
                                checked = notificationActive,
                                onCheckedChange = { notificationActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFAB47BC)
                                )
                            )
                        }
                    }
                }

                // Botón Actualizar
                Button(
                    onClick = {
                        if (title.isNotEmpty() && selectedCategory != null) {
                            onSave(
                                reminder.copy(
                                    title = title,
                                    description = description,
                                    category = selectedCategory!!,
                                    date = dateFormatter.format(selectedDate),
                                    time = timeFormatter.format(selectedTime),
                                    notificationActive = notificationActive
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFAB47BC)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = title.isNotEmpty() && selectedCategory != null
                ) {
                    Text(
                        "Actualizar Recordatorio",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Diálogo de categorías
        if (showCategoryMenu) {
            CategorySelectionDialog(
                onDismiss = { showCategoryMenu = false },
                onSelect = { category ->
                    selectedCategory = category
                    showCategoryMenu = false
                }
            )
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.time
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDate = Date(millis)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Aceptar", color = Color(0xFFAB47BC))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color(0xFFAB47BC),
                        todayContentColor = Color(0xFFAB47BC),
                        todayDateBorderColor = Color(0xFFAB47BC)
                    )
                )
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            val calendarForTime = Calendar.getInstance().apply { time = selectedTime }
            val timePickerState = rememberTimePickerState(
                initialHour = calendarForTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = calendarForTime.get(Calendar.MINUTE),
                is24Hour = false
            )

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val newCalendar = Calendar.getInstance().apply {
                                time = selectedTime
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                            }
                            selectedTime = newCalendar.time
                            showTimePicker = false
                        }
                    ) {
                        Text("Aceptar", color = Color(0xFFAB47BC))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                text = {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = Color.Black,
                            selectorColor = Color(0xFFAB47BC),
                            periodSelectorSelectedContainerColor = Color(0xFFAB47BC),
                            timeSelectorSelectedContainerColor = Color(0xFFAB47BC),
                            timeSelectorSelectedContentColor = Color.White
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    reminder: Reminder,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Eliminar recordatorio",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF5350)
            )
        },
        text = {
            Column {
                Text("¿Estás seguro de que quieres eliminar este recordatorio?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    reminder.title,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF424242)
                )
                if (reminder.description.isNotEmpty()) {
                    Text(
                        reminder.description,
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Esta acción no se puede deshacer.",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFEF5350)
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CategorySelectionDialog(
    onDismiss: () -> Unit,
    onSelect: (ReminderCategory) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFAB47BC))
                        .padding(16.dp)
                ) {
                    Text(
                        "Selecciona una categoría",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Categorías
                ReminderCategory.values().forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(category) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            category.emoji,
                            fontSize = 24.sp,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            category.displayName,
                            fontSize = 16.sp
                        )
                    }
                    if (category != ReminderCategory.values().last()) {
                        Divider()
                    }
                }
            }
        }
    }
}

```

## Características

- ✅ Gestión completa de recordatorios (CRUD)
- 🔔 Notificaciones programadas automáticamente
- 📅 Selector de fecha y hora integrado
- 🏷️ Categorías personalizadas con emojis
- 💾 Persistencia con Firebase/ViewModel
- ⚠️ Validación de fechas futuras







