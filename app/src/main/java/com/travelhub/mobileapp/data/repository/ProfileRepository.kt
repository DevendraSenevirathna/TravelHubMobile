package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ProfileRepository {
    val currentProfile: StateFlow<UserProfile?>
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(bio: String, interests: List<String>): Result<UserProfile>
}

// Singleton — same reasoning as MockFavoriteRepository: profile is shared
// app-wide state, must stay in sync across every screen that reads it.
object MockProfileRepository : ProfileRepository {

    private val _currentProfile = MutableStateFlow<UserProfile?>(
        UserProfile(
            id = 1,
            username = "you",
            email = "you@travelhub.test",
            firstName = "",
            lastName = "",
            bio = "Exploring the world one spot at a time 🌍",
            profileImageUrl = null,
            interests = listOf("Beaches", "Hiking Trails"),
            dateJoined = "2026-06-01T00:00:00Z"
        )
    )
    override val currentProfile: StateFlow<UserProfile?> = _currentProfile.asStateFlow()

    override suspend fun getProfile(): Result<UserProfile> {
        delay(300)
        return _currentProfile.value?.let { Result.success(it) }
            ?: Result.failure(Exception("Not logged in"))
    }

    override suspend fun updateProfile(bio: String, interests: List<String>): Result<UserProfile> {
        delay(400)
        val current = _currentProfile.value ?: return Result.failure(Exception("Not logged in"))
        val updated = current.copy(bio = bio, interests = interests)
        _currentProfile.value = updated
        return Result.success(updated)
    }
}
