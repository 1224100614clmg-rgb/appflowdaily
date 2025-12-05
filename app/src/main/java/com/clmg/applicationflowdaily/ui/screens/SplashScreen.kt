package com.clmg.applicationflowdaily.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pantalla de Splash - Pantalla inicial de bienvenida
 *
 * UBICACIÓN: ui/screens/SplashScreen.kt
 *
 * Basada en la primera imagen del documento:
 * - Gradiente azul de fondo
 * - Logo con estrella
 * - Título "Flows Daily"
 * - Subtítulo "Simplifica tu día"
 * - Botón "Comenzar"
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    // Gradiente azul (cian a morado)
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00D4FF), // Cian claro
            Color(0xFF6B7FFF)  // Morado azulado
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Logo - Estrella en cuadro azul redondeado
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color(0xFF4A90E2),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⭐",
                    fontSize = 50.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Título principal
            Text(
                text = "Flows Daily",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Simplifica tu día",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Botón Comenzar
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6B7FFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Comenzar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}