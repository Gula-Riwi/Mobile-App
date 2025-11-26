package com.obelixq.app.data.repository

import com.obelixq.app.data.model.User
import kotlinx.coroutines.delay

/**
 * Repository para gestionar usuarios
 *
 * Equivalente en Spring Boot: @Repository UserRepository extends JpaRepository
 *
 * Por ahora trabaja con los usuarios que AuthRepository crea. Cuando tengas backend, aquí llamarás
 * a endpoints como:
 * - GET /api/users/{id}
 * - PUT /api/users/{id}
 */
class UserRepository {

    // Esta lista debería compartirse con AuthRepository
    // Por ahora la duplicamos para simplicidad
    private val users =
            mutableListOf(
                    User(
                            id = "user1",
                            fullName = "Juan Pérez",
                            email = "juan@example.com",
                            phone = "+57 300 123 4567",
                            password = "password123",
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

    /** Obtiene un usuario por ID */
    suspend fun getUserById(id: String): User? {
        delay(200)
        return users.find { it.id == id }
    }

    /** Obtiene un usuario por email */
    suspend fun getUserByEmail(email: String): User? {
        delay(200)
        return users.find { it.email == email }
    }

    /** Actualiza el perfil del usuario */
    suspend fun updateUser(user: User): Result<User> {
        delay(400)

        val index = users.indexOfFirst { it.id == user.id }
        if (index == -1) {
            return Result.failure(Exception("Usuario no encontrado"))
        }

        users[index] = user
        return Result.success(user)
    }

    /** Agrega un usuario a la lista (usado por AuthRepository) */
    fun addUser(user: User) {
        users.add(user)
    }
}
