package com.obelixq.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.obelixq.app.data.model.User
import com.obelixq.app.ui.appointments.MyAppointmentsScreen
import com.obelixq.app.ui.auth.LoginScreen
import com.obelixq.app.ui.auth.RegisterScreen
import com.obelixq.app.ui.booking.BookingScreen
import com.obelixq.app.ui.detail.BusinessDetailScreen
import com.obelixq.app.ui.home.HomeScreen
import com.obelixq.app.ui.profile.ProfileScreen

/**
 * Navegación principal de la app
 *
 * Maneja:
 * - Navegación entre pantallas
 * - Bottom navigation
 * - Autenticación
 */
@Composable
fun ObelixNavGraph(currentUser: User?, onLoginSuccess: (User) -> Unit, onLogout: () -> Unit) {
    val navController = rememberNavController()

    // Si no hay usuario, mostrar login
    val startDestination =
            if (currentUser == null) {
                Screen.Login.route
            } else {
                Screen.Home.route
            }

    NavHost(navController = navController, startDestination = startDestination) {
        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = { user ->
                        // Actualizar usuario y navegar
                        onLoginSuccess(user)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
            )
        }

        // Register
        composable(Screen.Register.route) {
            RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = { user ->
                        // Actualizar usuario y navegar
                        onLoginSuccess(user)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
            )
        }

        // Home (con bottom navigation)
        composable(Screen.Home.route) {
            MainScreen(
                    navController = navController,
                    currentUser = currentUser,
                    onLogout = onLogout
            )
        }

        // Business Detail
        composable(
                route = Screen.BusinessDetail.route,
                arguments = listOf(navArgument("businessId") { type = NavType.StringType })
        ) { backStackEntry ->
            val businessId = backStackEntry.arguments?.getString("businessId") ?: return@composable

            BusinessDetailScreen(
                    businessId = businessId,
                    onNavigateBack = { navController.popBackStack() },
                    onBookService = { businessId, serviceId ->
                        navController.navigate(Screen.Booking.createRoute(businessId, serviceId))
                    }
            )
        }

        // Booking
        composable(
                route = Screen.Booking.route,
                arguments =
                        listOf(
                                navArgument("businessId") { type = NavType.StringType },
                                navArgument("serviceId") { type = NavType.StringType }
                        )
        ) { backStackEntry ->
            val businessId = backStackEntry.arguments?.getString("businessId") ?: return@composable
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
            val userId = currentUser?.id ?: return@composable

            BookingScreen(
                    businessId = businessId,
                    serviceId = serviceId,
                    userId = userId,
                    onNavigateBack = { navController.popBackStack() },
                    onBookingConfirmed = {
                        // Volver al Home después de confirmar
                        // El usuario puede ir a "Mis Citas" desde el bottom nav
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
            )
        }
    }
}

/** Pantalla principal con bottom navigation */
@Composable
fun MainScreen(navController: NavHostController, currentUser: User?, onLogout: () -> Unit) {
    val nestedNavController = rememberNavController()

    // Items del bottom navigation
    val bottomNavItems =
            listOf(
                    BottomNavItem("Inicio", Screen.Home.route, Icons.Default.Home),
                    BottomNavItem(
                            "Mis Citas",
                            Screen.MyAppointments.route,
                            Icons.Default.CalendarToday
                    ),
                    BottomNavItem("Perfil", Screen.Profile.route, Icons.Default.Person)
            )

    Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected =
                                        currentDestination?.hierarchy?.any {
                                            it.route == item.route
                                        } == true,
                                onClick = {
                                    nestedNavController.navigate(item.route) {
                                        popUpTo(
                                                nestedNavController.graph.findStartDestination().id
                                        ) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                        )
                    }
                }
            }
    ) { innerPadding ->
        NavHost(
                navController = nestedNavController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
        ) {
            // Home
            composable(Screen.Home.route) {
                HomeScreen(
                        onBusinessClick = { businessId ->
                            navController.navigate(Screen.BusinessDetail.createRoute(businessId))
                        }
                )
            }

            // My Appointments
            composable(Screen.MyAppointments.route) {
                val userId = currentUser?.id ?: return@composable
                MyAppointmentsScreen(
                        userId = userId,
                        onAppointmentClick = { appointmentId ->
                            // TODO: Navegar a detalle de cita
                        }
                )
            }

            // Profile
            composable(Screen.Profile.route) {
                currentUser?.let { user -> ProfileScreen(user = user, onLogout = onLogout) }
            }
        }
    }
}

/** Item del bottom navigation */
data class BottomNavItem(
        val label: String,
        val route: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
)
