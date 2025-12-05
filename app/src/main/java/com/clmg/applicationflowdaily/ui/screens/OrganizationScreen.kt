package com.clmg.applicationflowdaily.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clmg.applicationflowdaily.data.models.TaskModel
import com.clmg.applicationflowdaily.ui.viewmodel.TaskViewModel
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val time: String,
    var completed: Boolean = false,
    val category: String = ""
)

/**
 * Convierte Task (UI) a TaskModel (Firebase)
 */
fun Task.toTaskModel(): TaskModel {
    return TaskModel(
        id = this.id,
        userId = "",
        name = this.name,
        time = this.time,
        completed = this.completed,
        category = this.category,
        timestamp = System.currentTimeMillis()
    )
}

/**
 * Convierte TaskModel (Firebase) a Task (UI)
 * Usa formato amigable para la fecha
 */
fun TaskModel.toTask(): Task {
    return Task(
        id = this.id,
        name = this.name,
        time = formatFriendlyDateTime(this.timestamp), // ✅ Formato amigable
        completed = this.completed,
        category = this.category
    )
}

/**
 * Genera fecha/hora actual en formato amigable
 * Usado al crear nuevas tareas
 */
fun getCurrentDateTime(): String {
    val calendar = Calendar.getInstance()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return "Hoy ${timeFormat.format(Date(calendar.timeInMillis))}"
}

/**
 * Formatea timestamp a texto amigable
 * - Si es hoy: "Hoy 15:30"
 * - Si es ayer: "Ayer 15:30"
 * - Si es esta semana: "Lunes 15:30"
 * - Más antiguo: "04/12/2025 15:30"
 */
