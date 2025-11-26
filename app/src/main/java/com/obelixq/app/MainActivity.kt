package com.obelixq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.obelixq.app.data.model.User
import com.obelixq.app.ui.navigation.ObelixNavGraph
import com.obelixq.app.ui.theme.ObelixQTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * MainActivity - Punto de entrada de la app
 *
 * Maneja:
 * - Sesión del usuario (por ahora en memoria, luego en DataStore)
 * - Navegación principal
 * - Tema de la app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ObelixQTheme { ObelixApp() } }
    }
}

@Composable
fun ObelixApp() {
    // UserPreferences para persistencia
    val context = LocalContext.current
    val userPreferences = remember { com.obelixq.app.data.local.UserPreferences(context) }

    // Scope para coroutines
    val scope = rememberCoroutineScope()

    // Cargar usuario guardado al iniciar
    val savedUser by userPreferences.userFlow.collectAsState(initial = null)

    // Estado del usuario actual
    var currentUser by remember { mutableStateOf<User?>(null) }

    // Actualizar currentUser cuando savedUser cambie
    LaunchedEffect(savedUser) {
        if (currentUser == null && savedUser != null) {
            currentUser = savedUser
        }
    }

    // Navegación principal
    ObelixNavGraph(
            currentUser = currentUser,
            onLoginSuccess = { user ->
                currentUser = user
                // Guardar en DataStore
                scope.launch(Dispatchers.IO) { userPreferences.saveUser(user) }
            },
            onLogout = {
                currentUser = null
                // Borrar de DataStore
                scope.launch(Dispatchers.IO) { userPreferences.clearUser() }
            }
    )
}
