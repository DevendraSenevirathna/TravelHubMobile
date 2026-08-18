package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.model.LoginRequest
import com.travelhub.mobileapp.data.model.RegisterRequest
import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<Unit>
    suspend fun register(request: RegisterRequest): Result<Unit>
}

class MockAuthRepository : AuthRepository {
    override suspend fun login(request: LoginRequest): Result<Unit> {
        delay(1000) // simulate network
        return if (request.username.isNotBlank() && request.password.length >= 6) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        delay(1000)
        return if (request.username.isNotBlank() &&
            request.email.contains("@") &&
            request.password.length >= 6
        ) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Please check your details and try again"))
        }
    }
}
