package com.travelhub.mobileapp.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.CategoryChip
import com.travelhub.mobileapp.components.EmptyState
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.components.SpotCard
import com.travelhub.mobileapp.ui.AppViewModelFactory

@Composable
fun ExploreScreen(
    onSpotClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val viewModel: ExploreViewModel = viewModel(factory = AppViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val favoriteSpotIds by viewModel.favoriteSpotIds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Explore",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            placeholder = { Text("Search destinations...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoryOptions) { category ->
                CategoryChip(
                    label = category.replaceFirstChar { it.uppercase() },
                    selected = category == selectedCategory,
                    onClick = { viewModel.onCategorySelect(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (val state = uiState) {
            is ExploreUiState.Loading -> LoadingState(modifier = Modifier.weight(1f).fillMaxWidth())
            is ExploreUiState.Error -> ErrorState(
                message = state.message,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            is ExploreUiState.Success -> {
                if (state.spots.isEmpty()) {
                    EmptyState(
                        message = "No destinations found",
                        subtitle = "Try a different search or category",
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        items(state.spots, key = { it.id }) { spot ->
                            SpotCard(
                                name = spot.name,
                                category = spot.category,
                                imageUrl = spot.imageUrl,
                                rating = spot.averageRating,
                                isFavorite = spot.id in favoriteSpotIds,
                                onFavoriteClick = { viewModel.toggleFavorite(spot) },
                                onClick = { onSpotClick(spot.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
