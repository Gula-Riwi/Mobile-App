package com.obelixq.app.data.model

/**
 * Modelo de datos para el Usuario/Cliente de ObelixQ
 *
 * Equivalente en Spring Boot: @Entity User con @Table("users")
 *
 * Esta clase almacena toda la información del usuario que necesitan los bots para contactarlo y
 * personalizar los mensajes.
 */
data class User(
        val id: String, // UUID o ID generado por el backend
        val fullName: String, // Nombre completo - Para personalizar: "Hola Juan..."
        val email: String, // Email - Para login y confirmaciones por correo
        val phone: String, // Teléfono - CRÍTICO para WhatsApp de los bots
        val password: String, // Password hasheado (en backend con BCrypt)
        val profilePicture: String? = null, // URL de la foto (opcional)
        val createdAt: Long = System.currentTimeMillis() // Timestamp de registro
)

/**
 * DTO para el formulario de registro Solo contiene los campos que el usuario llena en el
 * RegisterScreen
 *
 * Equivalente en Spring Boot: @RequestBody RegisterRequest
 */
data class RegisterRequest(
        val fullName: String,
        val email: String,
        val phone: String,
        val password: String
)

/**
 * DTO para el formulario de login
 *
 * Equivalente en Spring Boot: @RequestBody LoginRequest
 */
data class LoginRequest(val email: String, val password: String)

/**
 * Respuesta del backend después de login/register exitoso
 *
 * Equivalente en Spring Boot: AuthResponse con el token JWT
 */
data class AuthResponse(
        val user: User,
        val token: String // JWT token para autenticar futuras peticiones
)
