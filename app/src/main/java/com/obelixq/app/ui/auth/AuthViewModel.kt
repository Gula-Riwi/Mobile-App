package com.obelixq.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obelixq.app.data.model.LoginRequest
import com.obelixq.app.data.model.RegisterRequest
import com.obelixq.app.data.model.User
import com.obelixq.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para autenticación (Login/Register)
 *
 * Equivalente en Spring Boot: @Service AuthService
 *
 * Maneja el estado de las pantallas de login y registro. Se comunica con AuthRepository para hacer
 * las operaciones.
 */
class AuthViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Usuario autenticado
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /** Inicia sesión con email y password */
    fun login(email: String, password: String) {
        // Validaciones básicas
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email y contraseña son requeridos")
            return
        }

        // Mostrar loading
        _uiState.value = AuthUiState.Loading

        // Llamar al repository (asíncrono)
        viewModelScope.launch {
            val result = authRepository.login(LoginRequest(email = email, password = password))

            result
                    .onSuccess { authResponse ->
                        _currentUser.value = authResponse.user
                        _uiState.value = AuthUiState.Success(authResponse.user)
                        // TODO: Guardar token en DataStore
                    }
                    .onFailure { exception ->
                        _uiState.value =
                                AuthUiState.Error(exception.message ?: "Error al iniciar sesión")
                    }
        }
    }

    /** Registra un nuevo usuario */
    fun register(fullName: String, email: String, phone: String, password: String) {
        // Validaciones
        if (fullName.isBlank()) {
            _uiState.value = AuthUiState.Error("El nombre es requerido")
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = AuthUiState.Error("Email inválido")
            return
        }
        if (phone.isBlank()) {
            _uiState.value = AuthUiState.Error("El teléfono es requerido")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        // Mostrar loading
        _uiState.value = AuthUiState.Loading

        // Llamar al repository
        viewModelScope.launch {
            val result =
                    authRepository.register(
                            RegisterRequest(
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    password = password
                            )
                    )

            result
                    .onSuccess { authResponse ->
                        _currentUser.value = authResponse.user
                        _uiState.value = AuthUiState.Success(authResponse.user)
                        // TODO: Guardar token en DataStore
                    }
                    .onFailure { exception ->
                        _uiState.value =
                                AuthUiState.Error(exception.message ?: "Error al registrarse")
                    }
        }
    }

    /** Cierra sesión */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _uiState.value = AuthUiState.Idle
            // TODO: Borrar token del DataStore
        }
    }

    /** Resetea el estado de la UI Útil para limpiar errores después de mostrarlos */
    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }
}

/**
 * Estados posibles de la UI de autenticación
 *
 * Sealed class = enum mejorado que puede tener datos asociados
 */
sealed class AuthUiState {
    object Idle : AuthUiState() // Estado inicial
    object Loading : AuthUiState() // Cargando (mostrar spinner)
    data class Success(val user: User) : AuthUiState() // Login/Register exitoso
    data class Error(val message: String) : AuthUiState() // Error (mostrar mensaje)
}
