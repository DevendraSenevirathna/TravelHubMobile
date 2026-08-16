package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.model.Spot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface FavoriteRepository {
    val favoriteSpotIds: StateFlow<Set<Int>>
    suspend fun toggleFavorite(spot: Spot): Result<Boolean>
    suspend fun getFavoriteSpots(): Result<List<Spot>>
    suspend fun refresh(): Result<Unit>
}

// Singleton mock repository — must be shared across screens so favorite state
// stays in sync. See AppViewModelFactory for how it's provided.
object MockFavoriteRepository : FavoriteRepository {

    private val _favoriteSpotIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteSpotIds: StateFlow<Set<Int>> = _favoriteSpotIds.asStateFlow()

    private val favoriteSpots = mutableMapOf<Int, Spot>()

    override suspend fun toggleFavorite(spot: Spot): Result<Boolean> {
        delay(200)
        val isNowFavorite: Boolean
        if (spot.id in _favoriteSpotIds.value) {
            _favoriteSpotIds.value -= spot.id
            favoriteSpots.remove(spot.id)
            isNowFavorite = false
        } else {
            _favoriteSpotIds.value += spot.id
            favoriteSpots[spot.id] = spot
            isNowFavorite = true
        }
        return Result.success(isNowFavorite)
    }

    override suspend fun getFavoriteSpots(): Result<List<Spot>> {
        delay(300)
        return Result.success(favoriteSpots.values.toList())
    }

    override suspend fun refresh(): Result<Unit> {
        return Result.success(Unit) // mock has no external source to reload from
    }
}
