package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.model.Spot
import kotlinx.coroutines.delay
import okhttp3.MultipartBody

interface SpotRepository {
    suspend fun getNearbySpots(): Result<List<Spot>>
    suspend fun getTrendingSpots(): Result<List<Spot>>
    suspend fun getAllSpots(): Result<List<Spot>>
    suspend fun searchSpots(query: String, category: String?): Result<List<Spot>>
    suspend fun getSpotById(id: Int): Result<Spot>
    suspend fun createSpot(name: String, description: String, category: String, latitude: Double, longitude: Double): Result<Spot>
    suspend fun uploadSpotImage(spotId: Int, imagePart: MultipartBody.Part): Result<String> // returns image URL
    suspend fun deleteSpotImage(spotId: Int, imageId: Int): Result<Unit>
}

class MockSpotRepository : SpotRepository {

    private val mockSpots = listOf(
        Spot(1, "Hidden Waterfall", "A quiet spot off the trail, surrounded by lush greenery.", "waterfall", 14.5995, 120.9842, "approved", "traveler1", null, 4.5, 2.3),
        Spot(2, "Emerald Ridge", "Panoramic mountain views perfect for sunrise hikes.", "mountain", 14.6100, 121.0200, "approved", "traveler2", null, 4.8, 5.1),
        Spot(3, "Sunset Cove", "A secluded beach with golden sand and calm waters.", "beach", 14.5800, 120.9700, "approved", "traveler3", null, 4.2, 8.4),
        Spot(4, "Pine Trail Camp", "A peaceful camping ground under tall pine trees.", "camping", 14.6300, 121.0500, "approved", "traveler1", null, 4.0, 12.7),
        Spot(5, "Riverside Hotel", "Cozy riverside stay with mountain views.", "hotel", 14.6000, 121.0000, "approved", "traveler4", null, 4.6, 3.9),
        Spot(6, "Cloud Nine Peak", "Popular hiking trail with cloud-level viewpoints.", "hiking", 14.6500, 121.0300, "approved", "traveler2", null, 4.9, 15.2),
        Spot(7, "Golden Sands Resort", "Beachfront restaurant known for fresh seafood.", "restaurant", 14.5700, 120.9600, "approved", "traveler5", null, 4.3, 9.1)
    )

    override suspend fun getNearbySpots(): Result<List<Spot>> {
        delay(400)
        return Result.success(mockSpots.sortedBy { it.distanceKm ?: Double.MAX_VALUE }.take(5))
    }

    override suspend fun getTrendingSpots(): Result<List<Spot>> {
        delay(400)
        return Result.success(mockSpots.sortedByDescending { it.averageRating }.take(5))
    }

    override suspend fun getAllSpots(): Result<List<Spot>> {
        delay(400)
        return Result.success(mockSpots)
    }

    override suspend fun searchSpots(query: String, category: String?): Result<List<Spot>> {
        delay(300)
        val filtered = mockSpots.filter { spot ->
            (query.isBlank() || spot.name.contains(query, ignoreCase = true) ||
                spot.description.contains(query, ignoreCase = true)) &&
                (category == null || spot.category == category)
        }
        return Result.success(filtered)
    }
    override suspend fun getSpotById(id: Int): Result<Spot> {
        delay(300)
        val spot = mockSpots.find { it.id == id }
        return if (spot != null) Result.success(spot) else Result.failure(Exception("Spot not found"))
    }

    override suspend fun createSpot(
        name: String, description: String, category: String, latitude: Double, longitude: Double
    ): Result<Spot> {
        delay(400)
        val newSpot = Spot(
            id = (mockSpots.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            description = description,
            category = category,
            latitude = latitude,
            longitude = longitude,
            status = "pending",
            createdBy = "you",
            imageUrl = null,
            averageRating = 0.0
        )
        return Result.success(newSpot)
    }
    override suspend fun uploadSpotImage(spotId: Int, imagePart: MultipartBody.Part): Result<String> {
        delay(400)
        return Result.success("https://example.com/mock-image.jpg")
    }
    override suspend fun deleteSpotImage(spotId: Int, imageId: Int): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }
}
