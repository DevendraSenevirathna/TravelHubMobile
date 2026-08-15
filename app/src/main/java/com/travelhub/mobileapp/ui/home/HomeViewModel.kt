package com.travelhub.mobileapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.model.Post
import com.travelhub.mobileapp.data.model.Spot
import com.travelhub.mobileapp.data.repository.PostRepository
import com.travelhub.mobileapp.data.repository.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val nearbySpots: List<Spot>,
        val trendingSpots: List<Spot>,
        val feed: List<Post>,
        val favoriteSpotIds: Set<Int> = emptySet()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val spotRepository: SpotRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val nearbyResult = spotRepository.getNearbySpots()
            val trendingResult = spotRepository.getTrendingSpots()
            val feedResult = postRepository.getFeed()

            if (nearbyResult.isSuccess && trendingResult.isSuccess && feedResult.isSuccess) {
                _uiState.value = HomeUiState.Success(
                    nearbySpots = nearbyResult.getOrDefault(emptyList()),
                    trendingSpots = trendingResult.getOrDefault(emptyList()),
                    feed = feedResult.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = HomeUiState.Error("Couldn't load your feed. Pull to refresh.")
            }
        }
    }

    fun toggleFavorite(spotId: Int) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            val updated = if (spotId in current.favoriteSpotIds) {
                current.favoriteSpotIds - spotId
            } else {
                current.favoriteSpotIds + spotId
            }
            _uiState.value = current.copy(favoriteSpotIds = updated)
            // TODO: persist via FavoriteRepository once built (Favorites step)
        }
    }

    fun toggleLike(postId: Int) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            viewModelScope.launch {
                postRepository.toggleLike(postId)
                val updatedFeed = current.feed.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            isLiked = !post.isLiked,
                            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                        )
                    } else post
                }
                _uiState.update {
                    (it as? HomeUiState.Success)?.copy(feed = updatedFeed) ?: it
                }
            }
        }
    }
}
