package com.travelhub.mobileapp.data.api

import com.travelhub.mobileapp.data.api.dto.CreatePostRequestDto
import com.travelhub.mobileapp.data.api.dto.PostDto
import com.travelhub.mobileapp.data.api.dto.ToggleLikeResponseDto
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import com.travelhub.mobileapp.data.api.dto.UploadImageResponseDto

interface PostApi {
    @GET("posts/")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("posts/{id}/")
    suspend fun getPostById(@Path("id") id: Int): Response<PostDto>

    @POST("posts/")
    suspend fun createPost(@Body body: CreatePostRequestDto): Response<PostDto>

    @PATCH("posts/{id}/")
    suspend fun updatePost(@Path("id") id: Int, @Body body: CreatePostRequestDto): Response<PostDto>

    @DELETE("posts/{id}/")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>

    @POST("posts/{id}/toggle_like/")
    suspend fun toggleLike(@Path("id") id: Int): Response<ToggleLikeResponseDto>

    @Multipart
    @POST("posts/{id}/upload_image/")
    suspend fun uploadPostImage(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponseDto>

    @DELETE("posts/{id}/images/{imageId}/")
    suspend fun deletePostImage(
        @Path("id") id: Int,
        @Path("imageId") imageId: Int
    ): Response<Unit>
}
