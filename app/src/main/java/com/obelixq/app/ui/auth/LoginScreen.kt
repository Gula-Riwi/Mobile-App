package com.obelixq.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla de Login
 *
 * Equivalente en web: Una página HTML con formulario
 *
 * Conceptos de Compose:
 * - @Composable = función que genera UI
 * - remember = mantiene estado entre recomposiciones
 * - LaunchedEffect = ejecuta código cuando cambia un valor
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
        onNavigateToRegister: () -> Unit, // Navegar a registro
        onLoginSuccess: (com.obelixq.app.data.model.User) -> Unit, // Pasar el usuario
        viewModel: AuthViewModel = viewModel() // ViewModel inyectado automáticamente
) {
        // Estados locales para los campos del formulario
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        // Observar el estado del ViewModel
        val uiState by viewModel.uiState.collectAsState()

        // Cuando el login es exitoso, navegar a home
        LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                        val user = (uiState as AuthUiState.Success).user
                        onLoginSuccess(user)
                }
        }

        // Scaffold = estructura básica de una pantalla Material 3
        Scaffold(topBar = { TopAppBar(title = { Text("Iniciar Sesión") }) }) { paddingValues ->
                // Column = layout vertical (como flexbox column)
                Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        // Logo/Título
                        Text(
                                text = "ObelixQ",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "Tu asistente de agendamiento inteligente",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Campo de Email
                        OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null)
                                },
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de Password
                        OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Contraseña") },
                                leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                        IconButton(
                                                onClick = { passwordVisible = !passwordVisible }
                                        ) {
                                                Icon(
                                                        imageVector =
                                                                if (passwordVisible)
                                                                        Icons.Default.Visibility
                                                                else Icons.Default.VisibilityOff,
                                                        contentDescription =
                                                                if (passwordVisible)
                                                                        "Ocultar contraseña"
                                                                else "Mostrar contraseña"
                                                )
                                        }
                                },
                                visualTransformation =
                                        if (passwordVisible) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de Login
                        Button(
                                onClick = { viewModel.login(email, password) },
                                enabled = uiState !is AuthUiState.Loading,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                if (uiState is AuthUiState.Loading) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = MaterialTheme.colorScheme.onPrimary
                                        )
                                } else {
                                        Text("Iniciar Sesión")
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para ir a registro
                        TextButton(onClick = onNavigateToRegister) {
                                Text("¿No tienes cuenta? Regístrate")
                        }

                        // Mostrar error si hay
                        if (uiState is AuthUiState.Error) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        text = (uiState as AuthUiState.Error).message,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                )
                        }
                }
        }
}
