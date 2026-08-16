package com.travelhub.mobileapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EditProfileState {
    object Idle : EditProfileState()
    object Loading : EditProfileState()
    object Success : EditProfileState()
    data class Error(val message: String) : EditProfileState()
}

class EditProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _bio = MutableStateFlow(profileRepository.currentProfile.value?.bio ?: "")
    val bio: StateFlow<String> = _bio

    private val _interests = MutableStateFlow(
        profileRepository.currentProfile.value?.interests?.toSet() ?: emptySet()
    )
    val interests: StateFlow<Set<String>> = _interests

    private val _saveState = MutableStateFlow<EditProfileState>(EditProfileState.Idle)
    val saveState: StateFlow<EditProfileState> = _saveState

    fun onBioChange(value: String) {
        _bio.value = value
    }

    fun toggleInterest(label: String) {
        _interests.value = if (label in _interests.value) {
            _interests.value - label
        } else {
            _interests.value + label
        }
    }

    fun save() {
        viewModelScope.launch {
            _saveState.value = EditProfileState.Loading
            profileRepository.updateProfile(_bio.value, _interests.value.toList()).fold(
                onSuccess = { _saveState.value = EditProfileState.Success },
                onFailure = { e -> _saveState.value = EditProfileState.Error(e.message ?: "Save failed") }
            )
        }
    }
}
