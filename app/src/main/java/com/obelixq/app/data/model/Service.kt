package com.obelixq.app.data.model

/**
 * Modelo de datos para un Servicio ofrecido por un Negocio
 *
 * Equivalente en Spring Boot: @Entity Service con @ManyToOne Business
 *
 * Ejemplo: "Corte de Cabello", "Consulta Legal", "Masaje Relajante"
 */
data class Service(
        val id: String, // UUID del servicio
        val businessId: String, // ID del negocio que ofrece este servicio
        val name: String, // Nombre del servicio: "Corte de Cabello"
        val description: String, // Descripción detallada
        val price: Double, // Precio en la moneda local
        val durationMinutes: Int, // Duración en minutos (Ej: 30, 60, 90)
        val imageUrl: String? = null, // Imagen del servicio (opcional)
        val isActive: Boolean = true // Si el servicio está disponible
)
