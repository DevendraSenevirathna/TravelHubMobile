package com.travelhub.mobileapp.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.model.Spot
import com.travelhub.mobileapp.data.repository.FavoriteRepository
import com.travelhub.mobileapp.data.repository.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(val spots: List<Spot>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}

val categoryOptions = listOf(
    "hotel", "waterfall", "mountain", "hiking", "beach", "restaurant", "camping"
)

class ExploreViewModel(
    private val spotRepository: SpotRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val favoriteSpotIds: StateFlow<Set<Int>> = favoriteRepository.favoriteSpotIds

    init {
        search()
    }

    fun refresh() {
        search()
    }
    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        search()
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
        search()
    }

    private fun search() {
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            val result = spotRepository.searchSpots(_query.value, _selectedCategory.value)
            result.fold(
                onSuccess = { spots -> _uiState.value = ExploreUiState.Success(spots) },
                onFailure = { e -> _uiState.value = ExploreUiState.Error(e.message ?: "Search failed") }
            )
        }
    }

    fun toggleFavorite(spot: Spot) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(spot)
        }
    }
}