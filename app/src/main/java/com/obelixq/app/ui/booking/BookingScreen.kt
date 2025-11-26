package com.obelixq.app.ui.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obelixq.app.data.model.Business
import com.obelixq.app.data.model.Service
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de agendamiento (BookingScreen)
 *
 * Flujo:
 * 1. Muestra resumen del servicio
 * 2. Usuario selecciona fecha (DatePicker)
 * 3. Usuario selecciona hora (Grid de horarios)
 * 4. Usuario confirma cita
 *
 * Conceptos nuevos:
 * - DatePicker de Material 3
 * - LazyVerticalGrid (grid de horarios)
 * - Formateo de fechas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
        businessId: String,
        serviceId: String,
        userId: String, // Usuario actual (de sesión)
        onNavigateBack: () -> Unit,
        onBookingConfirmed: () -> Unit,
        viewModel: BookingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val business by viewModel.business.collectAsState()
    val selectedService by viewModel.selectedService.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTimeSlot by viewModel.selectedTimeSlot.collectAsState()
    val availableTimeSlots by viewModel.availableTimeSlots.collectAsState()

    // Inicializar al entrar
    LaunchedEffect(Unit) { viewModel.initialize(businessId, serviceId) }

    // Navegar cuando se confirma la cita
    LaunchedEffect(uiState) {
        if (uiState is BookingUiState.AppointmentConfirmed) {
            onBookingConfirmed()
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Agendar Cita") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                            }
                        }
                )
            }
    ) { paddingValues ->
        when (val state = uiState) {
            is BookingUiState.Idle,
            is BookingUiState.SelectingDate,
            is BookingUiState.LoadingTimeSlots,
            is BookingUiState.SelectingTime,
            is BookingUiState.ReadyToConfirm -> {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(paddingValues)
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen del servicio
                    business?.let { b ->
                        selectedService?.let { s -> ServiceSummary(business = b, service = s) }
                    }

                    Divider()

                    // Selección de fecha
                    DateSelection(
                            selectedDate = selectedDate,
                            onDateSelected = { viewModel.selectDate(it) }
                    )

                    // Mostrar horarios si hay fecha seleccionada
                    if (selectedDate != null) {
                        Divider()

                        when (state) {
                            is BookingUiState.LoadingTimeSlots -> {
                                Box(
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                            else -> {
                                TimeSlotSelection(
                                        availableSlots = availableTimeSlots,
                                        selectedSlot = selectedTimeSlot,
                                        onSlotSelected = { viewModel.selectTimeSlot(it) }
                                )
                            }
                        }
                    }

                    // Botón confirmar (solo si hay hora seleccionada)
                    if (selectedTimeSlot != null) {
                        Button(
                                onClick = { viewModel.confirmAppointment(userId) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = state !is BookingUiState.ConfirmingAppointment
                        ) {
                            if (state is BookingUiState.ConfirmingAppointment) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Confirmar Cita")
                            }
                        }
                    }
                }
            }
            is BookingUiState.ConfirmingAppointment -> {
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is BookingUiState.Error -> {
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) { Text("Volver") }
                    }
                }
            }
            else -> {}
        }
    }
}

/** Resumen del servicio seleccionado */
@Composable
fun ServiceSummary(business: Business, service: Service) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = business.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = service.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "${service.durationMinutes} min",
                            style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                        text = "$${service.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Selección de fecha Por simplicidad, usamos botones para días futuros En producción usarías
 * DatePickerDialog
 */
@Composable
fun DateSelection(selectedDate: Long?, onDateSelected: (Long) -> Unit) {
    Column {
        Text(text = "Selecciona una fecha", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        // Generar próximos 7 días
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE dd MMM", Locale("es"))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(7) { index ->
                val date =
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, index)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                val dateMillis = date.timeInMillis

                OutlinedButton(
                        onClick = { onDateSelected(dateMillis) },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                if (selectedDate == dateMillis) {
                                    ButtonDefaults.outlinedButtonColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.primaryContainer
                                    )
                                } else {
                                    ButtonDefaults.outlinedButtonColors()
                                }
                ) { Text(dateFormat.format(date.time)) }
            }
        }
    }
}

/** Grid de horarios disponibles */
@Composable
fun TimeSlotSelection(
        availableSlots: List<Long>,
        selectedSlot: Long?,
        onSlotSelected: (Long) -> Unit
) {
    Column {
        Text(text = "Selecciona una hora", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        if (availableSlots.isEmpty()) {
            Text(
                    text = "No hay horarios disponibles para esta fecha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Grid de horarios (3 columnas)
            LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(300.dp)
            ) {
                items(availableSlots) { slot ->
                    TimeSlotChip(
                            timeSlot = slot,
                            isSelected = selectedSlot == slot,
                            onClick = { onSlotSelected(slot) }
                    )
                }
            }
        }
    }
}

/** Chip de horario individual */
@Composable
fun TimeSlotChip(timeSlot: Long, isSelected: Boolean, onClick: () -> Unit) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeText = timeFormat.format(Date(timeSlot))

    OutlinedButton(
            onClick = onClick,
            colors =
                    if (isSelected) {
                        ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    },
            border =
                    if (isSelected) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        ButtonDefaults.outlinedButtonBorder
                    },
            modifier = Modifier.fillMaxWidth()
    ) { Text(timeText) }
}
