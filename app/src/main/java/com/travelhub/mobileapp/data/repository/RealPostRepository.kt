package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.api.PostApi
import com.travelhub.mobileapp.data.api.dto.CreatePostRequestDto
import com.travelhub.mobileapp.data.api.dto.PostDto
import com.travelhub.mobileapp.data.model.Post

class RealPostRepository(
    private val postApi: PostApi
) : PostRepository {

    override suspend fun getFeed(): Result<List<Post>> {
        return try {
            val response = postApi.getPosts()
            if (response.isSuccessful) {
                val posts = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.success(posts)
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun getPostById(id: Int): Result<Post> {
        return try {
            val response = postApi.getPostById(id)
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) Result.success(dto.toDomain())
                else Result.failure(Exception("Post not found"))
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun getPostsForSpot(spotId: Int): Result<List<Post>> {
        // No dedicated "posts by spot" endpoint in the API reference —
        // fetch the full feed and filter client-side. Flag to backend dev
        // if this list grows large enough that a ?spot= query param would help.
        return getFeed().map { posts -> posts.filter { it.spotId == spotId } }
    }

    override suspend fun toggleLike(postId: Int): Result<Boolean> {
        return try {
            val response = postApi.toggleLike(postId)
            if (response.isSuccessful) {
                val liked = response.body()?.liked ?: false
                Result.success(liked)
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun createPost(caption: String, spotId: Int?): Result<Post> {
        return try {
            val response = postApi.createPost(CreatePostRequestDto(caption, spotId))
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) Result.success(dto.toDomain())
                else Result.failure(Exception("Empty response from server"))
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun updatePost(id: Int, caption: String): Result<Post> {
        return try {
            // spot is omitted from edits — API reference doesn't clarify whether
            // PATCH requires re-sending spot; sending null here would risk
            // accidentally clearing an existing spot tag. Caption-only PATCH is safer.
            val response = postApi.updatePost(id, CreatePostRequestDto(caption, null))
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) Result.success(dto.toDomain())
                else Result.failure(Exception("Empty response from server"))
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun deletePost(id: Int): Result<Unit> {
        return try {
            val response = postApi.deletePost(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }
}

private fun PostDto.toDomain(): Post {
    return Post(
        id = id,
        author = author,
        authorAvatarUrl = null, // API doesn't return author avatar in post payload
        spotId = spot,
        spotName = spot_name,
        caption = caption,
        imageUrl = images.firstOrNull()?.image,
        createdAt = created_at,
        likesCount = likes_count,
        isLiked = is_liked
    )
}