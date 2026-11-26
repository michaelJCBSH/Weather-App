package com.jcbsh.weather.core.testing.fake

import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.domain.repository.SavedLocationRepository
import com.jcbsh.weather.core.domain.result.DataError
import com.jcbsh.weather.core.domain.result.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant

class FakeSavedLocationRepository(
    initial: List<SavedLocation> = emptyList(),
) : SavedLocationRepository {
    private val saved = MutableStateFlow(initial)

    /** When set, [save] and [remove] fail with this error and change nothing. */
    var failWith: DataError? = null

    /** Replaces the observed flow, e.g. with one that throws. */
    var savedLocationsFlow: Flow<List<SavedLocation>> = saved

    val savedLocations: List<SavedLocation> get() = saved.value

    override fun observeSavedLocations(): Flow<List<SavedLocation>> = savedLocationsFlow

    override suspend fun save(location: Location): DataResult<Unit> {
        failWith?.let { return DataResult.Error(it) }
        saved.update { current ->
            if (current.any { it.location.id == location.id }) {
                current
            } else {
                current + SavedLocation(location, Instant.ofEpochSecond(current.size.toLong()))
            }
        }
        return DataResult.Success(Unit)
    }

    override suspend fun remove(locationId: Long): DataResult<Unit> {
        failWith?.let { return DataResult.Error(it) }
        saved.update { current -> current.filterNot { it.location.id == locationId } }
        return DataResult.Success(Unit)
    }
}
