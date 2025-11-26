package com.obelixq.app.data.model

/**
 * Enum para los estados de una cita
 *
 * Estos estados son críticos para el flujo de los bots:
 * - PENDING: Recién creada, esperando confirmación
 * - CONFIRMED: Bot Notificador envió confirmación
 * - COMPLETED: Servicio realizado (gatillo para Bot de Reputación)
 * - CANCELLED: Cancelada por el usuario o el negocio
 */
enum class AppointmentStatus(val displayName: String) {
    PENDING("Pendiente"),
    CONFIRMED("Confirmada"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada")
}
