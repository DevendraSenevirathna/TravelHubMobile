package com.travelhub.mobileapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.local.AppPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class SplashDestination { LOADING, ONBOARDING, AUTH, MAIN }

class SplashViewModel(
    private val preferences: AppPreferences
) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.LOADING)
    val destination: StateFlow<SplashDestination> = _destination

    init {
        viewModelScope.launch {
            delay(800) // brief brand moment, feels intentional rather than a flash

            val onboardingDone = preferences.hasCompletedOnboarding.first()
            val loggedIn = preferences.isLoggedIn.first()

            _destination.value = when {
                !onboardingDone -> SplashDestination.ONBOARDING
                loggedIn -> SplashDestination.MAIN
                else -> SplashDestination.AUTH
            }
        }
    }
}
