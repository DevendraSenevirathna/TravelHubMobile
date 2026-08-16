package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.api.SpotApi
import com.travelhub.mobileapp.data.api.dto.SpotDto
import com.travelhub.mobileapp.data.model.Spot

class RealSpotRepository(
    private val spotApi: SpotApi
) : SpotRepository {

    override suspend fun getNearbySpots(): Result<List<Spot>> {
        // No real "nearby" logic on the backend yet (no GPS-based endpoint) —
        // for now, just return all approved spots. Revisit once/if the backend
        // adds location-based sorting (flagged as a "Future Improvement" in ReadMe.md).
        return getAllSpots()
    }

    override suspend fun getTrendingSpots(): Result<List<Spot>> {
        // Same gap — no dedicated "trending" endpoint. Approximate using
        // rating as a stand-in until/unless the backend defines real trending logic.
        return getAllSpots().map { spots -> spots.sortedByDescending { it.averageRating } }
    }

    override suspend fun getAllSpots(): Result<List<Spot>> {
        return try {
            val response = spotApi.getSpots()
            if (response.isSuccessful) {
                val spots = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.success(spots)
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun searchSpots(query: String, category: String?): Result<List<Spot>> {
        return try {
            val response = spotApi.getSpots(search = query.ifBlank { null })
            if (response.isSuccessful) {
                var spots = response.body()?.map { it.toDomain() } ?: emptyList()
                // Category filtering isn't in the API's ?search= — filter client-side
                if (category != null) {
                    spots = spots.filter { it.category == category }
                }
                Result.success(spots)
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun getSpotById(id: Int): Result<Spot> {
        return try {
            val response = spotApi.getSpotById(id)
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) Result.success(dto.toDomain())
                else Result.failure(Exception("Spot not found"))
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }
}

// Maps the API's wire format (SpotDto) to the UI's domain model (Spot).
private fun SpotDto.toDomain(): Spot {
    return Spot(
        id = id,
        name = name,
        description = description,
        category = category,
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
        status = status,
        createdBy = created_by,
        imageUrl = images.firstOrNull()?.image,
        averageRating = average_rating ?: 0.0,   // ← null (no reviews yet) becomes 0.0 for display
        distanceKm = null
    )
}