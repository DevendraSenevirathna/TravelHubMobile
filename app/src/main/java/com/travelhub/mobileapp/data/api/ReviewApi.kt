package com.travelhub.mobileapp.data.api

import com.travelhub.mobileapp.data.api.dto.CreateReviewRequestDto
import com.travelhub.mobileapp.data.api.dto.ReviewDto
import retrofit2.Response
import retrofit2.http.*

interface ReviewApi {
    @GET("reviews/")
    suspend fun getReviews(@Query("spot") spotId: Int): Response<List<ReviewDto>>

    @POST("reviews/")
    suspend fun createReview(@Body body: CreateReviewRequestDto): Response<ReviewDto>

    @PATCH("reviews/{id}/")
    suspend fun updateReview(@Path("id") id: Int, @Body body: CreateReviewRequestDto): Response<ReviewDto>

    @DELETE("reviews/{id}/")
    suspend fun deleteReview(@Path("id") id: Int): Response<Unit>
}
