package com.travelhub.mobileapp.ui.destinations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.travelhub.mobileapp.components.ErrorState
import com.travelhub.mobileapp.components.LoadingState
import com.travelhub.mobileapp.components.PostCard
import com.travelhub.mobileapp.data.api.FavoriteApi
import com.travelhub.mobileapp.data.api.PostApi
import com.travelhub.mobileapp.data.repository.MockPostRepository
import com.travelhub.mobileapp.data.repository.MockReviewRepository
import com.travelhub.mobileapp.data.repository.MockSpotRepository
import com.travelhub.mobileapp.data.repository.MockFavoriteRepository
import com.travelhub.mobileapp.data.api.RetrofitClient
import com.travelhub.mobileapp.data.api.SpotApi
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.repository.RealPostRepository
import com.travelhub.mobileapp.data.repository.RealSpotRepository
import com.travelhub.mobileapp.ui.RepositoryProvider

@Composable
fun DestinationDetailsScreen(
    spotId: Int,
    onBackClick: () -> Unit,
    onPostClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val viewModel: DestinationDetailsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val preferences = AppPreferences(context.applicationContext)
                val retrofit = RetrofitClient.getInstance(preferences)
                val spotApi = retrofit.create(SpotApi::class.java)
                val postApi = retrofit.create(PostApi::class.java)
                val favoriteApi = retrofit.create(FavoriteApi::class.java)

                return DestinationDetailsViewModel(
                    spotId = spotId,
                    spotRepository = RealSpotRepository(spotApi),
                    reviewRepository = MockReviewRepository(),
                    postRepository = RealPostRepository(postApi),
                    favoriteRepository = RepositoryProvider.getFavoriteRepository(favoriteApi) // ← swapped
                ) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    val favoriteSpotIds by viewModel.favoriteSpotIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        when (val state = uiState) {
            is DestinationDetailsUiState.Loading -> LoadingState(modifier = Modifier.weight(1f).fillMaxWidth())
            is DestinationDetailsUiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.load() },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            is DestinationDetailsUiState.Success -> DestinationDetailsContent(
                state = state,
                isFavorite = state.spot.id in favoriteSpotIds,
                onFavoriteClick = viewModel::toggleFavorite,
                onPostClick = onPostClick,
                onPostLikeClick = viewModel::toggleLikeOnPost,   // ← new
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DestinationDetailsContent(
    state: DestinationDetailsUiState.Success,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onPostClick: (Int) -> Unit,
    onPostLikeClick: (Int) -> Unit,   // ← add this
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            // Hero image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = state.spot.imageUrl,
                    contentDescription = state.spot.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE53935) else Color.White
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(state.spot.name, style = MaterialTheme.typography.headlineMedium)

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.height(18.dp))
                    Text(
                        String.format("%.1f", state.spot.averageRating),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                    )
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.height(18.dp))
                    Text(
                        state.spot.category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    state.spot.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        item {
            // Map placeholder — real map integration flagged as a future step
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(140.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Map preview coming soon",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Text(
                "Reviews (${state.reviews.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
            )
        }

        if (state.reviews.isEmpty()) {
            item {
                Text(
                    "No reviews yet — be the first to share your experience.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                )
            }
        } else {
            items(state.reviews, key = { it.id }) { review ->
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(review.author, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        repeat(review.rating) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.height(14.dp)
                            )
                        }
                    }
                    Text(review.comment, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Related Posts",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
            )
        }

        if (state.relatedPosts.isEmpty()) {
            item {
                Text(
                    "No posts about this spot yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
                )
            }
        } else {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.relatedPosts, key = { it.id }) { post ->
                        Box(modifier = Modifier.width(260.dp)) {
                            PostCard(
                                authorName = post.author,
                                authorAvatarUrl = post.authorAvatarUrl,
                                imageUrl = post.imageUrl,
                                caption = post.caption,
                                spotName = post.spotName,
                                likesCount = post.likesCount,
                                isLiked = post.isLiked,
                                onLikeClick = { onPostLikeClick(post.id) },
                                onClick = { onPostClick(post.id) }   // ← changed
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}