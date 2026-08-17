package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.api.ReviewApi
import com.travelhub.mobileapp.data.api.dto.CreateReviewRequestDto
import com.travelhub.mobileapp.data.api.dto.ReviewDto
import com.travelhub.mobileapp.data.model.Review

class RealReviewRepository(
    private val reviewApi: ReviewApi
) : ReviewRepository {

    override suspend fun getReviewsForSpot(spotId: Int): Result<List<Review>> {
        return try {
            val response = reviewApi.getReviews(spotId)
            if (response.isSuccessful) {
                val reviews = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.success(reviews)
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun addReview(spotId: Int, rating: Int, comment: String): Result<Review> {
        return try {
            val response = reviewApi.createReview(CreateReviewRequestDto(spotId, rating, comment))
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) Result.success(dto.toDomain())
                else Result.failure(Exception("Empty response from server"))
            } else {
                // Backend returns a specific message for the "one review per user
                // per spot" rule via non_field_errors — parseApiError already
                // surfaces that correctly since it checks non_field_errors first.
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }
}

private fun ReviewDto.toDomain(): Review {
    return Review(
        id = id,
        spotId = spot,
        author = author,
        rating = rating,
        comment = comment,
        createdAt = created_at
    )
}
