package com.obelixq.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obelixq.app.data.model.User

/**
 * Pantalla de perfil del usuario
 *
 * Muestra:
 * - Información del usuario
 * - Opciones de configuración
 * - Botón de cerrar sesión
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: User, onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Perfil") }) }) { paddingValues ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con avatar
            ProfileHeader(user = user)

            Divider()

            // Información personal
            Text(text = "Información Personal", style = MaterialTheme.typography.titleMedium)

            ProfileInfoCard(user = user)

            Divider()

            // Configuración
            Text(text = "Configuración", style = MaterialTheme.typography.titleMedium)

            SettingsSection()

            Spacer(modifier = Modifier.weight(1f))

            // Botón cerrar sesión
            OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                            )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }

    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
                confirmButton = {
                    TextButton(
                            onClick = {
                                onLogout()
                                showLogoutDialog = false
                            }
                    ) { Text("Sí, cerrar sesión") }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
                }
        )
    }
}

/** Header con avatar y nombre */
@Composable
fun ProfileHeader(user: User) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Avatar (iniciales)
        Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                        text =
                                user.fullName.split(" ").take(2).joinToString("") {
                                    it.first().uppercase()
                                },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = user.fullName, style = MaterialTheme.typography.headlineSmall)
    }
}

/** Card con información del usuario */
@Composable
fun ProfileInfoCard(user: User) {
    Card {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = user.email)

            ProfileInfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = user.phone)
        }
    }
}

/** Fila de información */
@Composable
fun ProfileInfoRow(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Sección de configuración */
@Composable
fun SettingsSection() {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones",
                    subtitle = "Gestionar notificaciones de citas"
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            SettingItem(icon = Icons.Default.Language, title = "Idioma", subtitle = "Español")

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            SettingItem(icon = Icons.Default.Info, title = "Acerca de", subtitle = "ObelixQ v1.0")
        }
    }
}

/** Item de configuración */
@Composable
fun SettingItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
