package com.jcbsh.weather.core.network.geocoding

import com.jcbsh.weather.core.network.dto.GeocodingResultDto
import javax.inject.Inject

/** Looks up places by name. Throws on network or HTTP failure. */
interface GeocodingNetworkDataSource {
    suspend fun search(name: String, languageCode: String): List<GeocodingResultDto>
}

internal class RetrofitGeocodingNetworkDataSource @Inject constructor(
    private val service: GeocodingService,
) : GeocodingNetworkDataSource {
    override suspend fun search(name: String, languageCode: String): List<GeocodingResultDto> =
        service.search(name = name, count = MaxResults, language = languageCode).results

    private companion object {
        const val MaxResults = 10
    }
}
