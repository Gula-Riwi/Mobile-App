package com.obelixq.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import com.obelixq.app.data.model.BusinessCategory

/**
 * Pantalla principal (HomeScreen)
 *
 * Muestra:
 * - Barra de búsqueda
 * - Filtros por categoría (chips horizontales)
 * - Lista de negocios
 *
 * Conceptos nuevos:
 * - LazyColumn = RecyclerView de Compose (lista eficiente)
 * - LazyRow = Lista horizontal
 * - AsyncImage (Coil) = Cargar imágenes desde URL
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        onBusinessClick: (String) -> Unit, // Navegar al detalle con businessId
        viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("ObelixQ") }) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Barra de búsqueda
            SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.searchBusinesses(it) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Filtros de categoría (chips horizontales)
            CategoryFilters(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.filterByCategory(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de negocios según el estado
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Success -> {
                    if (state.businesses.isEmpty()) {
                        // Sin resultados
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) {
                            Text(
                                    text = "No se encontraron negocios",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Lista de negocios
                        BusinessList(
                                businesses = state.businesses,
                                onBusinessClick = onBusinessClick
                        )
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/** Barra de búsqueda */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar negocios...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = modifier
    )
}

/** Filtros de categoría (chips horizontales) */
@Composable
fun CategoryFilters(
        selectedCategory: BusinessCategory?,
        onCategorySelected: (BusinessCategory?) -> Unit,
        modifier: Modifier = Modifier
) {
    // LazyRow = lista horizontal (como RecyclerView horizontal)
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Chip "Todas"
        item {
            FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("Todas") }
            )
        }

        // Chips de categorías
        items(BusinessCategory.values()) { category ->
            FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.displayName) }
            )
        }
    }
}

/** Lista de negocios LazyColumn = RecyclerView de Compose */
@Composable
fun BusinessList(businesses: List<Business>, onBusinessClick: (String) -> Unit) {
    // LazyColumn solo renderiza los items visibles (eficiente)
    LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(businesses) { business ->
            BusinessCard(business = business, onClick = { onBusinessClick(business.id) })
        }
    }
}

/** Card de un negocio Componente reutilizable */
@Composable
fun BusinessCard(business: Business, onClick: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Imagen del negocio (Coil carga desde URL)
            AsyncImage(
                    model = business.imageUrl,
                    contentDescription = business.name,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información del negocio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text = business.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text = business.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "${business.rating} (${business.totalReviews} reseñas)",
                            style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
