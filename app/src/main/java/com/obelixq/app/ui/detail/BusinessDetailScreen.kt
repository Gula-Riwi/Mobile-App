package com.obelixq.app.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.obelixq.app.data.model.Business
import com.obelixq.app.data.model.Service

/**
 * Pantalla de detalle del negocio
 *
 * Muestra:
 * - Información completa del negocio
 * - Lista de servicios disponibles
 * - Botón para agendar cada servicio
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailScreen(
        businessId: String,
        onNavigateBack: () -> Unit,
        onBookService: (businessId: String, serviceId: String) -> Unit,
        viewModel: BusinessDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val business by viewModel.business.collectAsState()
    val services by viewModel.services.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(businessId) { viewModel.loadBusinessDetail(businessId) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text(business?.name ?: "Detalle") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                            }
                        }
                )
            }
    ) { paddingValues ->
        when (uiState) {
            is BusinessDetailUiState.Loading -> {
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is BusinessDetailUiState.Success -> {
                business?.let { businessData ->
                    LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header con imagen
                        item { BusinessHeader(business = businessData) }

                        // Información del negocio
                        item { BusinessInfo(business = businessData) }

                        // Título de servicios
                        item {
                            Text(
                                    text = "Servicios Disponibles",
                                    style = MaterialTheme.typography.titleLarge
                            )
                        }

                        // Lista de servicios
                        items(services) { service ->
                            ServiceCard(
                                    service = service,
                                    onBookClick = { onBookService(businessId, service.id) }
                            )
                        }
                    }
                }
            }
            is BusinessDetailUiState.Error -> {
                Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                ) {
                    Text(
                            text = (uiState as BusinessDetailUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/** Header con imagen y rating del negocio */
@Composable
fun BusinessHeader(business: Business) {
    Column {
        // Imagen grande
        AsyncImage(
                model = business.imageUrl,
                contentDescription = business.name,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categoría
        AssistChip(
                onClick = {},
                label = { Text(business.category.displayName) },
                leadingIcon = {
                    Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                    )
                }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${business.rating}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                    text = "(${business.totalReviews} reseñas)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Información del negocio (descripción, horarios, contacto) */
@Composable
fun BusinessInfo(business: Business) {
    Card {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Descripción
            Text(text = business.description, style = MaterialTheme.typography.bodyMedium)

            Divider()

            // Horarios
            InfoRow(
                    icon = Icons.Default.Schedule,
                    label = "Horario",
                    value = "${business.openingTime} - ${business.closingTime}"
            )

            // Ubicación
            InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Ubicación",
                    value = "${business.address}, ${business.city}"
            )

            // Teléfono
            InfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = business.phone)

            // Email
            InfoRow(icon = Icons.Default.Email, label = "Email", value = business.email)
        }
    }
}

/** Fila de información con ícono */
@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Card de un servicio con botón para agendar */
@Composable
fun ServiceCard(service: Service, onBookClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = service.name, style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                            text = service.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Precio
                Text(
                        text = "$${service.price.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                // Duración
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "${service.durationMinutes} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botón agendar
                Button(onClick = onBookClick) {
                    Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agendar")
                }
            }
        }
    }
}
