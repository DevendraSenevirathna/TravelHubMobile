package com.travelhub.mobileapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "travelhub_prefs")

class AppPreferences(private val context: Context) {

    private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in") // mock for now

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[ONBOARDING_COMPLETE] ?: false }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[IS_LOGGED_IN] ?: false }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETE] = true }
    }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs -> prefs[IS_LOGGED_IN] = value }
    }
}