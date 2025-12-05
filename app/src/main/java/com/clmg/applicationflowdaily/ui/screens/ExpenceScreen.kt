
package com.clmg.applicationflowdaily.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clmg.applicationflowdaily.data.models.ExpenseModel
import com.clmg.applicationflowdaily.ui.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

data class Gasto(
    val id: String = UUID.randomUUID().toString(),
    val monto: Double,
    val descripcion: String,
    val categoria: String,
    val fecha: Date = Date(),
    val tipo: TipoTransaccion = TipoTransaccion.GASTO,
    val emoji: String = "💰"
)

enum class TipoTransaccion {
    GASTO, INGRESO
}

/**
 * Convierte Gasto (UI) a ExpenseModel (Firebase)
 */
fun Gasto.toExpenseModel(): ExpenseModel {
    return ExpenseModel(
        id = this.id,
        userId = "",
        monto = this.monto,
        descripcion = this.descripcion,
        categoria = this.categoria,
        fecha = this.fecha.time, // Convertir Date a Long (timestamp)
        tipo = this.tipo.name, // Convertir enum a String
        emoji = this.emoji,
        timestamp = System.currentTimeMillis()
    )
}

/**
 * Convierte ExpenseModel (Firebase) a Gasto (UI)
 */
fun ExpenseModel.toGasto(): Gasto {
    return Gasto(
        id = this.id,
        monto = this.monto,
        descripcion = this.descripcion,
        categoria = this.categoria,
        fecha = Date(this.fecha), // Convertir Long a Date
        tipo = try {
            TipoTransaccion.valueOf(this.tipo)
        } catch (e: Exception) {
            TipoTransaccion.GASTO
        },
        emoji = this.emoji
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    onBackClick: () -> Unit,
    viewModel: ExpenseViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estados del ViewModel
    val expenseModels by viewModel.expenses.collectAsState()
    val balanceModel by viewModel.balance.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.operationSuccess.collectAsState()

    // Convertir ExpenseModel a Gasto para la UI
    val gastos = remember(expenseModels) {
        expenseModels.map { it.toGasto() }
    }

    // Balance inicial desde Firebase
    val balanceInicial = balanceModel?.balanceInicial ?: 0.0

    var showNewGastoDialog by remember { mutableStateOf(false) }
    var showEditBalanceDialog by remember { mutableStateOf(false) }
    var gastoToEdit by remember { mutableStateOf<Gasto?>(null) }
    var gastoToDelete by remember { mutableStateOf<Gasto?>(null) }

    val totalGastos = gastos.filter { it.tipo == TipoTransaccion.GASTO }.sumOf { it.monto }
    val totalIngresos = gastos.filter { it.tipo == TipoTransaccion.INGRESO }.sumOf { it.monto }
    val balanceDisponible = balanceInicial + totalIngresos - totalGastos

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
                        Text("Gastos", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Control de finanzas",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00897B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewGastoDialog = true },
                containerColor = Color(0xFF00897B),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar gasto",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF00897B)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BalanceCard(
                        balanceDisponible = balanceDisponible,
                        gastoHoy = gastos.filter { isSameDay(it.fecha, Date()) }
                            .filter { it.tipo == TipoTransaccion.GASTO }
                            .sumOf { it.monto },
                        onBalanceClick = { showEditBalanceDialog = true }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(
                            "Gastos recientes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (gastos.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.AccountBalance,
                                        null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("No hay gastos registrados", fontSize = 18.sp, color = Color.Gray)
                                    Text(
                                        "Toca el botón + para agregar uno",
                                        fontSize = 14.sp,
                                        color = Color.Gray.copy(0.7f)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(gastos) { gasto ->
                                    GastoItem(
                                        gasto = gasto,
                                        onEdit = { gastoToEdit = it },
                                        onDelete = {
                                            gastoToDelete = gasto
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }

        // Diálogo Agregar
        if (showNewGastoDialog) {
            NewGastoDialog(
                onDismiss = { showNewGastoDialog = false },
                onSave = { newGasto ->
                    viewModel.saveExpense(newGasto.toExpenseModel()) {
                        showNewGastoDialog = false
                    }
                }
            )
        }

        // Diálogo Editar
        if (gastoToEdit != null) {
            EditGastoDialog(
                gasto = gastoToEdit!!,
                onDismiss = { gastoToEdit = null },
                onSave = { updatedGasto ->
                    viewModel.updateExpense(updatedGasto.toExpenseModel()) {
                        gastoToEdit = null
                    }
                }
            )
        }

        // Diálogo Editar Balance
        if (showEditBalanceDialog) {
            EditBalanceDialog(
                currentBalance = balanceInicial,
                onDismiss = { showEditBalanceDialog = false },
                onSave = { newBalance ->
                    viewModel.saveBalance(newBalance) {
                        showEditBalanceDialog = false
                    }
                }
            )
        }

        // Diálogo Eliminar
        if (gastoToDelete != null) {
            DeleteGastoConfirmationDialog(
                gasto = gastoToDelete!!,
                onDismiss = {
                    gastoToDelete = null
                },
                onConfirm = {
                    viewModel.deleteExpense(gastoToDelete!!.id) {
                        gastoToDelete = null
                    }
                }
            )
        }
    }
}

@Composable
fun BalanceCard(
    balanceDisponible: Double,
    gastoHoy: Double,
    onBalanceClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onBalanceClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF26A69A),
                            Color(0xFF00897B)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "Balance disponible",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
                Text(
                    formatCurrency(balanceDisponible),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}

@Composable
fun GastoItem(
    gasto: Gasto,
    onEdit: (Gasto) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(gasto.emoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    gasto.descripcion,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    // Modificado: Se agregó la fecha junto con la categoría y hora
                    "${gasto.categoria} • ${formatDate(gasto.fecha)} • ${formatTime(gasto.fecha)}",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            Text(
                if (gasto.tipo == TipoTransaccion.GASTO) "-${formatCurrency(gasto.monto)}"
                else "+${formatCurrency(gasto.monto)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (gasto.tipo == TipoTransaccion.GASTO) Color(0xFFE53935) else Color(0xFF43A047)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Botones de editar y eliminar visibles
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Editar
                IconButton(
                    onClick = { onEdit(gasto) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF00897B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Botón Eliminar
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteGastoConfirmationDialog(
    gasto: Gasto,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Eliminar gasto",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935)
            )
        },
        text = {
            Column {
                Text("¿Estás seguro de que quieres eliminar este gasto?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    gasto.descripcion,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Monto: ${formatCurrency(gasto.monto)}",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
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
                    contentColor = Color(0xFFE53935)
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGastoDialog(
    onDismiss: () -> Unit,
    onSave: (Gasto) -> Unit
) {
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf("") }
    var showCategoryMenu by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f), // Reducido para hacerlo más compacto
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Header más compacto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00897B))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Nuevo Gasto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                "Agregar nuevo gasto",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Contenido principal más compacto
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monto
                    Column {
                        Text(
                            "Monto",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = monto,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    monto = it
                                }
                            },
                            placeholder = { Text("$ 0.00") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Descripción
                    Column {
                        Text(
                            "Descripción",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            placeholder = { Text("Ej: Comida, Café, Transporte...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Categoría
                    Column {
                        Text(
                            "Categoría",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryMenu = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (selectedCategoria.isEmpty()) "Selecciona una categoría"
                                    else selectedCategoria,
                                    color = if (selectedCategoria.isEmpty()) Color.Gray else Color.Black,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    // Fila para Fecha y Hora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Fecha
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Fecha",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        formatDate(selectedYear, selectedMonth, selectedDay),
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFF00897B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Hora
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Hora",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTimePicker = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        formatTimeOnly(selectedHour, selectedMinute),
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF00897B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Botón en la parte inferior
                Button(
                    onClick = {
                        if (monto.isNotEmpty() && descripcion.isNotEmpty() && selectedCategoria.isNotEmpty()) {
                            val finalCalendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, selectedYear)
                                set(Calendar.MONTH, selectedMonth)
                                set(Calendar.DAY_OF_MONTH, selectedDay)
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            onSave(
                                Gasto(
                                    monto = monto.toDoubleOrNull() ?: 0.0,
                                    descripcion = descripcion,
                                    categoria = selectedCategoria.substringAfter(" "),
                                    fecha = finalCalendar.time,
                                    tipo = TipoTransaccion.GASTO, // Siempre será gasto
                                    emoji = selectedCategoria.substringBefore(" ")
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00897B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = monto.isNotEmpty() && descripcion.isNotEmpty() && selectedCategoria.isNotEmpty()
                ) {
                    Text("Guardar Gasto", modifier = Modifier.padding(8.dp), fontSize = 15.sp)
                }
            }
        }

        if (showCategoryMenu) {
            CategoryDropdownGastos(
                onDismiss = { showCategoryMenu = false },
                onSelect = { category ->
                    selectedCategoria = category
                    showCategoryMenu = false
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val cal = Calendar.getInstance().apply {
                                    timeInMillis = millis
                                }
                                selectedYear = cal.get(Calendar.YEAR)
                                selectedMonth = cal.get(Calendar.MONTH)
                                selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Aceptar", color = Color(0xFF00897B))
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
                        selectedDayContainerColor = Color(0xFF00897B),
                        todayContentColor = Color(0xFF00897B),
                        todayDateBorderColor = Color(0xFF00897B)
                    )
                )
            }
        }

        if (showTimePicker) {
            TimePickerDialogCustom(
                onDismiss = { showTimePicker = false },
                onConfirm = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    showTimePicker = false
                },
                initialHour = selectedHour,
                initialMinute = selectedMinute
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGastoDialog(
    gasto: Gasto,
    onDismiss: () -> Unit,
    onSave: (Gasto) -> Unit
) {
    var monto by remember { mutableStateOf(gasto.monto.toString()) }
    var descripcion by remember { mutableStateOf(gasto.descripcion) }
    var selectedCategoria by remember { mutableStateOf("${gasto.emoji} ${gasto.categoria}") }
    var showCategoryMenu by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance().apply { time = gasto.fecha }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = gasto.fecha.time
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f), // Más compacto
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Header más compacto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00897B))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Editar Gasto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                "Modifica los datos del gasto",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Contenido principal más compacto
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monto
                    Column {
                        Text(
                            "Monto",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = monto,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    monto = it
                                }
                            },
                            placeholder = { Text("$ 0.00") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Descripción
                    Column {
                        Text(
                            "Descripción",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            placeholder = { Text("Ej: Comida, Café, Transporte...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Categoría
                    Column {
                        Text(
                            "Categoría",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryMenu = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (selectedCategoria.isEmpty()) "Selecciona una categoría"
                                    else selectedCategoria,
                                    color = if (selectedCategoria.isEmpty()) Color.Gray else Color.Black,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    // Fila para Fecha y Hora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Fecha
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Fecha",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        formatDate(selectedYear, selectedMonth, selectedDay),
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFF00897B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Hora
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Hora",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTimePicker = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        formatTimeOnly(selectedHour, selectedMinute),
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF00897B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Botón en la parte inferior
                Button(
                    onClick = {
                        if (monto.isNotEmpty() && descripcion.isNotEmpty() && selectedCategoria.isNotEmpty()) {
                            val finalCalendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, selectedYear)
                                set(Calendar.MONTH, selectedMonth)
                                set(Calendar.DAY_OF_MONTH, selectedDay)
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            onSave(
                                gasto.copy(
                                    monto = monto.toDoubleOrNull() ?: 0.0,
                                    descripcion = descripcion,
                                    categoria = selectedCategoria.substringAfter(" "),
                                    fecha = finalCalendar.time,
                                    tipo = TipoTransaccion.GASTO, // Siempre será gasto
                                    emoji = selectedCategoria.substringBefore(" ")
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00897B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = monto.isNotEmpty() && descripcion.isNotEmpty() && selectedCategoria.isNotEmpty()
                ) {
                    Text("Actualizar Gasto", modifier = Modifier.padding(8.dp), fontSize = 15.sp)
                }
            }
        }

        if (showCategoryMenu) {
            CategoryDropdownGastos(
                onDismiss = { showCategoryMenu = false },
                onSelect = { category ->
                    selectedCategoria = category
                    showCategoryMenu = false
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val cal = Calendar.getInstance().apply {
                                    timeInMillis = millis
                                }
                                selectedYear = cal.get(Calendar.YEAR)
                                selectedMonth = cal.get(Calendar.MONTH)
                                selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Aceptar", color = Color(0xFF00897B))
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
                        selectedDayContainerColor = Color(0xFF00897B),
                        todayContentColor = Color(0xFF00897B),
                        todayDateBorderColor = Color(0xFF00897B)
                    )
                )
            }
        }

        if (showTimePicker) {
            TimePickerDialogCustom(
                onDismiss = { showTimePicker = false },
                onConfirm = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    showTimePicker = false
                },
                initialHour = selectedHour,
                initialMinute = selectedMinute
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogCustom(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("Aceptar", color = Color(0xFF00897B))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = Color.Black,
                    selectorColor = Color(0xFF00897B),
                    periodSelectorSelectedContainerColor = Color(0xFF00897B),
                    timeSelectorSelectedContainerColor = Color(0xFF00897B),
                    timeSelectorSelectedContentColor = Color.White
                )
            )
        }
    )
}

@Composable
fun CategoryDropdownGastos(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val categories = listOf(
        "🍕 Alimentos",
        "☕ Bebidas",
        "🚗 Transporte",
        "🏠 Hogar",
        "💡 Servicios",
        "🎮 Entretenimiento",
        "🏥 Salud",
        "👕 Ropa",
        "📚 Educación",
        "✈ Viajes"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00897B))
                        .padding(16.dp)
                ) {
                    Text(
                        "Selecciona una categoría",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LazyColumn {
                    items(categories) { category ->
                        Text(
                            category,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(category) }
                                .padding(16.dp),
                            fontSize = 15.sp
                        )
                        if (category != categories.last()) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBalanceDialog(
    currentBalance: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var balanceText by remember { mutableStateOf(if (currentBalance == 0.0) "" else currentBalance.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00897B))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            "Editar Balance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            "Actualiza tu balance inicial",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Balance Inicial",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )

                    OutlinedTextField(
                        value = balanceText,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                balanceText = it
                            }
                        },
                        placeholder = { Text("$ 0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Text(
                                "$",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00897B),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    )

                    Text(
                        "Este será tu saldo base. Y los gastos se restarán de este monto.",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        lineHeight = 16.sp
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF00897B)
                        )
                    ) {
                        Text("Cancelar", modifier = Modifier.padding(8.dp))
                    }

                    Button(
                        onClick = {
                            val newBalance = balanceText.toDoubleOrNull() ?: 0.0
                            onSave(newBalance)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = balanceText.isNotEmpty()
                    ) {
                        Text("Guardar", modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    return "$${String.format("%.2f", amount)}"
}

fun formatTime(date: Date): String {
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(date)
}

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(date)
}

fun formatDate(year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
    }
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(calendar.time)
}

fun formatTimeOnly(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(calendar.time)
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}