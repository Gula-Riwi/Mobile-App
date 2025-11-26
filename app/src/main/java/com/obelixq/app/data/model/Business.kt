package com.obelixq.app.data.model

/**
 * Modelo de datos para un Negocio/Emprendimiento en ObelixQ
 *
 * Equivalente en Spring Boot: @Entity Business
 *
 * Representa un negocio que ofrece servicios y puede recibir citas.
 */
data class Business(
        val id: String, // UUID del negocio
        val name: String, // Nombre del negocio: "Barbería El Corte Perfecto"
        val description: String, // Descripción breve del negocio
        val category: BusinessCategory, // Categoría (Barbería, Spa, Abogado, etc.)
        val imageUrl: String, // URL de la imagen/logo del negocio
        val rating: Float = 0f, // Rating promedio (0.0 a 5.0)
        val totalReviews: Int = 0, // Número total de reseñas

        // Información de contacto
        val phone: String, // Teléfono del negocio
        val email: String, // Email del negocio
        val address: String, // Dirección física
        val city: String, // Ciudad

        // Horarios de atención
        val openingTime: String, // Ej: "09:00"
        val closingTime: String, // Ej: "18:00"
        val workingDays: List<String>, // Ej: ["Lunes", "Martes", "Miércoles", ...]

        // Metadata
        val ownerId: String, // ID del dueño del negocio
        val isActive: Boolean = true, // Si el negocio está activo
        val createdAt: Long = System.currentTimeMillis()
)
