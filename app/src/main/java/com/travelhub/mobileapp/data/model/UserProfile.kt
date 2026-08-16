package com.travelhub.mobileapp.data.model

data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val bio: String,
    val profileImageUrl: String?,
    val interests: List<String>,
    val dateJoined: String
)
