package com.travelhub.mobileapp.data.model

data class Spot(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val status: String, // "pending" | "approved" | "rejected"
    val createdBy: String,
    val imageUrl: String?,
    val averageRating: Double,
    val distanceKm: Double? = null // used for "nearby" sorting, mock-only for now
)
