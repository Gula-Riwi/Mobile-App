package com.obelixq.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obelixq.app.data.model.Business
import com.obelixq.app.data.model.Service
import com.obelixq.app.data.repository.BusinessRepository
import com.obelixq.app.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de detalle del negocio (BusinessDetailScreen)
 *
 * Muestra:
 * - Información completa del negocio
 * - Lista de servicios disponibles
 * - Permite navegar al agendamiento
 */
class BusinessDetailViewModel(
        private val businessRepository: BusinessRepository = BusinessRepository(),
        private val serviceRepository: ServiceRepository = ServiceRepository()
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<BusinessDetailUiState>(BusinessDetailUiState.Loading)
    val uiState: StateFlow<BusinessDetailUiState> = _uiState.asStateFlow()

    // Negocio actual
    private val _business = MutableStateFlow<Business?>(null)
    val business: StateFlow<Business?> = _business.asStateFlow()

    // Servicios del negocio
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    /** Carga el detalle del negocio y sus servicios */
    fun loadBusinessDetail(businessId: String) {
        _uiState.value = BusinessDetailUiState.Loading

        viewModelScope.launch {
            try {
                // Cargar negocio
                val business = businessRepository.getBusinessById(businessId)
                if (business == null) {
                    _uiState.value = BusinessDetailUiState.Error("Negocio no encontrado")
                    return@launch
                }

                // Cargar servicios
                val services = serviceRepository.getServicesByBusinessId(businessId)

                _business.value = business
                _services.value = services
                _uiState.value = BusinessDetailUiState.Success
            } catch (e: Exception) {
                _uiState.value =
                        BusinessDetailUiState.Error(e.message ?: "Error al cargar el negocio")
            }
        }
    }
}

/** Estados posibles de la pantalla de detalle */
sealed class BusinessDetailUiState {
    object Loading : BusinessDetailUiState()
    object Success : BusinessDetailUiState()
    data class Error(val message: String) : BusinessDetailUiState()
}
