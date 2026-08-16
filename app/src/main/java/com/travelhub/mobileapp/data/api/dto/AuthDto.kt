package com.travelhub.mobileapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponseDto(
    val id: Int,
    val username: String,
    val email: String
)

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val access: String,
    val refresh: String
)

@Serializable
data class RefreshRequestDto(
    val refresh: String
)

@Serializable
data class RefreshResponseDto(
    val access: String
)

@Serializable
data class ProfileDto(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String = "",
    val last_name: String = "",
    val bio: String = "",
    val profile_image: String? = null,
    val interests: List<String> = emptyList(),
    val date_joined: String
)

@Serializable
data class UpdateProfileRequestDto(
    val bio: String,
    val interests: List<String>
)
