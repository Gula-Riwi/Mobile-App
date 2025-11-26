package com.obelixq.app.data.model

/**
 * Modelo de datos para una Cita/Reserva en ObelixQ
 *
 * Equivalente en Spring Boot: @Entity Appointment con relaciones @ManyToOne
 *
 * Representa una cita agendada por un usuario en un negocio para un servicio específico. Esta es la
 * entidad CORE del sistema - todo gira alrededor de las citas.
 */
data class Appointment(
        val id: String, // UUID de la cita

        // Relaciones (en backend serían @ManyToOne)
        val userId: String, // ID del usuario que agenda
        val businessId: String, // ID del negocio
        val serviceId: String, // ID del servicio seleccionado

        // Información de la cita
        val appointmentDate: Long, // Timestamp de la fecha/hora de la cita
        val status: AppointmentStatus, // Estado actual (Pendiente, Confirmada, etc.)
        val notes: String? = null, // Notas adicionales del usuario

        // Metadata
        val createdAt: Long = System.currentTimeMillis(), // Cuándo se creó la cita
        val updatedAt: Long = System.currentTimeMillis() // Última actualización
)

/**
 * DTO para crear una nueva cita Lo que se envía al backend cuando el usuario confirma el
 * agendamiento
 */
data class CreateAppointmentRequest(
        val userId: String,
        val businessId: String,
        val serviceId: String,
        val appointmentDate: Long,
        val notes: String? = null
)

/**
 * Modelo enriquecido para mostrar en la UI Combina Appointment con los datos relacionados
 * (Business, Service, User)
 *
 * En Spring Boot esto sería el resultado de un JOIN o un DTO personalizado
 */
data class AppointmentDetail(
        val appointment: Appointment,
        val business: Business,
        val service: Service,
        val user: User
)
