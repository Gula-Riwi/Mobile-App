package com.obelixq.app.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obelixq.app.data.model.Appointment
import com.obelixq.app.data.model.Business
import com.obelixq.app.data.model.CreateAppointmentRequest
import com.obelixq.app.data.model.Service
import com.obelixq.app.data.repository.AppointmentRepository
import com.obelixq.app.data.repository.BusinessRepository
import com.obelixq.app.data.repository.ServiceRepository
import com.obelixq.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de agendamiento (BookingScreen)
 *
 * Maneja:
 * - Selección de servicio
 * - Selección de fecha
 * - Selección de hora (horarios disponibles)
 * - Confirmación de cita
 */
class BookingViewModel(
        private val businessRepository: BusinessRepository = BusinessRepository(),
        private val serviceRepository: ServiceRepository = ServiceRepository(),
        private val appointmentRepository: AppointmentRepository =
                AppointmentRepository.getInstance(
                        businessRepository = BusinessRepository(),
                        serviceRepository = ServiceRepository(),
                        userRepository = UserRepository()
                )
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    // Negocio seleccionado
    private val _business = MutableStateFlow<Business?>(null)
    val business: StateFlow<Business?> = _business.asStateFlow()

    // Servicio seleccionado
    private val _selectedService = MutableStateFlow<Service?>(null)
    val selectedService: StateFlow<Service?> = _selectedService.asStateFlow()

    // Fecha seleccionada (timestamp)
    private val _selectedDate = MutableStateFlow<Long?>(null)
    val selectedDate: StateFlow<Long?> = _selectedDate.asStateFlow()

    // Hora seleccionada (timestamp completo: fecha + hora)
    private val _selectedTimeSlot = MutableStateFlow<Long?>(null)
    val selectedTimeSlot: StateFlow<Long?> = _selectedTimeSlot.asStateFlow()

    // Horarios disponibles para la fecha seleccionada
    private val _availableTimeSlots = MutableStateFlow<List<Long>>(emptyList())
    val availableTimeSlots: StateFlow<List<Long>> = _availableTimeSlots.asStateFlow()

    /** Inicializa el proceso de agendamiento cargando el negocio y servicio */
    fun initialize(businessId: String, serviceId: String) {
        viewModelScope.launch {
            try {
                val business = businessRepository.getBusinessById(businessId)
                val service = serviceRepository.getServiceById(serviceId)

                if (business == null || service == null) {
                    _uiState.value = BookingUiState.Error("Negocio o servicio no encontrado")
                    return@launch
                }

                _business.value = business
                _selectedService.value = service
                _uiState.value = BookingUiState.SelectingDate
            } catch (e: Exception) {
                _uiState.value = BookingUiState.Error(e.message ?: "Error al cargar datos")
            }
        }
    }

    /**
     * Usuario selecciona una fecha en el calendario Carga los horarios disponibles para esa fecha
     */
    fun selectDate(dateMillis: Long) {
        _selectedDate.value = dateMillis
        _selectedTimeSlot.value = null // Resetear hora seleccionada

        val businessId = _business.value?.id ?: return

        _uiState.value = BookingUiState.LoadingTimeSlots

        viewModelScope.launch {
            try {
                val slots =
                        appointmentRepository.getAvailableTimeSlots(
                                businessId = businessId,
                                dateMillis = dateMillis
                        )
                _availableTimeSlots.value = slots
                _uiState.value = BookingUiState.SelectingTime
            } catch (e: Exception) {
                _uiState.value = BookingUiState.Error(e.message ?: "Error al cargar horarios")
            }
        }
    }

    /** Usuario selecciona un horario específico */
    fun selectTimeSlot(timeSlotMillis: Long) {
        _selectedTimeSlot.value = timeSlotMillis
        _uiState.value = BookingUiState.ReadyToConfirm
    }

    /** Confirma la cita Crea el appointment en el repository */
    fun confirmAppointment(userId: String, notes: String? = null) {
        val businessId = _business.value?.id
        val serviceId = _selectedService.value?.id
        val timeSlot = _selectedTimeSlot.value

        // Validaciones
        if (businessId == null || serviceId == null || timeSlot == null) {
            _uiState.value = BookingUiState.Error("Faltan datos para confirmar la cita")
            return
        }

        _uiState.value = BookingUiState.ConfirmingAppointment

        viewModelScope.launch {
            val result =
                    appointmentRepository.createAppointment(
                            CreateAppointmentRequest(
                                    userId = userId,
                                    businessId = businessId,
                                    serviceId = serviceId,
                                    appointmentDate = timeSlot,
                                    notes = notes
                            )
                    )

            result
                    .onSuccess { appointment ->
                        _uiState.value = BookingUiState.AppointmentConfirmed(appointment)
                        // TODO: Aquí se dispararían los bots:
                        // - Notificación push al dueño
                        // - Bot Notificador envía WhatsApp al usuario
                    }
                    .onFailure { exception ->
                        _uiState.value =
                                BookingUiState.Error(
                                        exception.message ?: "Error al confirmar la cita"
                                )
                    }
        }
    }

    /** Resetea el estado para iniciar un nuevo agendamiento */
    fun reset() {
        _business.value = null
        _selectedService.value = null
        _selectedDate.value = null
        _selectedTimeSlot.value = null
        _availableTimeSlots.value = emptyList()
        _uiState.value = BookingUiState.Idle
    }
}

/** Estados posibles del flujo de agendamiento */
sealed class BookingUiState {
    object Idle : BookingUiState()
    object SelectingDate : BookingUiState() // Usuario debe seleccionar fecha
    object LoadingTimeSlots : BookingUiState() // Cargando horarios disponibles
    object SelectingTime : BookingUiState() // Usuario debe seleccionar hora
    object ReadyToConfirm : BookingUiState() // Todo listo para confirmar
    object ConfirmingAppointment : BookingUiState() // Creando la cita
    data class AppointmentConfirmed(val appointment: Appointment) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}
