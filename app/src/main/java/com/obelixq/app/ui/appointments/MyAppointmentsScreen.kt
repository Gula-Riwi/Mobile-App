package com.obelixq.app.ui.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obelixq.app.data.model.Appointment
import com.obelixq.app.data.model.AppointmentStatus
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla "Mis Citas" (MyAppointmentsScreen)
 *
 * Muestra:
 * - Historial de citas del usuario
 * - Filtros por estado (Próximas, Completadas, Canceladas)
 * - Opción de cancelar citas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(
        userId: String,
        onAppointmentClick: (String) -> Unit,
        viewModel: MyAppointmentsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    // Cargar citas al iniciar
    LaunchedEffect(userId) { viewModel.loadAppointments(userId) }

    Scaffold(topBar = { TopAppBar(title = { Text("Mis Citas") }) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Filtros (Tabs)
            FilterTabs(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { viewModel.filterByStatus(it) }
            )

            // Lista de citas según el estado
            when (val state = uiState) {
                is AppointmentsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AppointmentsUiState.Success -> {
                    AppointmentsList(
                            appointments = state.appointments,
                            onAppointmentClick = onAppointmentClick,
                            onCancelClick = { appointmentId ->
                                viewModel.cancelAppointment(appointmentId, userId)
                            }
                    )
                }
                is AppointmentsUiState.Empty -> {
                    EmptyState(filter = state.filter)
                }
                is AppointmentsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

/** Tabs de filtros */
@Composable
fun FilterTabs(selectedFilter: AppointmentFilter, onFilterSelected: (AppointmentFilter) -> Unit) {
    ScrollableTabRow(
            selectedTabIndex = selectedFilter.ordinal,
            modifier = Modifier.fillMaxWidth()
    ) {
        AppointmentFilter.values().forEach { filter ->
            Tab(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelected(filter) },
                    text = { Text(filter.displayName) }
            )
        }
    }
}

/** Lista de citas */
@Composable
fun AppointmentsList(
        appointments: List<Appointment>,
        onAppointmentClick: (String) -> Unit,
        onCancelClick: (String) -> Unit
) {
    LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                    appointment = appointment,
                    onClick = { onAppointmentClick(appointment.id) },
                    onCancelClick = { onCancelClick(appointment.id) }
            )
        }
    }
}

/** Card de una cita */
@Composable
fun AppointmentCard(appointment: Appointment, onClick: () -> Unit, onCancelClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEE dd MMM, yyyy", Locale("es"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(appointment.appointmentDate)

    var showCancelDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Estado
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = appointment.status)

                // Botón cancelar (solo si está pendiente o confirmada)
                if (appointment.status == AppointmentStatus.PENDING ||
                                appointment.status == AppointmentStatus.CONFIRMED
                ) {
                    TextButton(onClick = { showCancelDialog = true }) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha y hora
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = dateFormat.format(date), style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = timeFormat.format(date), style = MaterialTheme.typography.bodyLarge)
            }

            // Notas (si hay)
            appointment.notes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "Notas: $notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Diálogo de confirmación de cancelación
    if (showCancelDialog) {
        AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Cancelar Cita") },
                text = { Text("¿Estás seguro de que quieres cancelar esta cita?") },
                confirmButton = {
                    TextButton(
                            onClick = {
                                onCancelClick()
                                showCancelDialog = false
                            }
                    ) { Text("Sí, cancelar", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) { Text("No") }
                }
        )
    }
}

/** Chip de estado de la cita */
@Composable
fun StatusChip(status: AppointmentStatus) {
    val (color, icon) =
            when (status) {
                AppointmentStatus.PENDING ->
                        Pair(MaterialTheme.colorScheme.tertiary, Icons.Default.Schedule)
                AppointmentStatus.CONFIRMED ->
                        Pair(MaterialTheme.colorScheme.primary, Icons.Default.CheckCircle)
                AppointmentStatus.COMPLETED ->
                        Pair(MaterialTheme.colorScheme.secondary, Icons.Default.Done)
                AppointmentStatus.CANCELLED ->
                        Pair(MaterialTheme.colorScheme.error, Icons.Default.Cancel)
            }

    AssistChip(
            onClick = {},
            label = { Text(status.displayName) },
            leadingIcon = {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            colors =
                    AssistChipDefaults.assistChipColors(
                            leadingIconContentColor = color,
                            labelColor = color
                    )
    )
}

/** Estado vacío */
@Composable
fun EmptyState(filter: AppointmentFilter) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                    text =
                            when (filter) {
                                AppointmentFilter.UPCOMING -> "No tienes citas próximas"
                                AppointmentFilter.COMPLETED -> "No tienes citas completadas"
                                AppointmentFilter.CANCELLED -> "No tienes citas canceladas"
                                AppointmentFilter.ALL -> "No tienes citas"
                            },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
