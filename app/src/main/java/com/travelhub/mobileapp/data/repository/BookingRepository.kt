package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.model.Booking
import com.travelhub.mobileapp.data.model.BookingStatus
import com.travelhub.mobileapp.data.model.Service
import kotlinx.coroutines.delay

interface BookingRepository {
    suspend fun getServices(): Result<List<Service>>
    suspend fun getServiceById(id: Int): Result<Service>
    suspend fun createBooking(serviceId: Int, date: String, guests: Int, notes: String): Result<Booking>
    suspend fun getBookingHistory(): Result<List<Booking>>
    suspend fun cancelBooking(bookingId: Int): Result<Unit>
}

// Singleton so bookings created in this session persist across screen navigation
object MockBookingRepository : BookingRepository {

    private val mockServices = listOf(
        Service(1, "Riverside Hotel Stay", "hotel", "Cozy riverside rooms with mountain views and breakfast included.", null, 45.0, "per night", 4.6, "Near Emerald Ridge"),
        Service(2, "Mountain Trail Guide", "guide", "Certified local guide for the Cloud Nine Peak hiking trail.", null, 25.0, "per person", 4.9, "Cloud Nine Peak"),
        Service(3, "Airport Transfer Van", "transport", "Comfortable shared van service to/from the airport.", null, 15.0, "per trip", 4.3, "City-wide"),
        Service(4, "Sunset Cove Beach Tour", "tour", "Half-day guided tour of the hidden beaches nearby.", null, 30.0, "per person", 4.7, "Sunset Cove"),
        Service(5, "Camping Gear Rental", "guide", "Full camping kit rental — tent, sleeping bag, and cooking gear.", null, 12.0, "per day", 4.1, "Pine Trail Camp")
    )

    private val bookings = mutableListOf<Booking>()
    private var nextBookingId = 1

    override suspend fun getServices(): Result<List<Service>> {
        delay(400)
        return Result.success(mockServices)
    }

    override suspend fun getServiceById(id: Int): Result<Service> {
        delay(300)
        val service = mockServices.find { it.id == id }
        return if (service != null) Result.success(service) else Result.failure(Exception("Service not found"))
    }

    override suspend fun createBooking(serviceId: Int, date: String, guests: Int, notes: String): Result<Booking> {
        delay(500)
        val service = mockServices.find { it.id == serviceId }
            ?: return Result.failure(Exception("Service not found"))

        val booking = Booking(
            id = nextBookingId++,
            serviceId = serviceId,
            serviceName = service.name,
            date = date,
            guests = guests,
            notes = notes,
            status = BookingStatus.CONFIRMED, // mock always confirms instantly
            totalPrice = service.pricePerUnit * guests
        )
        bookings.add(0, booking)
        return Result.success(booking)
    }

    override suspend fun getBookingHistory(): Result<List<Booking>> {
        delay(300)
        return Result.success(bookings.toList())
    }

    override suspend fun cancelBooking(bookingId: Int): Result<Unit> {
        delay(300)
        val index = bookings.indexOfFirst { it.id == bookingId }
        if (index == -1) return Result.failure(Exception("Booking not found"))
        bookings[index] = bookings[index].copy(status = BookingStatus.CANCELLED)
        return Result.success(Unit)
    }
}
