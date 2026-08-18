package com.travelhub.mobileapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelhub.mobileapp.components.EmptyState
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.components.PostCard
import com.travelhub.mobileapp.components.SpotCard
import com.travelhub.mobileapp.ui.AppViewModelFactory

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
    onPostClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    val favoriteSpotIds by viewModel.favoriteSpotIds.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
        is HomeUiState.Error -> ErrorState(
            message = state.message,
            onRetry = { viewModel.loadHome() },
            modifier = Modifier.fillMaxSize()
        )
        is HomeUiState.Success -> HomeContent(
            state = state,
            favoriteSpotIds = favoriteSpotIds,
            onSpotClick = onSpotClick,
            onPostClick = onPostClick,
            onSearchClick = onSearchClick,
            onFavoriteClick = { spot -> viewModel.toggleFavorite(spot) },
            onLikeClick = viewModel::toggleLike
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    favoriteSpotIds: Set<Int>,
    onSpotClick: (Int) -> Unit,
    onPostClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: (com.travelhub.mobileapp.data.model.Spot) -> Unit,
    onLikeClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 4.dp, bottom = 12.dp)
            ) {
                // 1. Centered App Title pulled to the very top with Notification Bell
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TravelHub",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { /* Handle Notifications */ },
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = Color(0xFF212121),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 2. Greeting Header
                Text(
                    text = "Hello, Adventurer!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F)
                )

                Text(
                    text = "Ready to explore the conscious way?",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // 3. Rounded Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickableNoRipple { onSearchClick() },
                    shape = RoundedCornerShape(25.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Where to next?",
                            fontSize = 14.5.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
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
                            isFavorite = spot.id in favoriteSpotIds,
                            onFavoriteClick = { onFavoriteClick(spot) },
                            onClick = { onSpotClick(spot.id) },
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
                            isFavorite = spot.id in favoriteSpotIds,
                            onFavoriteClick = { onFavoriteClick(spot) },
                            onClick = { onSpotClick(spot.id) },
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
                        onClick = { onPostClick(post.id) }
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
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
    )
}