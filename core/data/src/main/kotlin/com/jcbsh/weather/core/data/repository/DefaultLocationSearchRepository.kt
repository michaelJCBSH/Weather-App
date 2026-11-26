package com.jcbsh.weather.core.data.repository

import com.jcbsh.weather.core.common.dispatcher.IoDispatcher
import com.jcbsh.weather.core.common.logging.Logger
import com.jcbsh.weather.core.data.mapper.toLocation
import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.repository.LocationSearchRepository
import com.jcbsh.weather.core.domain.result.DataResult
import com.jcbsh.weather.core.network.geocoding.GeocodingNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DefaultLocationSearchRepository @Inject constructor(
    private val dataSource: GeocodingNetworkDataSource,
    private val logger: Logger,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : LocationSearchRepository {
    override suspend fun search(query: String, languageCode: String): DataResult<List<Location>> =
        logger.runCatchingData("Location search") {
            withContext(ioDispatcher) {
                dataSource.search(name = query.trim(), languageCode = languageCode).map { it.toLocation() }
            }
        }
}
