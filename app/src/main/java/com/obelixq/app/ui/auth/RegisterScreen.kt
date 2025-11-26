package com.obelixq.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
 * Pantalla de Registro
 *
 * Formulario completo para que el usuario se registre. Los datos son críticos para que los bots
 * puedan contactarlo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
        onNavigateToLogin: () -> Unit,
        onRegisterSuccess: (com.obelixq.app.data.model.User) -> Unit,
        viewModel: AuthViewModel = viewModel()
) {
        // Estados locales para los campos
        var fullName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }

        // Observar estado del ViewModel
        val uiState by viewModel.uiState.collectAsState()

        // Navegar cuando el registro es exitoso
        LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                        val user = (uiState as AuthUiState.Success).user
                        onRegisterSuccess(user)
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Crear Cuenta") },
                                navigationIcon = {
                                        IconButton(onClick = onNavigateToLogin) {
                                                Icon(
                                                        Icons.Default.ArrowBack,
                                                        contentDescription = "Volver"
                                                )
                                        }
                                }
                        )
                }
        ) { paddingValues ->
                // Column con scroll para formularios largos
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(paddingValues)
                                        .verticalScroll(rememberScrollState()) // Permite scroll
                                        .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(
                                text = "Únete a ObelixQ",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "Completa tus datos para empezar a agendar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Campo: Nombre Completo
                        OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Nombre Completo") },
                                leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo: Email
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

                        // Campo: Teléfono (CRÍTICO para WhatsApp)
                        OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Teléfono (con código de país)") },
                                leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null)
                                },
                                placeholder = { Text("+57 300 123 4567") },
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                supportingText = {
                                        Text(
                                                text = "Necesario para confirmaciones por WhatsApp",
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo: Contraseña
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
                                                                if (passwordVisible) "Ocultar"
                                                                else "Mostrar"
                                                )
                                        }
                                },
                                visualTransformation =
                                        if (passwordVisible) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                supportingText = {
                                        Text(
                                                text = "Mínimo 6 caracteres",
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo: Confirmar Contraseña
                        OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirmar Contraseña") },
                                leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                        IconButton(
                                                onClick = {
                                                        confirmPasswordVisible =
                                                                !confirmPasswordVisible
                                                }
                                        ) {
                                                Icon(
                                                        imageVector =
                                                                if (confirmPasswordVisible)
                                                                        Icons.Default.Visibility
                                                                else Icons.Default.VisibilityOff,
                                                        contentDescription =
                                                                if (confirmPasswordVisible)
                                                                        "Ocultar"
                                                                else "Mostrar"
                                                )
                                        }
                                },
                                visualTransformation =
                                        if (confirmPasswordVisible) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                isError =
                                        confirmPassword.isNotEmpty() && password != confirmPassword,
                                supportingText = {
                                        if (confirmPassword.isNotEmpty() &&
                                                        password != confirmPassword
                                        ) {
                                                Text(
                                                        text = "Las contraseñas no coinciden",
                                                        color = MaterialTheme.colorScheme.error
                                                )
                                        }
                                }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botón de Registro
                        Button(
                                onClick = {
                                        if (password == confirmPassword) {
                                                viewModel.register(fullName, email, phone, password)
                                        }
                                },
                                enabled =
                                        uiState !is AuthUiState.Loading &&
                                                fullName.isNotEmpty() &&
                                                email.isNotEmpty() &&
                                                phone.isNotEmpty() &&
                                                password.isNotEmpty() &&
                                                password == confirmPassword,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                if (uiState is AuthUiState.Loading) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = MaterialTheme.colorScheme.onPrimary
                                        )
                                } else {
                                        Text("Crear Cuenta")
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para volver a login
                        TextButton(onClick = onNavigateToLogin) {
                                Text("¿Ya tienes cuenta? Inicia sesión")
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
