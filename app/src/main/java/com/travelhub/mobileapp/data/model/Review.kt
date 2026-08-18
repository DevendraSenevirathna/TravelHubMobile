package com.travelhub.mobileapp.data.model

data class Review(
    val id: Int,
    val spotId: Int,
    val author: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)
