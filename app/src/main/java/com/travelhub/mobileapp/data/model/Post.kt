package com.travelhub.mobileapp.data.model

data class Post(
    val id: Int,
    val author: String,
    val authorAvatarUrl: String?,
    val spotId: Int?,
    val spotName: String?,
    val caption: String,
    val imageUrl: String?,
    val createdAt: String,
    val likesCount: Int,
    val isLiked: Boolean
)
