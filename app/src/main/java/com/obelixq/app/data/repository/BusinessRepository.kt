package com.obelixq.app.data.repository

import com.obelixq.app.data.model.Business
import com.obelixq.app.data.model.BusinessCategory
import kotlinx.coroutines.delay

/**
 * Repository para gestionar negocios
 *
 * Equivalente en Spring Boot: @Repository BusinessRepository extends JpaRepository
 *
 * Por ahora usa datos MOCK (hardcodeados). Cuando tengas el backend, aquí usarás Retrofit para
 * llamar a tu API REST.
 */
class BusinessRepository {

    // Simulación de base de datos con negocios mock
    private val mockBusinesses =
            listOf(
                    Business(
                            id = "1",
                            name = "Barbería El Corte Perfecto",
                            description =
                                    "Barbería moderna con más de 10 años de experiencia. Especialistas en cortes clásicos y modernos.",
                            category = BusinessCategory.BARBERSHOP,
                            imageUrl =
                                    "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800",
                            rating = 4.8f,
                            totalReviews = 127,
                            phone = "+57 300 123 4567",
                            email = "contacto@corteperfecto.com",
                            address = "Calle 45 #23-15",
                            city = "Bogotá",
                            openingTime = "09:00",
                            closingTime = "19:00",
                            workingDays =
                                    listOf(
                                            "Lunes",
                                            "Martes",
                                            "Miércoles",
                                            "Jueves",
                                            "Viernes",
                                            "Sábado"
                                    ),
                            ownerId = "owner1"
                    ),
                    Business(
                            id = "2",
                            name = "Spa Relajación Total",
                            description =
                                    "Centro de bienestar y relajación. Masajes terapéuticos, faciales y tratamientos corporales.",
                            category = BusinessCategory.SPA,
                            imageUrl =
                                    "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800",
                            rating = 4.9f,
                            totalReviews = 89,
                            phone = "+57 301 987 6543",
                            email = "info@sparelajacion.com",
                            address = "Carrera 15 #67-89",
                            city = "Medellín",
                            openingTime = "10:00",
                            closingTime = "20:00",
                            workingDays =
                                    listOf(
                                            "Lunes",
                                            "Martes",
                                            "Miércoles",
                                            "Jueves",
                                            "Viernes",
                                            "Sábado",
                                            "Domingo"
                                    ),
                            ownerId = "owner2"
                    ),
                    Business(
                            id = "3",
                            name = "Abogados Pérez & Asociados",
                            description =
                                    "Firma de abogados especializada en derecho civil, laboral y comercial. Más de 15 años de experiencia.",
                            category = BusinessCategory.LAWYER,
                            imageUrl =
                                    "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800",
                            rating = 4.7f,
                            totalReviews = 54,
                            phone = "+57 302 456 7890",
                            email = "contacto@perezabogados.com",
                            address = "Avenida 80 #45-23, Oficina 501",
                            city = "Cali",
                            openingTime = "08:00",
                            closingTime = "18:00",
                            workingDays =
                                    listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"),
                            ownerId = "owner3"
                    ),
                    Business(
                            id = "4",
                            name = "Consultora Digital Pro",
                            description =
                                    "Consultoría en transformación digital, marketing digital y estrategia empresarial.",
                            category = BusinessCategory.CONSULTANT,
                            imageUrl =
                                    "https://images.unsplash.com/photo-1552664730-d307ca884978?w=800",
                            rating = 4.6f,
                            totalReviews = 32,
                            phone = "+57 303 111 2222",
                            email = "hola@digitalpro.com",
                            address = "Calle 100 #19-45",
                            city = "Bogotá",
                            openingTime = "09:00",
                            closingTime = "17:00",
                            workingDays =
                                    listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"),
                            ownerId = "owner4"
                    ),
                    Business(
                            id = "5",
                            name = "Clínica Dental Sonrisa Perfecta",
                            description =
                                    "Odontología general, ortodoncia, implantes y blanqueamiento dental. Tecnología de punta.",
                            category = BusinessCategory.DENTIST,
                            imageUrl =
                                    "https://images.unsplash.com/photo-1606811841689-23dfddce3e95?w=800",
                            rating = 4.9f,
                            totalReviews = 156,
                            phone = "+57 304 333 4444",
                            email = "citas@sonrisaperfecta.com",
                            address = "Carrera 7 #34-56",
                            city = "Bogotá",
                            openingTime = "08:00",
                            closingTime = "18:00",
                            workingDays =
                                    listOf(
                                            "Lunes",
                                            "Martes",
                                            "Miércoles",
                                            "Jueves",
                                            "Viernes",
                                            "Sábado"
                                    ),
                            ownerId = "owner5"
                    ),
                    Business(
                            id = "6",
                            name = "Psicología Integral",
                            description =
                                    "Atención psicológica individual, de pareja y familiar. Terapia cognitivo-conductual.",
                            category = BusinessCategory.PSYCHOLOGIST,
                            imageUrl =
                                    "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=800",
                            rating = 4.8f,
                            totalReviews = 67,
                            phone = "+57 305 555 6666",
                            email = "contacto@psicologiaintegral.com",
                            address = "Calle 85 #12-34",
                            city = "Medellín",
                            openingTime = "09:00",
                            closingTime = "19:00",
                            workingDays =
                                    listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"),
                            ownerId = "owner6"
                    )
            )

    /**
     * Obtiene todos los negocios
     *
     * suspend = función asíncrona (como CompletableFuture en Java) delay() = simula latencia de red
     * (500ms)
     */
    suspend fun getAllBusinesses(): List<Business> {
        delay(500) // Simula llamada a API
        return mockBusinesses
    }

    /** Obtiene un negocio por ID */
    suspend fun getBusinessById(id: String): Business? {
        delay(300)
        return mockBusinesses.find { it.id == id }
    }

    /** Filtra negocios por categoría */
    suspend fun getBusinessesByCategory(category: BusinessCategory): List<Business> {
        delay(400)
        return mockBusinesses.filter { it.category == category }
    }

    /** Busca negocios por nombre o descripción */
    suspend fun searchBusinesses(query: String): List<Business> {
        delay(400)
        if (query.isBlank()) return mockBusinesses

        val lowerQuery = query.lowercase()
        return mockBusinesses.filter {
            it.name.lowercase().contains(lowerQuery) ||
                    it.description.lowercase().contains(lowerQuery)
        }
    }

    /** Obtiene negocios por ciudad */
    suspend fun getBusinessesByCity(city: String): List<Business> {
        delay(400)
        return mockBusinesses.filter { it.city.equals(city, ignoreCase = true) }
    }
}
