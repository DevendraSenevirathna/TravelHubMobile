package com.travelhub.mobileapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.EmptyState
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.components.PostCard
import com.travelhub.mobileapp.components.SpotCard
import com.travelhub.mobileapp.ui.AppViewModelFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.composed
import androidx.compose.runtime.remember
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
@Composable
fun HomeScreen(
    onSpotClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
        is HomeUiState.Error -> ErrorState(
            message = state.message,
            onRetry = { viewModel.loadHome() },
            modifier = Modifier.fillMaxSize()
        )
        is HomeUiState.Success -> HomeContent(
            state = state,
            onSpotClick = onSpotClick,
            onSearchClick = onSearchClick,
            onFavoriteClick = viewModel::toggleFavorite,
            onLikeClick = viewModel::toggleLike
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onSpotClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Good morning, Explorer 🌿", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = { Text("Search destinations...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple { onSearchClick() },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            }
        }

        item {
            SectionHeader("Nearby Destinations")
            if (state.nearbySpots.isEmpty()) {
                EmptyState(message = "No nearby spots yet", modifier = Modifier.height(120.dp))
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.nearbySpots, key = { it.id }) { spot ->
                        SpotCard(
                            name = spot.name,
                            category = spot.category,
                            imageUrl = spot.imageUrl,
                            rating = spot.averageRating,
                            isFavorite = spot.id in state.favoriteSpotIds,
                            onClick = { onSpotClick(spot.id) },
                            onFavoriteClick = { onFavoriteClick(spot.id) },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }
        }

        item {
            SectionHeader("Trending This Week")
            if (state.trendingSpots.isEmpty()) {
                EmptyState(message = "No trending spots yet", modifier = Modifier.height(120.dp))
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.trendingSpots, key = { it.id }) { spot ->
                        SpotCard(
                            name = spot.name,
                            category = spot.category,
                            imageUrl = spot.imageUrl,
                            rating = spot.averageRating,
                            isFavorite = spot.id in state.favoriteSpotIds,
                            onClick = { onSpotClick(spot.id) },
                            onFavoriteClick = { onFavoriteClick(spot.id) },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }
        }

        item {
            SectionHeader("Travel Stories")
        }

        if (state.feed.isEmpty()) {
            item {
                EmptyState(
                    message = "No stories yet",
                    subtitle = "Be the first to share a travel story",
                    modifier = Modifier.height(160.dp)
                )
            }
        } else {
            items(state.feed, key = { it.id }) { post ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    PostCard(
                        authorName = post.author,
                        authorAvatarUrl = post.authorAvatarUrl,
                        imageUrl = post.imageUrl,
                        caption = post.caption,
                        spotName = post.spotName,
                        likesCount = post.likesCount,
                        isLiked = post.isLiked,
                        onLikeClick = { onLikeClick(post.id) },
                        onClick = { /* navigate to post details later */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
    )
}
