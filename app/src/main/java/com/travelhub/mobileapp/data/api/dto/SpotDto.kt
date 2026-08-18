package com.travelhub.mobileapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SpotImageDto(
    val id: Int,
    val image: String
)

@Serializable
data class UploadImageResponseDto(
    val id: Int,
    val image: String
)

@Serializable
data class SpotDto(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val latitude: String,
    val longitude: String,
    val status: String,
    val created_by: String,
    val created_at: String,
    val images: List<SpotImageDto> = emptyList(),
    val average_rating: Double? = null   // ← nullable — backend sends null when no reviews exist yet
)

@Serializable
data class CreateSpotRequestDto(
    val name: String,
    val description: String,
    val category: String,
    val latitude: Double,
    val longitude: Double
)
