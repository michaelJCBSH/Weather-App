package com.jcbsh.weather.core.data.repository

import com.jcbsh.weather.core.common.logging.Logger
import com.jcbsh.weather.core.data.mapper.toEntity
import com.jcbsh.weather.core.data.mapper.toSavedLocation
import com.jcbsh.weather.core.database.dao.SavedLocationDao
import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.domain.repository.SavedLocationRepository
import com.jcbsh.weather.core.domain.result.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import javax.inject.Inject

internal class DefaultSavedLocationRepository @Inject constructor(
    private val dao: SavedLocationDao,
    private val clock: Clock,
    private val logger: Logger,
) : SavedLocationRepository {
    override fun observeSavedLocations(): Flow<List<SavedLocation>> =
        dao.observeAll().map { entities -> entities.map { it.toSavedLocation() } }

    override suspend fun save(location: Location): DataResult<Unit> =
        logger.runCatchingData("Save location") {
            dao.insert(location.toEntity(savedAt = clock.instant()))
        }

    override suspend fun remove(locationId: Long): DataResult<Unit> =
        logger.runCatchingData("Remove location") {
            dao.deleteById(locationId)
        }
}
