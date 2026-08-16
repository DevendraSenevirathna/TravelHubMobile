package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.api.AuthApi
import com.travelhub.mobileapp.data.api.dto.LoginRequestDto
import com.travelhub.mobileapp.data.api.dto.RegisterRequestDto
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.model.LoginRequest
import com.travelhub.mobileapp.data.model.RegisterRequest
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody

class RealAuthRepository(
    private val authApi: AuthApi,
    private val preferences: AppPreferences
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<Unit> {
        return try {
            val response = authApi.login(LoginRequestDto(request.username, request.password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    preferences.saveTokens(body.access, body.refresh)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val response = authApi.register(
                RegisterRequestDto(request.username, request.email, request.password)
            )
            if (response.isSuccessful) {
                // Registration succeeded but doesn't return tokens (per API reference) —
                // log the user in immediately afterward using the same credentials.
                login(LoginRequest(request.username, request.password))
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }
}

// Shared helpers used across real repositories — kept here for now, can move to
// a common file (e.g. ApiErrorUtils.kt) once more repositories need them.
fun parseApiError(errorBody: ResponseBody?): String {
    val raw = errorBody?.string() ?: return "Something went wrong"
    return try {
        val json = Json { ignoreUnknownKeys = true }
        val element = json.parseToJsonElement(raw)
        // Try common DRF error shapes: {"detail": "..."} or {"field": ["msg"]}
        val obj = element.jsonObject
        obj["detail"]?.jsonPrimitive?.content
            ?: obj["non_field_errors"]?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
            ?: obj.entries.firstOrNull()?.value?.let { v ->
                if (v is kotlinx.serialization.json.JsonArray) v.firstOrNull()?.jsonPrimitive?.content else null
            }
            ?: "Request failed"
    } catch (e: Exception) {
        "Request failed"
    }
}

fun networkErrorMessage(e: Exception): String = when (e) {
    is java.net.ConnectException -> "Can't reach the server. Check your connection."
    is java.net.SocketTimeoutException -> "Request timed out. Try again."
    is java.net.UnknownHostException -> "No internet connection."
    else -> e.message ?: "Something went wrong"
}
