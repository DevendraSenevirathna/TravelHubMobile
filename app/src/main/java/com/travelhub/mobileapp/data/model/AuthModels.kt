package com.travelhub.mobileapp.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
