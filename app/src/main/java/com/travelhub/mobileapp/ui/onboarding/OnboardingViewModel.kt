package com.travelhub.mobileapp.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelhub.mobileapp.data.local.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferences: AppPreferences
) : ViewModel() {

    private val _selectedInterests = MutableStateFlow<Set<String>>(emptySet())
    val selectedInterests: StateFlow<Set<String>> = _selectedInterests

    fun toggleInterest(label: String) {
        _selectedInterests.value = if (label in _selectedInterests.value) {
            _selectedInterests.value - label
        } else {
            _selectedInterests.value + label
        }
    }

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            preferences.setOnboardingComplete()
            // TODO: persist selectedInterests to profile once real auth/profile API is wired (step 20+)
            onDone()
        }
    }
}