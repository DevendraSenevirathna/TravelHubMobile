package com.travelhub.mobileapp.data.api

import com.travelhub.mobileapp.data.api.dto.CreateSpotRequestDto
import com.travelhub.mobileapp.data.api.dto.SpotDto
import retrofit2.Response
import retrofit2.http.*

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
}
