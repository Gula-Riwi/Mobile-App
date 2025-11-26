package com.obelixq.app.ui.navigation

/**
 * Rutas de navegación de la app
 *
 * Sealed class = conjunto cerrado de valores (como enum mejorado) Cada ruta puede tener argumentos
 */
sealed class Screen(val route: String) {
    // Autenticación
    object Login : Screen("login")
    object Register : Screen("register")

    // Main (con bottom navigation)
    object Home : Screen("home")
    object MyAppointments : Screen("my_appointments")
    object Profile : Screen("profile")

    // Detalle y agendamiento
    object BusinessDetail : Screen("business_detail/{businessId}") {
        fun createRoute(businessId: String) = "business_detail/$businessId"
    }

    object Booking : Screen("booking/{businessId}/{serviceId}") {
        fun createRoute(businessId: String, serviceId: String) = "booking/$businessId/$serviceId"
    }
}
