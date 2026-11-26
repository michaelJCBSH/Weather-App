package com.jcbsh.weather.core.domain.repository

import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.domain.result.DataResult
import kotlinx.coroutines.flow.Flow

/** The user's Saved Locations. */
interface SavedLocationRepository {
    /** Saved Locations, oldest first. */
    fun observeSavedLocations(): Flow<List<SavedLocation>>

    /** Saves [location]. Saving one that is already saved is a no-op. */
    suspend fun save(location: Location): DataResult<Unit>

    suspend fun remove(locationId: Long): DataResult<Unit>
}
