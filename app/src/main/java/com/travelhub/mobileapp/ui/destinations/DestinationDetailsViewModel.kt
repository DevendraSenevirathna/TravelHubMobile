package com.travelhub.mobileapp.ui.destinations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.model.Post
import com.travelhub.mobileapp.data.model.Review
import com.travelhub.mobileapp.data.model.Spot
import com.travelhub.mobileapp.data.repository.PostRepository
import com.travelhub.mobileapp.data.repository.ReviewRepository
import com.travelhub.mobileapp.data.repository.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DestinationDetailsUiState {
    object Loading : DestinationDetailsUiState()
    data class Success(
        val spot: Spot,
        val reviews: List<Review>,
        val relatedPosts: List<Post>,
        val isFavorite: Boolean = false
    ) : DestinationDetailsUiState()
    data class Error(val message: String) : DestinationDetailsUiState()
}

class DestinationDetailsViewModel(
    private val spotId: Int,
    private val spotRepository: SpotRepository,
    private val reviewRepository: ReviewRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DestinationDetailsUiState>(DestinationDetailsUiState.Loading)
    val uiState: StateFlow<DestinationDetailsUiState> = _uiState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DestinationDetailsUiState.Loading
            val spotResult = spotRepository.getSpotById(spotId)
            val reviewsResult = reviewRepository.getReviewsForSpot(spotId)
            val postsResult = postRepository.getPostsForSpot(spotId)

            val spot = spotResult.getOrNull()
            if (spot == null) {
                _uiState.value = DestinationDetailsUiState.Error("Destination not found")
                return@launch
            }

            _uiState.value = DestinationDetailsUiState.Success(
                spot = spot,
                reviews = reviewsResult.getOrDefault(emptyList()),
                relatedPosts = postsResult.getOrDefault(emptyList())
            )
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value
        if (current is DestinationDetailsUiState.Success) {
            _uiState.value = current.copy(isFavorite = !current.isFavorite)
            // TODO: persist via FavoriteRepository once built (Favorites step)
        }
    }

    fun submitReview(rating: Int, comment: String) {
        val current = _uiState.value
        if (current is DestinationDetailsUiState.Success) {
            viewModelScope.launch {
                val result = reviewRepository.addReview(spotId, rating, comment)
                result.onSuccess { newReview ->
                    _uiState.value = current.copy(reviews = current.reviews + newReview)
                }
            }
        }
    }
}