fun formatFriendlyDateTime(timestamp: Long): String {
    val now = Calendar.getInstance()
    val taskDate = Calendar.getInstance().apply { timeInMillis = timestamp }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    return when {
        // Hoy
        now.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR) &&
                now.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) -> {
            "Hoy ${timeFormat.format(Date(timestamp))}"
        }

        // Ayer
        now.get(Calendar.DAY_OF_YEAR) - taskDate.get(Calendar.DAY_OF_YEAR) == 1 &&
                now.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) -> {
            "Ayer ${timeFormat.format(Date(timestamp))}"
        }

        // Esta semana
        now.get(Calendar.WEEK_OF_YEAR) == taskDate.get(Calendar.WEEK_OF_YEAR) &&
                now.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) -> {
            val dayName = SimpleDateFormat("EEEE", Locale("es", "ES")).format(Date(timestamp))
            "${dayName.replaceFirstChar { it.uppercase() }} ${timeFormat.format(Date(timestamp))}"
        }

        // Fecha completa
        else -> dateTimeFormat.format(Date(timestamp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionScreen(
    onBackClick: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estados del ViewModel
    val taskModels by viewModel.tasks.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.operationSuccess.collectAsState()

    // Convertir TaskModel a Task para la UI
    val tasks = remember(taskModels) {
        taskModels.map { it.toTask() }
    }

    var showNewTaskDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

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
                title = { Text("Organización", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5C6BC0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewTaskDialog = true },
                containerColor = Color(0xFF5C6BC0),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar tarea",
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
                    color = Color(0xFF5C6BC0)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    ProgressCard(tasks)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Tareas pendientes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (tasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Assignment,
                                    null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No hay tareas", fontSize = 18.sp, color = Color.Gray)
                                Text(
                                    "Toca el botón + para crear una",
                                    fontSize = 14.sp,
                                    color = Color.Gray.copy(0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(tasks) { index, task ->
                                TaskItem(
                                    task = task,
                                    onToggle = {
                                        val updatedTask = task.copy(completed = !task.completed)
                                        viewModel.updateTask(updatedTask.toTaskModel())
                                    },
                                    onEdit = {
                                        taskToEdit = task
                                        showEditDialog = true
                                    },
                                    onDelete = {
                                        taskToDelete = task
                                        showDeleteConfirmation = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Diálogo Agregar
        if (showNewTaskDialog) {
            NewTaskDialog(
                onDismiss = { showNewTaskDialog = false },
                onSave = { newTask ->
                    viewModel.saveTask(newTask.toTaskModel()) {
                        showNewTaskDialog = false
                    }
                }
            )
        }

        // Diálogo Editar
        if (showEditDialog && taskToEdit != null) {
            EditTaskDialog(
                task = taskToEdit!!,
                onDismiss = {
                    showEditDialog = false
                    taskToEdit = null
                },
                onSave = { updatedTask ->
                    viewModel.updateTask(updatedTask.toTaskModel()) {
                        showEditDialog = false
                        taskToEdit = null
                    }
                }
            )
        }

        // Diálogo Eliminar
        if (showDeleteConfirmation && taskToDelete != null) {
            DeleteConfirmationDialog(
                task = taskToDelete!!,
                onDismiss = {
                    showDeleteConfirmation = false
                    taskToDelete = null
                },
                onConfirm = {
                    viewModel.deleteTask(taskToDelete!!.id) {
                        showDeleteConfirmation = false
                        taskToDelete = null
                    }
                }
            )
        }
    }
}

@Composable
fun ProgressCard(tasks: List<Task>) {
    val completed = tasks.count { it.completed }
    val total = tasks.size
    val percentage = if (total > 0) (completed.toFloat() / total * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7986CB),
                            Color(0xFF5C6BC0)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Progreso",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                        Text(
                            "$completed/$total",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "$percentage%",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(percentage / 100f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
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
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (task.completed) Color(0xFF4CAF50) else Color(0xFFE0E0E0)
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.completed) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completada",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        task.time,
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF5C6BC0),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nueva", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Organización", fontSize = 14.sp, color = Color(0xFF757575))
                    }
                }

                Text(
                    "Crea una tarea",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text("Descripción de la tarea", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Describe tu tarea...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Categoría", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
                                if (selectedCategory.isEmpty()) "Selecciona una categoría" else selectedCategory,
                                color = if (selectedCategory.isEmpty()) Color(0xFF999999) else Color.Black
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    if (showCategoryMenu) {
                        CategoryDropdown(
                            onDismiss = { showCategoryMenu = false },
                            onSelect = { category ->
                                selectedCategory = category
                                showCategoryMenu = false
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Marcar como completada", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("La tarea estará marcada como hecha", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                        Switch(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF5C6BC0)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (description.isNotEmpty() && selectedCategory.isNotEmpty()) {
                                // ✅ Usa getCurrentDateTime() para formato amigable
                                onSave(
                                    Task(
                                        name = description,
                                        time = getCurrentDateTime(),
                                        completed = isCompleted,
                                        category = selectedCategory
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C6BC0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = description.isNotEmpty() && selectedCategory.isNotEmpty()
                    ) {
                        Text("Guardar Tarea", modifier = Modifier.padding(8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var description by remember { mutableStateOf(task.name) }
    var selectedCategory by remember { mutableStateOf(task.category) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(task.completed) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Editar", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Organización", fontSize = 14.sp, color = Color(0xFF757575))
                    }
                }

                Text(
                    "Modifica tu tarea",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text("Descripción de la tarea", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Describe tu tarea...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Categoría", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
                                if (selectedCategory.isEmpty()) "Selecciona una categoría" else selectedCategory,
                                color = if (selectedCategory.isEmpty()) Color(0xFF999999) else Color.Black
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    if (showCategoryMenu) {
                        CategoryDropdown(
                            onDismiss = { showCategoryMenu = false },
                            onSelect = { category ->
                                selectedCategory = category
                                showCategoryMenu = false
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Marcar como completada", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("La tarea estará marcada como hecha", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                        Switch(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF5C6BC0)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (description.isNotEmpty() && selectedCategory.isNotEmpty()) {
                                // ✅ Mantiene el time original automáticamente con task.copy()
                                onSave(
                                    task.copy(
                                        name = description,
                                        category = selectedCategory,
                                        completed = isCompleted
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C6BC0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = description.isNotEmpty() && selectedCategory.isNotEmpty()
                    ) {
                        Text("Actualizar Tarea", modifier = Modifier.padding(8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Eliminar tarea",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF5350)
            )
        },
        text = {
            Column {
                Text("¿Estás seguro de que quieres eliminar esta tarea?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    task.name,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF424242)
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
fun CategoryDropdown(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val categories = listOf(
        "💼 Trabajo",
        "🔥 Personal",
        "🏥 Salud",
        "🛒 Compras",
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
                        .background(Color(0xFF5C6BC0))
                        .padding(16.dp)
                ) {
                    Text(
                        "Selecciona una categoría",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(category) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(category, fontSize = 16.sp)
                    }
                    if (category != categories.last()) {
                        Divider()
                    }
                }
            }
        }
    }
}