package com.travelhub.mobileapp.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.travelhub.mobileapp.data.local.AppPreferences
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val json = Json {
        ignoreUnknownKeys = true // tolerate backend adding fields we don't model yet
        isLenient = true
        encodeDefaults = true
    }

    private var retrofit: Retrofit? = null

    fun getInstance(preferences: AppPreferences): Retrofit {
        return retrofit ?: buildRetrofit(preferences).also { retrofit = it }
    }

    private fun buildRetrofit(preferences: AppPreferences): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(preferences))
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenAuthenticator(preferences))   // ← add this
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}