package com.travelhub.mobileapp.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.model.Spot
import com.travelhub.mobileapp.data.repository.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(
        val spots: List<Spot>,
        val favoriteSpotIds: Set<Int> = emptySet()
    ) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}

val categoryOptions = listOf(
    "hotel", "waterfall", "mountain", "hiking", "beach", "restaurant", "camping"
)

class ExploreViewModel(
    private val spotRepository: SpotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    init {
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

    fun toggleFavorite(spotId: Int) {
        val current = _uiState.value
        if (current is ExploreUiState.Success) {
            val updated = if (spotId in current.favoriteSpotIds) {
                current.favoriteSpotIds - spotId
            } else {
                current.favoriteSpotIds + spotId
            }
            _uiState.value = current.copy(favoriteSpotIds = updated)
        }
    }
}
