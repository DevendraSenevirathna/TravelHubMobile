package com.travelhub.mobileapp.data.api

import com.travelhub.mobileapp.data.api.dto.RefreshRequestDto
import com.travelhub.mobileapp.data.local.AppPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

// Runs automatically when the server responds with 401. Tries to refresh the
// access token once using the stored refresh token; if that also fails
// (refresh expired/invalid), logs the user out by clearing tokens.
class TokenAuthenticator(
    private val preferences: AppPreferences
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid infinite retry loops: if we've already retried once, give up.
        if (responseCount(response) >= 2) return null

        val refreshToken = runBlocking { preferences.getRefreshToken() } ?: return null

        val newAccessToken = runBlocking { attemptRefresh(refreshToken) } ?: run {
            // Refresh failed — clear tokens so the app treats the user as logged out
            runBlocking {
                preferences.clearTokens()
                preferences.setLoggedIn(false)
            }
            return null
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }

    private suspend fun attemptRefresh(refreshToken: String): String? {
        return try {
            // Build a minimal, separate Retrofit instance for the refresh call
            // to avoid recursive interceptor/authenticator loops on the main client.
            val json = Json { ignoreUnknownKeys = true }
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
            val api = retrofit.create(AuthApi::class.java)

            val response = api.refresh(RefreshRequestDto(refreshToken))
            if (response.isSuccessful) {
                val newAccess = response.body()?.access
                if (newAccess != null) {
                    preferences.saveTokens(newAccess, refreshToken) // refresh token unchanged unless rotated
                    newAccess
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
