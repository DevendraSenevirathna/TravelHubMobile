package com.travelhub.mobileapp.data.repository

import com.travelhub.mobileapp.data.api.FavoriteApi
import com.travelhub.mobileapp.data.api.dto.CreateFavoriteRequestDto
import com.travelhub.mobileapp.data.api.dto.FavoriteDto
import com.travelhub.mobileapp.data.model.Spot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Singleton — same reasoning as MockFavoriteRepository: favorite state must
// stay in sync across every screen (Home, Explore, Details, Favorites tab).
class RealFavoriteRepository(
    private val favoriteApi: FavoriteApi
) : FavoriteRepository {

    private val _favoriteSpotIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteSpotIds: StateFlow<Set<Int>> = _favoriteSpotIds.asStateFlow()

    // Maps spotId -> the favorite record's own id, needed for DELETE calls
    private val favoriteRecordIds = mutableMapOf<Int, Int>()
    private val cachedSpots = mutableMapOf<Int, Spot>()

    override suspend fun refresh(): Result<Unit> {
        return try {
            val response = favoriteApi.getFavorites()
            if (response.isSuccessful) {
                val favorites = response.body() ?: emptyList()
                favoriteRecordIds.clear()
                cachedSpots.clear()
                favorites.forEach { fav ->
                    favoriteRecordIds[fav.spot] = fav.id
                    cachedSpots[fav.spot] = fav.spot_detail.toDomain()
                }
                _favoriteSpotIds.value = favoriteRecordIds.keys.toSet()
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(networkErrorMessage(e)))
        }
    }

    override suspend fun toggleFavorite(spot: Spot): Result<Boolean> {
        val existingFavoriteId = favoriteRecordIds[spot.id]

        return if (existingFavoriteId != null) {
            // Currently favorited — remove it
            try {
                val response = favoriteApi.removeFavorite(existingFavoriteId)
                if (response.isSuccessful) {
                    favoriteRecordIds.remove(spot.id)
                    cachedSpots.remove(spot.id)
                    _favoriteSpotIds.value = favoriteRecordIds.keys.toSet()
                    Result.success(false)
                } else {
                    Result.failure(Exception(parseApiError(response.errorBody())))
                }
            } catch (e: Exception) {
                Result.failure(Exception(networkErrorMessage(e)))
            }
        } else {
            // Not favorited yet — add it
            try {
                val response = favoriteApi.addFavorite(CreateFavoriteRequestDto(spot.id))
                if (response.isSuccessful) {
                    val dto = response.body()
                    if (dto != null) {
                        favoriteRecordIds[spot.id] = dto.id
                        cachedSpots[spot.id] = spot
                        _favoriteSpotIds.value = favoriteRecordIds.keys.toSet()
                        Result.success(true)
                    } else {
                        Result.failure(Exception("Empty response from server"))
                    }
                } else {
                    Result.failure(Exception(parseApiError(response.errorBody())))
                }
            } catch (e: Exception) {
                Result.failure(Exception(networkErrorMessage(e)))
            }
        }
    }

    override suspend fun getFavoriteSpots(): Result<List<Spot>> {
        // Always refresh from server to stay accurate, then return cached spots
        val refreshResult = refresh()
        return if (refreshResult.isSuccess) {
            Result.success(cachedSpots.values.toList())
        } else {
            Result.failure(refreshResult.exceptionOrNull() ?: Exception("Failed to load favorites"))
        }
    }
}

private fun com.travelhub.mobileapp.data.api.dto.SpotDto.toDomain(): Spot {
    return Spot(
        id = id,
        name = name,
        description = description,
        category = category,
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
        status = status,
        createdBy = created_by,
        imageUrl = images.firstOrNull()?.image,
        averageRating = average_rating ?: 0.0,
        distanceKm = null
    )
}