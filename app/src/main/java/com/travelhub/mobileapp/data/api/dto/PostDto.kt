package com.travelhub.mobileapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostImageDto(
    val id: Int,
    val image: String
)

@Serializable
data class PostDto(
    val id: Int,
    val author: String,
    val spot: Int? = null,
    val spot_name: String? = null,
    val caption: String,
    val created_at: String,
    val images: List<PostImageDto> = emptyList(),
    val likes_count: Int = 0,
    val is_liked: Boolean = false
)

@Serializable
data class CreatePostRequestDto(
    val caption: String,
    val spot: Int? = null
)

@Serializable
data class ToggleLikeResponseDto(
    val liked: Boolean
)