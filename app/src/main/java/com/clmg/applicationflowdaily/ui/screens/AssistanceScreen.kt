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