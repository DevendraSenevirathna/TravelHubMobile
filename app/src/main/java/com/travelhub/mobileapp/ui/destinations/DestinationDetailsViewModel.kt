package com.travelhub.mobileapp.ui.destinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.model.Post
import com.travelhub.mobileapp.data.model.Review
import com.travelhub.mobileapp.data.model.Spot
import com.travelhub.mobileapp.data.repository.FavoriteRepository
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
        val relatedPosts: List<Post>
    ) : DestinationDetailsUiState()
    data class Error(val message: String) : DestinationDetailsUiState()
}

class DestinationDetailsViewModel(
    private val spotId: Int,
    private val spotRepository: SpotRepository,
    private val reviewRepository: ReviewRepository,
    private val postRepository: PostRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DestinationDetailsUiState>(DestinationDetailsUiState.Loading)
    val uiState: StateFlow<DestinationDetailsUiState> = _uiState
    private val _reviewError = MutableStateFlow<String?>(null)
    val reviewError: StateFlow<String?> = _reviewError
    val favoriteSpotIds: StateFlow<Set<Int>> = favoriteRepository.favoriteSpotIds

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DestinationDetailsUiState.Loading
            val spot = spotRepository.getSpotById(spotId).getOrNull()
            if (spot == null) {
                _uiState.value = DestinationDetailsUiState.Error("Destination not found")
                return@launch
            }
            val reviews = reviewRepository.getReviewsForSpot(spotId).getOrDefault(emptyList())
            val posts = postRepository.getPostsForSpot(spotId).getOrDefault(emptyList())

            _uiState.value = DestinationDetailsUiState.Success(spot, reviews, posts)
        }
    }

    fun toggleLikeOnPost(postId: Int) {
        val current = _uiState.value
        if (current is DestinationDetailsUiState.Success) {
            viewModelScope.launch {
                postRepository.toggleLike(postId)
                val updatedPosts = current.relatedPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            isLiked = !post.isLiked,
                            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                        )
                    } else post
                }
                _uiState.value = current.copy(relatedPosts = updatedPosts)
            }
        }
    }
    fun toggleFavorite() {
        val current = _uiState.value
        if (current is DestinationDetailsUiState.Success) {
            viewModelScope.launch {
                favoriteRepository.toggleFavorite(current.spot)
            }
        }
    }

    fun submitReview(rating: Int, comment: String) {
        val current = _uiState.value
        if (current is DestinationDetailsUiState.Success) {
            viewModelScope.launch {
                _reviewError.value = null
                reviewRepository.addReview(spotId, rating, comment).fold(
                    onSuccess = { newReview ->
                        _uiState.value = current.copy(reviews = current.reviews + newReview)
                    },
                    onFailure = { e ->
                        _reviewError.value = e.message ?: "Couldn't submit review"
                    }
                )
            }
        }
    }

    fun clearReviewError() {
        _reviewError.value = null
    }
}