package com.travelhub.mobileapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.model.Spot
import com.travelhub.mobileapp.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    data class Success(val spots: List<Spot>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        // Reload whenever the set of favorite ids changes, so removing a favorite
        // elsewhere (e.g. Home) reflects here without needing manual refresh.
        viewModelScope.launch {
            favoriteRepository.favoriteSpotIds.collect {
                load()
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            favoriteRepository.getFavoriteSpots().fold(
                onSuccess = { spots -> _uiState.value = FavoritesUiState.Success(spots) },
                onFailure = { e -> _uiState.value = FavoritesUiState.Error(e.message ?: "Couldn't load favorites") }
            )
        }
    }

    fun removeFavorite(spot: Spot) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(spot)
        }
    }
}
