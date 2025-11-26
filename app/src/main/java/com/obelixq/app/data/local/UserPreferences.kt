package com.obelixq.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.obelixq.app.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * UserPreferences - Manejo de sesión persistente
 *
 * Usa DataStore para guardar el usuario logueado. Cuando la app se cierra y vuelve a abrir, el
 * usuario sigue logueado.
 *
 * Conceptos:
 * - DataStore = almacenamiento key-value persistente (como SharedPreferences mejorado)
 * - Flow = stream reactivo de datos (como LiveData pero más moderno)
 * - suspend = función asíncrona (se ejecuta en background)
 */

// Extension property para acceder al DataStore
private val Context.dataStore: DataStore<Preferences> by
        preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    // Keys para guardar los datos del usuario
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")
    }

    /** Guardar usuario en DataStore Se llama después de login/register exitoso */
    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_NAME] = user.fullName
            preferences[USER_EMAIL] = user.email
            preferences[USER_PHONE] = user.phone
        }
    }

    /** Leer usuario desde DataStore Retorna un Flow que emite el usuario cuando cambia */
    val userFlow: Flow<User?> =
            context.dataStore.data.map { preferences ->
                val userId = preferences[USER_ID]
                val userName = preferences[USER_NAME]
                val userEmail = preferences[USER_EMAIL]
                val userPhone = preferences[USER_PHONE]

                // Si todos los datos existen, crear el usuario
                if (userId != null && userName != null && userEmail != null && userPhone != null) {
                    User(
                            id = userId,
                            fullName = userName,
                            email = userEmail,
                            phone = userPhone,
                            password = "" // No guardamos la contraseña por seguridad
                    )
                } else {
                    null // No hay usuario guardado
                }
            }

    /** Borrar usuario (logout) */
    suspend fun clearUser() {
        context.dataStore.edit { preferences -> preferences.clear() }
    }
}
