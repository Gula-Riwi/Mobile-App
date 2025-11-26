package com.obelixq.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obelixq.app.data.model.Business
import com.obelixq.app.data.model.BusinessCategory
import com.obelixq.app.data.repository.BusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de inicio (HomeScreen)
 *
 * Maneja:
 * - Lista de negocios
 * - Búsqueda por nombre
 * - Filtro por categoría
 * - Estado de carga
 */
class HomeViewModel(private val businessRepository: BusinessRepository = BusinessRepository()) :
        ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Categoría seleccionada (null = todas)
    private val _selectedCategory = MutableStateFlow<BusinessCategory?>(null)
    val selectedCategory: StateFlow<BusinessCategory?> = _selectedCategory.asStateFlow()

    // Query de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // Cargar negocios al iniciar
        loadBusinesses()
    }

    /** Carga todos los negocios */
    fun loadBusinesses() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            try {
                val businesses = businessRepository.getAllBusinesses()
                _uiState.value = HomeUiState.Success(businesses)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error al cargar negocios")
            }
        }
    }

    /** Filtra negocios por categoría */
    fun filterByCategory(category: BusinessCategory?) {
        _selectedCategory.value = category
        _searchQuery.value = "" // Limpiar búsqueda

        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            try {
                val businesses =
                        if (category == null) {
                            businessRepository.getAllBusinesses()
                        } else {
                            businessRepository.getBusinessesByCategory(category)
                        }
                _uiState.value = HomeUiState.Success(businesses)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error al filtrar negocios")
            }
        }
    }

    /** Busca negocios por nombre o descripción */
    fun searchBusinesses(query: String) {
        _searchQuery.value = query
        _selectedCategory.value = null // Limpiar filtro de categoría

        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            try {
                val businesses = businessRepository.searchBusinesses(query)
                _uiState.value = HomeUiState.Success(businesses)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error al buscar negocios")
            }
        }
    }

    /** Limpia filtros y búsqueda */
    fun clearFilters() {
        _selectedCategory.value = null
        _searchQuery.value = ""
        loadBusinesses()
    }
}

/** Estados posibles de la pantalla de inicio */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val businesses: List<Business>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
