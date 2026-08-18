package com.travelhub.mobileapp.data.api

import com.travelhub.mobileapp.data.api.dto.CreateSpotRequestDto
import com.travelhub.mobileapp.data.api.dto.SpotDto
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import com.travelhub.mobileapp.data.api.dto.UploadImageResponseDto

interface SpotApi {
    @GET("spots/")
    suspend fun getSpots(@Query("search") search: String? = null): Response<List<SpotDto>>

    @GET("spots/{id}/")
    suspend fun getSpotById(@Path("id") id: Int): Response<SpotDto>

    @POST("spots/")
    suspend fun createSpot(@Body body: CreateSpotRequestDto): Response<SpotDto>

    @PATCH("spots/{id}/")
    suspend fun updateSpot(@Path("id") id: Int, @Body body: CreateSpotRequestDto): Response<SpotDto>

    @DELETE("spots/{id}/")
    suspend fun deleteSpot(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("spots/{id}/upload_image/")
    suspend fun uploadSpotImage(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponseDto>

    @DELETE("spots/{id}/images/{imageId}/")
    suspend fun deleteSpotImage(
        @Path("id") id: Int,
        @Path("imageId") imageId: Int
    ): Response<Unit>
}
