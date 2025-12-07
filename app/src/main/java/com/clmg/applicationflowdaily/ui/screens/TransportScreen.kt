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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
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
                        Icon(
                            Icons.Default.ArrowBack,
                            "Volver",
                            tint = Color.White
                        )
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
                            Icon(
                                Icons.Default.MyLocation,
                                contentDescription = "Mi ubicación",
                                tint = Color.White
                            )
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
                            if (origin == null) {
                                viewModel.setOrigin(latLng)
                            } else {
                                viewModel.setDestination(latLng)
                            }
                        }
                    ) {
                        origin?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "Origen"
                            )
                        }

                        destination?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "Destino"
                            )
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
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                "Toca el mapa para seleccionar el ORIGEN",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF00BCD4)
                            )
                        }
                    } else if (destination == null) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                "Toca el mapa para seleccionar el DESTINO",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF00BCD4)
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Distancia calculada:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                String.format("%.2f km", distanceKm),
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFF00BCD4),
                                fontWeight = FontWeight.Bold
                            )

                            if (distanceKm > 0) {
                                Text(
                                    "$durationMinutes min",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        if (hasLocationPermission && currentLocation != null && origin == null) {
                            Button(
                                onClick = {
                                    currentLocation?.let { viewModel.setOrigin(it) }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Usar mi ubicación como origen")
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showSaveDialog = true },
                                enabled = origin != null && destination != null && !isLoading,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00BCD4)
                                )
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Guardar")
                                Spacer(Modifier.width(4.dp))
                                Text("Guardar")
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearRoute() },
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Limpiar")
                                Spacer(Modifier.width(4.dp))
                                Text("Limpiar")
                            }
                        }

                        if (saveSuccess) {
                            Spacer(Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF00BCD4).copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    "✓ Ruta guardada exitosamente",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF00BCD4),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        errorMessage?.let { error ->
                            Spacer(Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    "Error: $error",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // VISTA DEL HISTORIAL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00BCD4)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Route,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Rutas frecuentes",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "${transportRecords.size} rutas",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Total distancia",
                                    color = Color.White.copy(0.8f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    String.format("%.1f km", totalDistance),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Tiempo total",
                                    color = Color.White.copy(0.8f),
                                    fontSize = 12.sp
                                )
                                val totalMinutes = transportRecords.sumOf { it.durationMinutes }
                                Text(
                                    "$totalMinutes min",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Text(
                    "Mis rutas",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00BCD4))
                    }
                } else if (transportRecords.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No hay rutas guardadas",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(transportRecords) { record ->
                            TransportRecordCard(
                                record = record,
                                onEditClick = {
                                    viewModel.setOrigin(
                                        LatLng(record.originLat, record.originLng)
                                    )
                                    viewModel.setDestination(
                                        LatLng(record.destinationLat, record.destinationLng)
                                    )
                                    showMapView = true
                                },
                                onDeleteClick = {
                                    viewModel.deleteRecord(record.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = {
                    Text(
                        "Guardar ruta",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text("Agregar notas (opcional):")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("Ej: Casa - Oficina") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.saveCurrentRoute(notes)
                            showSaveDialog = false
                            notes = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4)
                        )
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancelar")
                    }
                }
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ✅ Fila superior: Icono + Información
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

                    // Distancia y tiempo
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                    // Fecha
                    Text(
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(record.timestamp)),
                        fontSize = 12.sp,
                        color = Color(0xFF00BCD4)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ✅ Fila inferior: Botones (ahora en toda la anchura)
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
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Editar", fontSize = 13.sp)
                }

                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", fontSize = 13.sp)
                }
            }
        }
    }
}