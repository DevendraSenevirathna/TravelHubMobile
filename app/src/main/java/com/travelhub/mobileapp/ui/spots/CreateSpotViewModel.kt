package com.travelhub.mobileapp.ui.spots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.repository.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

val spotCategoryOptions = listOf(
    "hotel", "waterfall", "mountain", "hiking", "beach", "restaurant", "camping"
)

sealed class CreateSpotState {
    object Idle : CreateSpotState()
    object Loading : CreateSpotState()
    object Success : CreateSpotState()
    data class Error(val message: String) : CreateSpotState()
}

class CreateSpotViewModel(
    private val spotRepository: SpotRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category

    private val _latitude = MutableStateFlow("")
    val latitude: StateFlow<String> = _latitude

    private val _longitude = MutableStateFlow("")
    val longitude: StateFlow<String> = _longitude

    private val _submitState = MutableStateFlow<CreateSpotState>(CreateSpotState.Idle)
    val submitState: StateFlow<CreateSpotState> = _submitState

    fun onNameChange(value: String) { _name.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }
    fun onCategorySelect(value: String) { _category.value = value }
    fun onLatitudeChange(value: String) { _latitude.value = value }
    fun onLongitudeChange(value: String) { _longitude.value = value }

    fun submit() {
        val lat = _latitude.value.toDoubleOrNull()
        val lng = _longitude.value.toDoubleOrNull()

        if (_name.value.isBlank()) {
            _submitState.value = CreateSpotState.Error("Please enter a spot name")
            return
        }
        if (_description.value.isBlank()) {
            _submitState.value = CreateSpotState.Error("Please enter a description")
            return
        }
        if (_category.value == null) {
            _submitState.value = CreateSpotState.Error("Please select a category")
            return
        }
        if (lat == null || lng == null) {
            _submitState.value = CreateSpotState.Error("Please enter valid latitude and longitude")
            return
        }

        viewModelScope.launch {
            _submitState.value = CreateSpotState.Loading
            spotRepository.createSpot(
                name = _name.value,
                description = _description.value,
                category = _category.value!!,
                latitude = lat,
                longitude = lng
            ).fold(
                onSuccess = { _submitState.value = CreateSpotState.Success },
                onFailure = { e -> _submitState.value = CreateSpotState.Error(e.message ?: "Couldn't submit spot") }
            )
        }
    }
}
