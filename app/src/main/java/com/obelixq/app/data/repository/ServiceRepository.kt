package com.obelixq.app.data.repository

import com.obelixq.app.data.model.Service
import kotlinx.coroutines.delay

/**
 * Repository para gestionar servicios ofrecidos por los negocios
 *
 * Equivalente en Spring Boot: @Repository ServiceRepository extends JpaRepository
 *
 * Cada negocio tiene múltiples servicios con diferentes precios y duraciones.
 */
class ServiceRepository {

    // Servicios mock organizados por negocio
    private val mockServices =
            listOf(
                    // Barbería El Corte Perfecto (businessId = "1")
                    Service(
                            id = "s1",
                            businessId = "1",
                            name = "Corte de Cabello Clásico",
                            description = "Corte tradicional con tijera y máquina. Incluye lavado.",
                            price = 25000.0,
                            durationMinutes = 30
                    ),
                    Service(
                            id = "s2",
                            businessId = "1",
                            name = "Corte Moderno + Barba",
                            description =
                                    "Corte moderno con diseño + arreglo de barba. Incluye lavado y productos premium.",
                            price = 45000.0,
                            durationMinutes = 60
                    ),
                    Service(
                            id = "s3",
                            businessId = "1",
                            name = "Afeitado Clásico",
                            description =
                                    "Afeitado tradicional con navaja, toalla caliente y productos de alta calidad.",
                            price = 30000.0,
                            durationMinutes = 45
                    ),

                    // Spa Relajación Total (businessId = "2")
                    Service(
                            id = "s4",
                            businessId = "2",
                            name = "Masaje Relajante",
                            description =
                                    "Masaje corporal completo con aceites esenciales. Ideal para aliviar el estrés.",
                            price = 80000.0,
                            durationMinutes = 60
                    ),
                    Service(
                            id = "s5",
                            businessId = "2",
                            name = "Masaje Terapéutico",
                            description =
                                    "Masaje profundo para aliviar tensiones musculares y dolores crónicos.",
                            price = 100000.0,
                            durationMinutes = 90
                    ),
                    Service(
                            id = "s6",
                            businessId = "2",
                            name = "Facial Hidratante",
                            description =
                                    "Tratamiento facial con limpieza profunda, exfoliación e hidratación.",
                            price = 70000.0,
                            durationMinutes = 75
                    ),
                    Service(
                            id = "s7",
                            businessId = "2",
                            name = "Paquete Spa Completo",
                            description =
                                    "Masaje + Facial + Manicure. Experiencia completa de relajación.",
                            price = 180000.0,
                            durationMinutes = 180
                    ),

                    // Abogados Pérez & Asociados (businessId = "3")
                    Service(
                            id = "s8",
                            businessId = "3",
                            name = "Consulta Legal General",
                            description =
                                    "Asesoría legal en derecho civil, laboral o comercial. Primera consulta.",
                            price = 150000.0,
                            durationMinutes = 60
                    ),
                    Service(
                            id = "s9",
                            businessId = "3",
                            name = "Redacción de Contratos",
                            description =
                                    "Elaboración de contratos personalizados según sus necesidades.",
                            price = 300000.0,
                            durationMinutes = 120
                    ),
                    Service(
                            id = "s10",
                            businessId = "3",
                            name = "Representación Legal",
                            description =
                                    "Representación en procesos judiciales. Precio por audiencia.",
                            price = 500000.0,
                            durationMinutes = 180
                    ),

                    // Consultora Digital Pro (businessId = "4")
                    Service(
                            id = "s11",
                            businessId = "4",
                            name = "Consultoría en Marketing Digital",
                            description =
                                    "Análisis y estrategia de marketing digital para tu negocio.",
                            price = 200000.0,
                            durationMinutes = 90
                    ),
                    Service(
                            id = "s12",
                            businessId = "4",
                            name = "Auditoría de Redes Sociales",
                            description =
                                    "Evaluación completa de tu presencia en redes sociales con plan de mejora.",
                            price = 250000.0,
                            durationMinutes = 120
                    ),
                    Service(
                            id = "s13",
                            businessId = "4",
                            name = "Transformación Digital",
                            description =
                                    "Consultoría integral para digitalizar tu empresa. Incluye diagnóstico y roadmap.",
                            price = 800000.0,
                            durationMinutes = 240
                    ),

                    // Clínica Dental Sonrisa Perfecta (businessId = "5")
                    Service(
                            id = "s14",
                            businessId = "5",
                            name = "Limpieza Dental",
                            description = "Profilaxis dental profesional con ultrasonido y pulido.",
                            price = 80000.0,
                            durationMinutes = 45
                    ),
                    Service(
                            id = "s15",
                            businessId = "5",
                            name = "Blanqueamiento Dental",
                            description =
                                    "Blanqueamiento dental con luz LED. Resultados inmediatos.",
                            price = 350000.0,
                            durationMinutes = 90
                    ),
                    Service(
                            id = "s16",
                            businessId = "5",
                            name = "Consulta Odontológica",
                            description =
                                    "Valoración general del estado dental con radiografía incluida.",
                            price = 60000.0,
                            durationMinutes = 30
                    ),
                    Service(
                            id = "s17",
                            businessId = "5",
                            name = "Ortodoncia - Valoración",
                            description = "Consulta especializada para tratamiento de ortodoncia.",
                            price = 100000.0,
                            durationMinutes = 60
                    ),

                    // Psicología Integral (businessId = "6")
                    Service(
                            id = "s18",
                            businessId = "6",
                            name = "Terapia Individual",
                            description =
                                    "Sesión de terapia psicológica individual. Enfoque cognitivo-conductual.",
                            price = 120000.0,
                            durationMinutes = 60
                    ),
                    Service(
                            id = "s19",
                            businessId = "6",
                            name = "Terapia de Pareja",
                            description =
                                    "Sesión de terapia para parejas. Resolución de conflictos y comunicación.",
                            price = 180000.0,
                            durationMinutes = 90
                    ),
                    Service(
                            id = "s20",
                            businessId = "6",
                            name = "Terapia Familiar",
                            description =
                                    "Sesión de terapia familiar. Dinámicas familiares y resolución de problemas.",
                            price = 200000.0,
                            durationMinutes = 90
                    ),
                    Service(
                            id = "s21",
                            businessId = "6",
                            name = "Primera Consulta",
                            description =
                                    "Evaluación inicial y diagnóstico. Incluye plan de tratamiento.",
                            price = 100000.0,
                            durationMinutes = 75
                    )
            )

    /**
     * Obtiene todos los servicios de un negocio específico Este es el método más usado - cuando el
     * usuario ve el detalle de un negocio
     */
    suspend fun getServicesByBusinessId(businessId: String): List<Service> {
        delay(300)
        return mockServices.filter { it.businessId == businessId }
    }

    /** Obtiene un servicio por ID */
    suspend fun getServiceById(id: String): Service? {
        delay(200)
        return mockServices.find { it.id == id }
    }

    /** Obtiene todos los servicios (raramente usado) */
    suspend fun getAllServices(): List<Service> {
        delay(400)
        return mockServices
    }

    /** Busca servicios por nombre o descripción */
    suspend fun searchServices(query: String): List<Service> {
        delay(300)
        if (query.isBlank()) return mockServices

        val lowerQuery = query.lowercase()
        return mockServices.filter {
            it.name.lowercase().contains(lowerQuery) ||
                    it.description.lowercase().contains(lowerQuery)
        }
    }
}
