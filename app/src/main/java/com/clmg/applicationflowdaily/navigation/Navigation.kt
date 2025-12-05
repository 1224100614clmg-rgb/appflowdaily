package com.clmg.applicationflowdaily.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.clmg.applicationflowdaily.ui.screens.*
import com.clmg.applicationflowdaily.ui.viewmodel.AuthViewModel

sealed class AppScreen(val route: String) {
    object Splash : AppScreen("splash")
    object Login : AppScreen("login")
    object Register : AppScreen("register")
    object Home : AppScreen("home")
    object Profile : AppScreen("profile")
    object Settings : AppScreen("settings")
    object Organizacion : AppScreen("organizacion")
    object Gastos : AppScreen("gastos")
    object Transporte : AppScreen("transporte")
    object Asistencia : AppScreen("asistencia")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Splash.route
    ) {
        // Splash Screen
        composable(AppScreen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(AppScreen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(AppScreen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(AppScreen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(AppScreen.Home.route) {
            HomeScreen(
                viewModel = authViewModel,
                onProfileClick = {
                    navController.navigate(AppScreen.Profile.route)
                },
                onOrganizacionClick = {
                    navController.navigate(AppScreen.Organizacion.route)
                },
                onGastosClick = {
                    navController.navigate(AppScreen.Gastos.route)
                },
                onTransporteClick = {
                    navController.navigate(AppScreen.Transporte.route)
                },
                onAsistenciaClick = {
                    navController.navigate(AppScreen.Asistencia.route)
                }
            )
        }

        // Profile Screen
        composable(AppScreen.Profile.route) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onConfigurationClick = {
                    // NAVEGACIÓN A CONFIGURACIÓN
                    navController.navigate(AppScreen.Settings.route)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Settings Screen (NUEVA PANTALLA)
        composable(AppScreen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Organizacion Screen
        composable(AppScreen.Organizacion.route) {
            OrganizacionScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Gastos Screen
        composable(AppScreen.Gastos.route) {
            GastosScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Transporte Screen
        composable(AppScreen.Transporte.route) {
            TransportScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Asistencia Screen
        composable(AppScreen.Asistencia.route) {
            AsistenciaScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}