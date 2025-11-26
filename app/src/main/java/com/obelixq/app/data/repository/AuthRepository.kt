package com.obelixq.app.data.repository

import com.obelixq.app.data.model.AuthResponse
import com.obelixq.app.data.model.LoginRequest
import com.obelixq.app.data.model.RegisterRequest
import com.obelixq.app.data.model.User
import java.util.UUID
import kotlinx.coroutines.delay

/**
 * Repository para autenticación (Login/Register)
 *
 * Equivalente en Spring Boot: @Service AuthService con UserRepository
 *
 * Por ahora simula autenticación con datos mock. Cuando tengas backend, aquí llamarás a endpoints
 * como:
 * - POST /api/auth/register
 * - POST /api/auth/login
 */
class AuthRepository {

    // Simulación de "base de datos" de usuarios registrados
    // En producción esto estaría en tu backend
    private val registeredUsers =
            mutableListOf(
                    User(
                            id = "user1",
                            fullName = "Juan Pérez",
                            email = "juan@example.com",
                            phone = "+57 300 123 4567",
                            password = "password123", // En backend estaría hasheado con BCrypt
                            profilePicture = null
                    ),
                    User(
                            id = "user2",
                            fullName = "María García",
                            email = "maria@example.com",
                            phone = "+57 301 987 6543",
                            password = "password123",
                            profilePicture = null
                    )
            )

    /**
     * Registra un nuevo usuario
     *
     * Equivalente en Spring Boot:
     * @PostMapping("/api/auth/register") public AuthResponse register(@RequestBody RegisterRequest
     * request)
     */
    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        delay(800) // Simula latencia de red

        // Validar que el email no esté registrado
        val existingUser = registeredUsers.find { it.email == request.email }
        if (existingUser != null) {
            return Result.failure(Exception("El email ya está registrado"))
        }

        // Validar que el teléfono no esté registrado
        val existingPhone = registeredUsers.find { it.phone == request.phone }
        if (existingPhone != null) {
            return Result.failure(Exception("El teléfono ya está registrado"))
        }

        // Crear nuevo usuario
        val newUser =
                User(
                        id = UUID.randomUUID().toString(),
                        fullName = request.fullName,
                        email = request.email,
                        phone = request.phone,
                        password = request.password, // En backend: BCrypt.hashpw(password)
                        profilePicture = null,
                        createdAt = System.currentTimeMillis()
                )

        // Guardar en "base de datos"
        registeredUsers.add(newUser)

        // Generar token (en backend sería JWT)
        val token = "mock_token_${newUser.id}_${System.currentTimeMillis()}"

        return Result.success(AuthResponse(user = newUser, token = token))
    }

    /**
     * Inicia sesión con email y password
     *
     * Equivalente en Spring Boot:
     * @PostMapping("/api/auth/login") public AuthResponse login(@RequestBody LoginRequest request)
     */
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        delay(600) // Simula latencia de red

        // Buscar usuario por email
        val user = registeredUsers.find { it.email == request.email }

        if (user == null) {
            return Result.failure(Exception("Email no registrado"))
        }

        // Verificar password
        // En backend: BCrypt.checkpw(request.password, user.password)
        if (user.password != request.password) {
            return Result.failure(Exception("Contraseña incorrecta"))
        }

        // Generar token (en backend sería JWT)
        val token = "mock_token_${user.id}_${System.currentTimeMillis()}"

        return Result.success(AuthResponse(user = user, token = token))
    }

    /** Verifica si un token es válido En backend verificarías el JWT */
    suspend fun validateToken(token: String): Result<User> {
        delay(200)

        // Mock: extraer userId del token
        if (!token.startsWith("mock_token_")) {
            return Result.failure(Exception("Token inválido"))
        }

        val parts = token.split("_")
        if (parts.size < 3) {
            return Result.failure(Exception("Token malformado"))
        }

        val userId = parts[2]
        val user = registeredUsers.find { it.id == userId }

        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Usuario no encontrado"))
        }
    }

    /** Cierra sesión (en backend invalidarías el token) */
    suspend fun logout(): Result<Unit> {
        delay(200)
        // En una app real, aquí borrarías el token del DataStore
        return Result.success(Unit)
    }
}
