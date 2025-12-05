package com.clmg.applicationflowdaily.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clmg.applicationflowdaily.R
import com.clmg.applicationflowdaily.ui.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onOrganizacionClick: () -> Unit = {},
    onGastosClick: () -> Unit = {},
    onTransporteClick: () -> Unit = {},
    onAsistenciaClick: () -> Unit = {},
    viewModel: AuthViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()

    // Obtener solo el primer nombre
    val firstName = currentUser?.name?.split(" ")?.firstOrNull()
        ?: stringResource(R.string.welcome)

    // Obtener fecha actual formateada según el idioma
    val currentLocale = Locale.getDefault()
    val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM", currentLocale)
    val currentDate = remember(currentLocale) {
        dateFormat.format(Date()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.hello)}, $firstName 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Profile Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.profile),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Menu Cards
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            // Organización Card
            MenuCard(
                title = stringResource(R.string.organization),
                subtitle = stringResource(R.string.tasks),
                icon = Icons.Default.Check,
                iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                iconTint = MaterialTheme.colorScheme.primary,
                badgeCount = 4,
                onClick = onOrganizacionClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Gastos Card
            MenuCard(
                title = stringResource(R.string.expenses),
                subtitle = stringResource(R.string.my_expenses),
                icon = Icons.Default.ShoppingCart,
                iconBackgroundColor = Color(0xFFE0F2F1),
                iconTint = Color(0xFF26A69A),
                onClick = onGastosClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transporte Card
            MenuCard(
                title = stringResource(R.string.transport),
                subtitle = stringResource(R.string.my_routes),
                icon = Icons.Default.LocationOn,
                iconBackgroundColor = Color(0xFFE1F5FE),
                iconTint = Color(0xFF29B6F6),
                onClick = onTransporteClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Asistencia Personal Card
            MenuCard(
                title = stringResource(R.string.attendance),
                subtitle = "Recordatorios y notas",
                icon = Icons.Default.DateRange,
                iconBackgroundColor = Color(0xFFF3E5F5),
                iconTint = Color(0xFFAB47BC),
                badgeCount = 2,
                onClick = onAsistenciaClick
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    badgeCount: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Badge (si existe)
                    badgeCount?.let { count ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = count.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}