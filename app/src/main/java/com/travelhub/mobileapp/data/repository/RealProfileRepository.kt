package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.api.AuthApi
import com.travelhub.mobileapp.data.api.dto.ProfileDto
import com.travelhub.mobileapp.data.api.dto.UpdateProfileRequestDto
import com.travelhub.mobileapp.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Singleton — same reasoning as RealFavoriteRepository: profile is shared,
// app-wide state that multiple screens read (Profile, Edit Profile, Post
// author-matching for edit/delete permissions).
class RealProfileRepository(
    private val authApi: AuthApi
) : ProfileRepository {

    private val _currentProfile = MutableStateFlow<UserProfile?>(null)
    override val currentProfile: StateFlow<UserProfile?> = _currentProfile.asStateFlow()

    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val response = authApi.getProfile()
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val profile = dto.toDomain()
                    _currentProfile.value = profile
                    Result.success(profile)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun updateProfile(bio: String, interests: List<String>): Result<UserProfile> {
        return try {
            val response = authApi.updateProfile(UpdateProfileRequestDto(bio, interests))
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val profile = dto.toDomain()
                    _currentProfile.value = profile
                    Result.success(profile)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }
}

private fun ProfileDto.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        username = username,
        email = email,
        firstName = first_name,
        lastName = last_name,
        bio = bio,
        profileImageUrl = profile_image,
        interests = interests,
        dateJoined = date_joined
    )
}
