package com.travelhub.mobileapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.model.AuthResult
import com.travelhub.mobileapp.data.model.LoginRequest
import com.travelhub.mobileapp.data.repository.AuthRepository
import com.travelhub.mobileapp.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferences: AppPreferences,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authResult.value = AuthResult.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val result = authRepository.login(LoginRequest(username, password))
            result.fold(
                onSuccess = {
                    preferences.setLoggedIn(true)
                    favoriteRepository.refresh() // ← add this
                    _authResult.value = AuthResult.Success
                },
                onFailure = { e ->
                    _authResult.value = AuthResult.Error(e.message ?: "Login failed")
                }
            )
        }
    }

    fun resetState() {
        _authResult.value = AuthResult.Idle
    }
}
