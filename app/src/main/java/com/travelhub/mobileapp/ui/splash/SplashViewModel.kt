package com.travelhub.mobileapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.repository.FavoriteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class SplashDestination { LOADING, ONBOARDING, AUTH, MAIN }

class SplashViewModel(
    private val preferences: AppPreferences,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.LOADING)
    val destination: StateFlow<SplashDestination> = _destination

    init {
        viewModelScope.launch {
            delay(800) // brief brand moment, feels intentional rather than a flash

            val onboardingDone = preferences.hasCompletedOnboarding.first()
            val loggedIn = preferences.isLoggedIn.first()

            if (loggedIn) {
                favoriteRepository.refresh() // ← add this
            }

            _destination.value = when {
                !onboardingDone -> SplashDestination.ONBOARDING
                loggedIn -> SplashDestination.MAIN
                else -> SplashDestination.AUTH
            }
        }
    }
}
