package com.travelhub.mobileapp.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.EmptyState
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.components.SpotCard
import com.travelhub.mobileapp.ui.AppViewModelFactory

@Composable
fun FavoritesScreen(
    onSpotClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val viewModel: FavoritesViewModel = viewModel(factory = AppViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Favorites",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
        )

        when (val state = uiState) {
            is FavoritesUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
            is FavoritesUiState.Error -> ErrorState(message = state.message, modifier = Modifier.fillMaxSize())
            is FavoritesUiState.Success -> {
                if (state.spots.isEmpty()) {
                    EmptyState(
                        message = "No favorites yet",
                        subtitle = "Your personal collection of favorite travel destinations",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.spots, key = { it.id }) { spot ->
                            SpotCard(
                                name = spot.name,
                                category = spot.category,
                                imageUrl = spot.imageUrl,
                                rating = spot.averageRating,
                                isFavorite = true, // always true on this screen
                                onClick = { onSpotClick(spot.id) },
                                onFavoriteClick = { viewModel.removeFavorite(spot) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
