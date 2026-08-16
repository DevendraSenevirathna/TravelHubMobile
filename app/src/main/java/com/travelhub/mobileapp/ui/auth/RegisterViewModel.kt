package com.travelhub.mobileapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.model.AuthResult
import com.travelhub.mobileapp.data.model.RegisterRequest
import com.travelhub.mobileapp.data.repository.AuthRepository
import com.travelhub.mobileapp.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val preferences: AppPreferences,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _authResult.value = AuthResult.Error("Please fill in all fields")
            return
        }
        if (!email.contains("@")) {
            _authResult.value = AuthResult.Error("Please enter a valid email")
            return
        }
        if (password.length < 6) {
            _authResult.value = AuthResult.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            _authResult.value = AuthResult.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val result = authRepository.register(RegisterRequest(username, email, password))
            result.fold(
                onSuccess = {
                    preferences.setLoggedIn(true)
                    favoriteRepository.refresh()
                    _authResult.value = AuthResult.Success
                },
                onFailure = { e ->
                    _authResult.value = AuthResult.Error(e.message ?: "Registration failed")
                }
            )
        }
    }

    fun resetState() {
        _authResult.value = AuthResult.Idle
    }
}
