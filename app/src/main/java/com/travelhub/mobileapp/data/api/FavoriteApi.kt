package com.travelhub.mobileapp.data.api

import com.travelhub.mobileapp.data.api.dto.CreateFavoriteRequestDto
import com.travelhub.mobileapp.data.api.dto.FavoriteDto
import retrofit2.Response
import retrofit2.http.*

interface FavoriteApi {
    @GET("favorites/")
    suspend fun getFavorites(): Response<List<FavoriteDto>>

    @POST("favorites/")
    suspend fun addFavorite(@Body body: CreateFavoriteRequestDto): Response<FavoriteDto>

    @DELETE("favorites/{id}/")
    suspend fun removeFavorite(@Path("id") id: Int): Response<Unit>
}
