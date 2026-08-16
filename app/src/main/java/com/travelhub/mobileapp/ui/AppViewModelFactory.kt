package com.travelhub.mobileapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelhub.mobileapp.data.api.AuthApi
import com.travelhub.mobileapp.data.api.FavoriteApi
import com.travelhub.mobileapp.data.api.PostApi
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

object RepositoryProvider {
    // Real favorites now need to be a genuine singleton too (like MockFavoriteRepository
    // was via `object`), since RealFavoriteRepository is a class with in-memory state
    // that must be shared across every screen. Built lazily, once, here.
    private var favoriteRepositoryInstance: FavoriteRepository? = null

    fun getFavoriteRepository(favoriteApi: FavoriteApi): FavoriteRepository {
        return favoriteRepositoryInstance ?: RealFavoriteRepository(favoriteApi).also {
            favoriteRepositoryInstance = it
        }
    }
}

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val preferences = AppPreferences(context.applicationContext)
        val retrofit = RetrofitClient.getInstance(preferences)

        val authApi = retrofit.create(AuthApi::class.java)
        val spotApi = retrofit.create(SpotApi::class.java)
        val postApi = retrofit.create(PostApi::class.java)
        val favoriteApi = retrofit.create(FavoriteApi::class.java)

        val authRepository: AuthRepository = RealAuthRepository(authApi, preferences)
        val spotRepository: SpotRepository = RealSpotRepository(spotApi)
        val postRepository: PostRepository = RealPostRepository(postApi)
        val favoriteRepository: FavoriteRepository = RepositoryProvider.getFavoriteRepository(favoriteApi) // ← swapped

        // Still mock — swapped in the next step
        val profileRepository = MockProfileRepository

        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(preferences, favoriteRepository) as T
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(preferences) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(authRepository, preferences, favoriteRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(authRepository, preferences, favoriteRepository) as T
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