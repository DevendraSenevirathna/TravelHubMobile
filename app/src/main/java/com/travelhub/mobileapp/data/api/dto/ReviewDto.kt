package com.travelhub.mobileapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: Int,
    val spot: Int,
    val author: String = "",
    val rating: Int,
    val comment: String,
    val created_at: String = ""
)

@Serializable
data class CreateReviewRequestDto(
    val spot: Int,
    val rating: Int,
    val comment: String
)
