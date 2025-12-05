package com.clmg.applicationflowdaily

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.clmg.applicationflowdaily.navigation.AppNavigation
import com.clmg.applicationflowdaily.ui.viewmodel.AuthViewModel
import com.clmg.applicationflowdaily.ui.viewmodel.SettingsViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permiso de notificaciones
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ViewModel para configuración (COMPARTIDO EN TODA LA APP)
            val settingsViewModel: SettingsViewModel = viewModel()
            val preferences by settingsViewModel.userPreferences.collectAsState()

            // Aplicar idioma
            setAppLocale(this, preferences.language)

            // Aplicar tema a TODA LA APP
            FlowDailyTheme(darkTheme = preferences.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()

                    DisposableEffect(Unit) {
                        requestNotificationPermission()
                        onDispose { }
                    }

                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

/**
 * Cambia el idioma de TODA la aplicación
 */
fun setAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

/**
 * Tema que se aplica a TODA la aplicación
 */
@Composable
fun FlowDailyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF7986CB),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFF5C6BC0),
            onPrimaryContainer = Color(0xFFFFFFFF),

            secondary = Color(0xFF42A5F5),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFF1976D2),
            onSecondaryContainer = Color(0xFFFFFFFF),

            background = Color(0xFF121212),
            onBackground = Color(0xFFE0E0E0),

            surface = Color(0xFF1E1E1E),
            onSurface = Color(0xFFE0E0E0),
            surfaceVariant = Color(0xFF2C2C2C),
            onSurfaceVariant = Color(0xFFB0B0B0),

            outline = Color(0xFF757575),
            outlineVariant = Color(0xFF3C3C3C),

            error = Color(0xFFE53935),
            onError = Color(0xFFFFFFFF)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF7986CB),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE8EAF6),
            onPrimaryContainer = Color(0xFF283593),

            secondary = Color(0xFF42A5F5),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE3F2FD),
            onSecondaryContainer = Color(0xFF0D47A1),

            background = Color(0xFFF5F5F5),
            onBackground = Color(0xFF000000),

            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF000000),
            surfaceVariant = Color(0xFFF5F5F5),
            onSurfaceVariant = Color(0xFF666666),

            outline = Color(0xFFBDBDBD),
            outlineVariant = Color(0xFFE0E0E0),

            error = Color(0xFFE53935),
            onError = Color(0xFFFFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}