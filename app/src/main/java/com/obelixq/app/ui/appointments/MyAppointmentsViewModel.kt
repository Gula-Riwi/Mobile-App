package com.obelixq.app.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obelixq.app.data.model.Appointment
import com.obelixq.app.data.model.AppointmentDetail
import com.obelixq.app.data.model.AppointmentStatus
import com.obelixq.app.data.repository.AppointmentRepository
import com.obelixq.app.data.repository.BusinessRepository
import com.obelixq.app.data.repository.ServiceRepository
import com.obelixq.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla "Mis Citas" (MyAppointmentsScreen)
 *
 * Muestra:
 * - Historial de citas del usuario
 * - Filtros por estado (Próximas, Completadas, Canceladas)
 * - Permite cancelar citas
 */
class MyAppointmentsViewModel(
        private val appointmentRepository: AppointmentRepository =
                AppointmentRepository.getInstance(
                        businessRepository = BusinessRepository(),
                        serviceRepository = ServiceRepository(),
                        userRepository = UserRepository()
                )
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<AppointmentsUiState>(AppointmentsUiState.Loading)
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()

    // Lista de citas
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    // Filtro de estado seleccionado
    private val _selectedFilter = MutableStateFlow<AppointmentFilter>(AppointmentFilter.UPCOMING)
    val selectedFilter: StateFlow<AppointmentFilter> = _selectedFilter.asStateFlow()

    /** Carga las citas del usuario */
    fun loadAppointments(userId: String) {
        _uiState.value = AppointmentsUiState.Loading

        viewModelScope.launch {
            try {
                val appointments = appointmentRepository.getAppointmentsByUserId(userId)
                _appointments.value = appointments
                applyFilter(_selectedFilter.value)
            } catch (e: Exception) {
                _uiState.value = AppointmentsUiState.Error(e.message ?: "Error al cargar citas")
            }
        }
    }

    /** Filtra citas por estado */
    fun filterByStatus(filter: AppointmentFilter) {
        _selectedFilter.value = filter
        applyFilter(filter)
    }

    /** Aplica el filtro a la lista de citas */
    private fun applyFilter(filter: AppointmentFilter) {
        val allAppointments = _appointments.value
        val now = System.currentTimeMillis()

        val filtered =
                when (filter) {
                    AppointmentFilter.UPCOMING -> {
                        // Citas futuras que no están canceladas
                        allAppointments.filter {
                            it.appointmentDate >= now && it.status != AppointmentStatus.CANCELLED
                        }
                    }
                    AppointmentFilter.COMPLETED -> {
                        allAppointments.filter { it.status == AppointmentStatus.COMPLETED }
                    }
                    AppointmentFilter.CANCELLED -> {
                        allAppointments.filter { it.status == AppointmentStatus.CANCELLED }
                    }
                    AppointmentFilter.ALL -> allAppointments
                }

        _uiState.value =
                if (filtered.isEmpty()) {
                    AppointmentsUiState.Empty(filter)
                } else {
                    AppointmentsUiState.Success(filtered)
                }
    }

    /** Cancela una cita */
    fun cancelAppointment(appointmentId: String, userId: String) {
        viewModelScope.launch {
            val result = appointmentRepository.cancelAppointment(appointmentId)

            result
                    .onSuccess {
                        // Recargar citas después de cancelar
                        loadAppointments(userId)
                        // TODO: Bot notifica cancelación al negocio
                    }
                    .onFailure { exception ->
                        _uiState.value =
                                AppointmentsUiState.Error(
                                        exception.message ?: "Error al cancelar la cita"
                                )
                    }
        }
    }

    /** Obtiene el detalle completo de una cita */
    suspend fun getAppointmentDetail(appointmentId: String): Result<AppointmentDetail> {
        return appointmentRepository.getAppointmentDetail(appointmentId)
    }
}

/** Filtros para las citas */
enum class AppointmentFilter(val displayName: String) {
    UPCOMING("Próximas"),
    COMPLETED("Completadas"),
    CANCELLED("Canceladas"),
    ALL("Todas")
}

/** Estados posibles de la pantalla de citas */
sealed class AppointmentsUiState {
    object Loading : AppointmentsUiState()
    data class Success(val appointments: List<Appointment>) : AppointmentsUiState()
    data class Empty(val filter: AppointmentFilter) : AppointmentsUiState()
    data class Error(val message: String) : AppointmentsUiState()
}
