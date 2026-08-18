package com.travelhub.mobileapp.data.api

import com.travelhub.mobileapp.data.api.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("users/register/")
    suspend fun register(@Body body: RegisterRequestDto): Response<RegisterResponseDto>

    @POST("auth/login/")
    suspend fun login(@Body body: LoginRequestDto): Response<LoginResponseDto>

    @POST("auth/refresh/")
    suspend fun refresh(@Body body: RefreshRequestDto): Response<RefreshResponseDto>

    @GET("users/profile/")
    suspend fun getProfile(): Response<ProfileDto>

    @PUT("users/profile/")
    suspend fun updateProfile(@Body body: UpdateProfileRequestDto): Response<ProfileDto>
}
