package com.travelhub.mobileapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelhub.mobileapp.data.api.AuthApi
import com.travelhub.mobileapp.data.api.RetrofitClient
import com.travelhub.mobileapp.data.api.SpotApi
import com.travelhub.mobileapp.data.local.AppPreferences
import com.travelhub.mobileapp.data.repository.*
import com.travelhub.mobileapp.ui.auth.LoginViewModel
import com.travelhub.mobileapp.ui.auth.RegisterViewModel
import com.travelhub.mobileapp.ui.explore.ExploreViewModel
import com.travelhub.mobileapp.ui.favorites.FavoritesViewModel
import com.travelhub.mobileapp.ui.home.HomeViewModel
import com.travelhub.mobileapp.ui.onboarding.OnboardingViewModel
import com.travelhub.mobileapp.ui.profile.EditProfileViewModel
import com.travelhub.mobileapp.ui.profile.ProfileViewModel
import com.travelhub.mobileapp.ui.splash.SplashViewModel

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val preferences = AppPreferences(context.applicationContext)
        val retrofit = RetrofitClient.getInstance(preferences)

        val authApi = retrofit.create(AuthApi::class.java)
        val spotApi = retrofit.create(SpotApi::class.java)

        val authRepository: AuthRepository = RealAuthRepository(authApi, preferences)
        val spotRepository: SpotRepository = RealSpotRepository(spotApi) // ← swapped from Mock

        // Still mock — swapped one-by-one in upcoming steps
        val postRepository = MockPostRepository()
        val favoriteRepository = MockFavoriteRepository
        val profileRepository = MockProfileRepository

        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(preferences) as T
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(preferences) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(authRepository, preferences) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(authRepository, preferences) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(spotRepository, postRepository, favoriteRepository) as T
            modelClass.isAssignableFrom(ExploreViewModel::class.java) ->
                ExploreViewModel(spotRepository, favoriteRepository) as T
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) ->
                FavoritesViewModel(favoriteRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(profileRepository, postRepository, preferences) as T
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) ->
                EditProfileViewModel(profileRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}