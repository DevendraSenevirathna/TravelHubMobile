package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.model.Review
import kotlinx.coroutines.delay

interface ReviewRepository {
    suspend fun getReviewsForSpot(spotId: Int): Result<List<Review>>
    suspend fun addReview(spotId: Int, rating: Int, comment: String): Result<Review>
}

class MockReviewRepository : ReviewRepository {

    private val mockReviews = mutableListOf(
        Review(1, 1, "traveler2", 5, "Absolutely stunning, worth the hike!", "2026-08-05T10:00:00Z"),
        Review(2, 1, "traveler3", 4, "Beautiful but a bit crowded on weekends.", "2026-08-07T14:00:00Z"),
        Review(3, 2, "traveler1", 5, "Best sunrise view in the region.", "2026-08-06T05:00:00Z"),
        Review(4, 3, "traveler4", 4, "Peaceful and clean, great for families.", "2026-08-08T11:00:00Z")
    )

    override suspend fun getReviewsForSpot(spotId: Int): Result<List<Review>> {
        delay(300)
        return Result.success(mockReviews.filter { it.spotId == spotId })
    }

    override suspend fun addReview(spotId: Int, rating: Int, comment: String): Result<Review> {
        delay(400)
        val newReview = Review(
            id = (mockReviews.maxOfOrNull { it.id } ?: 0) + 1,
            spotId = spotId,
            author = "you",
            rating = rating,
            comment = comment,
            createdAt = "2026-08-15T00:00:00Z"
        )
        mockReviews.add(newReview)
        return Result.success(newReview)
    }
}
