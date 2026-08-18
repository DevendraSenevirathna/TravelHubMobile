package com.travelhub.mobileapp.data.model

data class Service(
    val id: Int,
    val name: String,
    val category: String, // "hotel" | "transport" | "tour" | "guide"
    val description: String,
    val imageUrl: String?,
    val pricePerUnit: Double,
    val priceUnit: String, // e.g. "per night", "per trip", "per person"
    val rating: Double,
    val location: String
)

enum class BookingStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }

data class Booking(
    val id: Int,
    val serviceId: Int,
    val serviceName: String,
    val date: String,
    val guests: Int,
    val notes: String,
    val status: BookingStatus,
    val totalPrice: Double
)