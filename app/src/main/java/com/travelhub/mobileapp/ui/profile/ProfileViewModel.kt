package com.travelhub.mobileapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.model.Post
import com.travelhub.mobileapp.data.model.UserProfile
import com.travelhub.mobileapp.data.repository.PostRepository
import com.travelhub.mobileapp.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val profile: UserProfile,
        val myPosts: List<Post>
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val postRepository: PostRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val profileResult = profileRepository.getProfile()
            val profile = profileResult.getOrNull()
            if (profile == null) {
                _uiState.value = ProfileUiState.Error("Couldn't load profile")
                return@launch
            }
            val allPosts = postRepository.getFeed().getOrDefault(emptyList())
            val myPosts = allPosts.filter { it.author == profile.username }
            _uiState.value = ProfileUiState.Success(profile, myPosts)
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            preferences.setLoggedIn(false)
            onLoggedOut()
        }
    }
}
