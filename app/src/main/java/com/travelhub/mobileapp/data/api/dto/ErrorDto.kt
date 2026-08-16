package com.travelhub.mobileapp.data.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ErrorResponseDto(
    val detail: String? = null,
    val non_field_errors: List<String>? = null
)