package com.travelhub.mobileapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    val id: Int,
    val user: Int,
    val spot: Int,
    val spot_detail: SpotDto,
    val created_at: String
)

@Serializable
data class CreateFavoriteRequestDto(
    val spot: Int
)
