package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.model.Post
import kotlinx.coroutines.delay

interface PostRepository {
    suspend fun getFeed(): Result<List<Post>>
    suspend fun toggleLike(postId: Int): Result<Boolean>
    suspend fun createPost(caption: String, spotId: Int?): Result<Post>
    suspend fun getPostsForSpot(spotId: Int): Result<List<Post>>
}

class MockPostRepository : PostRepository {

    private val mockPosts = mutableListOf(
        Post(1, "traveler1", null, 1, "Hidden Waterfall", "Amazing sunset at this spot! Truly peaceful.", null, "2026-08-10T12:00:00Z", 12, false),
        Post(2, "traveler2", null, 2, "Emerald Ridge", "Woke up at 4am for this view — worth every step.", null, "2026-08-11T06:30:00Z", 27, true),
        Post(3, "traveler3", null, 3, "Sunset Cove", "Found this hidden beach completely by accident!", null, "2026-08-12T17:15:00Z", 8, false),
        Post(4, "traveler4", null, null, null, "Just packed for my next adventure. Where should I go next?", null, "2026-08-13T09:00:00Z", 5, false)
    )

    override suspend fun getFeed(): Result<List<Post>> {
        delay(400)
        return Result.success(mockPosts.sortedByDescending { it.createdAt })
    }

    override suspend fun toggleLike(postId: Int): Result<Boolean> {
        delay(150)
        val index = mockPosts.indexOfFirst { it.id == postId }
        if (index == -1) return Result.failure(Exception("Post not found"))
        val post = mockPosts[index]
        val updated = post.copy(
            isLiked = !post.isLiked,
            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
        )
        mockPosts[index] = updated
        return Result.success(updated.isLiked)
    }

    override suspend fun createPost(caption: String, spotId: Int?): Result<Post> {
        delay(500)
        val newPost = Post(
            id = mockPosts.maxOf { it.id } + 1,
            author = "you",
            authorAvatarUrl = null,
            spotId = spotId,
            spotName = null,
            caption = caption,
            imageUrl = null,
            createdAt = "2026-08-15T00:00:00Z",
            likesCount = 0,
            isLiked = false
        )
        mockPosts.add(0, newPost)
        return Result.success(newPost)
    }
    override suspend fun getPostsForSpot(spotId: Int): Result<List<Post>> {
        delay(300)
        return Result.success(mockPosts.filter { it.spotId == spotId })
    }
}