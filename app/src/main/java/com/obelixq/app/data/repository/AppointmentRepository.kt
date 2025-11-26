package com.obelixq.app.data.repository

import com.obelixq.app.data.model.Appointment
import com.obelixq.app.data.model.AppointmentDetail
import com.obelixq.app.data.model.AppointmentStatus
import com.obelixq.app.data.model.CreateAppointmentRequest
import java.util.Calendar
import java.util.UUID
import kotlinx.coroutines.delay

/**
 * Repository para gestionar citas/reservas
 *
 * Equivalente en Spring Boot: @Repository AppointmentRepository extends JpaRepository
 *
 * Este es el CORE del sistema - todo gira alrededor de las citas. Cuando una cita cambia de estado,
 * se activan los bots correspondientes.
 *
 * NOTA: Singleton para mantener las citas en memoria durante la sesión de la app
 */
class AppointmentRepository
private constructor(
        private val businessRepository: BusinessRepository,
        private val serviceRepository: ServiceRepository,
        private val userRepository: UserRepository
) {

        companion object {
                @Volatile private var INSTANCE: AppointmentRepository? = null

                fun getInstance(
                        businessRepository: BusinessRepository,
                        serviceRepository: ServiceRepository,
                        userRepository: UserRepository
                ): AppointmentRepository {
                        return INSTANCE
                                ?: synchronized(this) {
                                        INSTANCE
                                                ?: AppointmentRepository(
                                                                businessRepository,
                                                                serviceRepository,
                                                                userRepository
                                                        )
                                                        .also { INSTANCE = it }
                                }
                }
        }

        // Simulación de "base de datos" de citas (persiste durante la sesión)
        private val appointments = mutableListOf<Appointment>()

        /**
         * Crea una nueva cita
         *
         * Equivalente en Spring Boot:
         * @PostMapping("/api/appointments") public Appointment createAppointment(@RequestBody
         * CreateAppointmentRequest request)
         *
         * Flujo después de crear:
         * 1. Se guarda en DB con estado PENDING
         * 2. Se envía notificación push al dueño del negocio
         * 3. Bot Notificador envía WhatsApp al usuario confirmando
         */
        suspend fun createAppointment(request: CreateAppointmentRequest): Result<Appointment> {
                delay(600) // Simula latencia de red

                // Validar que el horario esté disponible
                val isAvailable =
                        isTimeSlotAvailable(
                                businessId = request.businessId,
                                date = request.appointmentDate
                        )

                if (!isAvailable) {
                        return Result.failure(Exception("Este horario ya no está disponible"))
                }

                // Crear la cita
                val appointment =
                        Appointment(
                                id = UUID.randomUUID().toString(),
                                userId = request.userId,
                                businessId = request.businessId,
                                serviceId = request.serviceId,
                                appointmentDate = request.appointmentDate,
                                status = AppointmentStatus.PENDING, // Inicia como PENDING
                                notes = request.notes,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                        )

                appointments.add(appointment)

                // TODO: Aquí se dispararían eventos para los bots:
                // - Notificación push al dueño del negocio
                // - Bot Notificador envía WhatsApp al usuario

                return Result.success(appointment)
        }

        /** Obtiene todas las citas de un usuario Usado en la pantalla "Mis Citas" */
        suspend fun getAppointmentsByUserId(userId: String): List<Appointment> {
                delay(400)
                return appointments.filter { it.userId == userId }.sortedByDescending {
                        it.appointmentDate
                } // Más recientes primero
        }

        /**
         * Obtiene una cita con todos sus detalles (Business, Service, User) Usado para mostrar el
         * detalle completo de una cita
         */
        suspend fun getAppointmentDetail(appointmentId: String): Result<AppointmentDetail> {
                delay(400)

                val appointment =
                        appointments.find { it.id == appointmentId }
                                ?: return Result.failure(Exception("Cita no encontrada"))

                val business =
                        businessRepository.getBusinessById(appointment.businessId)
                                ?: return Result.failure(Exception("Negocio no encontrado"))

                val service =
                        serviceRepository.getServiceById(appointment.serviceId)
                                ?: return Result.failure(Exception("Servicio no encontrado"))

                val user =
                        userRepository.getUserById(appointment.userId)
                                ?: return Result.failure(Exception("Usuario no encontrado"))

                return Result.success(
                        AppointmentDetail(
                                appointment = appointment,
                                business = business,
                                service = service,
                                user = user
                        )
                )
        }

        /**
         * Actualiza el estado de una cita
         *
         * CRÍTICO: Cuando el estado cambia a COMPLETED, se activa el Bot de Reputación
         *
         * Equivalente en Spring Boot:
         * @PatchMapping("/api/appointments/{id}/status") public Appointment
         * updateStatus(@PathVariable String id, @RequestBody AppointmentStatus status)
         */
        suspend fun updateAppointmentStatus(
                appointmentId: String,
                newStatus: AppointmentStatus
        ): Result<Appointment> {
                delay(400)

                val appointment =
                        appointments.find { it.id == appointmentId }
                                ?: return Result.failure(Exception("Cita no encontrada"))

                // Crear copia con nuevo estado
                val updatedAppointment =
                        appointment.copy(status = newStatus, updatedAt = System.currentTimeMillis())

                // Reemplazar en la lista
                val index = appointments.indexOf(appointment)
                appointments[index] = updatedAppointment

                // TODO: Disparar eventos según el estado:
                // - CONFIRMED: Bot envía recordatorio
                // - COMPLETED: Bot de Reputación pide reseña (X horas después)
                // - CANCELLED: Bot notifica cancelación

                return Result.success(updatedAppointment)
        }

        /** Cancela una cita */
        suspend fun cancelAppointment(appointmentId: String): Result<Appointment> {
                return updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED)
        }

        /** Verifica si un horario está disponible Evita doble reserva del mismo horario */
        private fun isTimeSlotAvailable(businessId: String, date: Long): Boolean {
                // Buscar citas en el mismo negocio y fecha
                val existingAppointments =
                        appointments.filter {
                                it.businessId == businessId &&
                                        it.appointmentDate == date &&
                                        it.status != AppointmentStatus.CANCELLED
                        }

                return existingAppointments.isEmpty()
        }

        /**
         * Obtiene horarios disponibles para un negocio en una fecha específica
         *
         * Retorna lista de timestamps disponibles (cada 30 minutos) Ejemplo: [09:00, 09:30, 10:00,
         * 10:30, ...]
         */
        suspend fun getAvailableTimeSlots(businessId: String, dateMillis: Long): List<Long> {
                delay(500)

                val business = businessRepository.getBusinessById(businessId) ?: return emptyList()

                // Parsear horarios del negocio (simplificado)
                val openingHour = business.openingTime.split(":")[0].toInt()
                val closingHour = business.closingTime.split(":")[0].toInt()

                // Generar slots cada 30 minutos
                val availableSlots = mutableListOf<Long>()
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateMillis

                for (hour in openingHour until closingHour) {
                        for (minute in listOf(0, 30)) {
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                calendar.set(Calendar.SECOND, 0)
                                calendar.set(Calendar.MILLISECOND, 0)

                                val slotTime = calendar.timeInMillis

                                // Verificar si está disponible
                                if (isTimeSlotAvailable(businessId, slotTime)) {
                                        availableSlots.add(slotTime)
                                }
                        }
                }

                return availableSlots
        }

        /** Obtiene citas por estado Útil para filtrar en "Mis Citas" */
        suspend fun getAppointmentsByStatus(
                userId: String,
                status: AppointmentStatus
        ): List<Appointment> {
                delay(300)
                return appointments
                        .filter { it.userId == userId && it.status == status }
                        .sortedByDescending { it.appointmentDate }
        }
}
