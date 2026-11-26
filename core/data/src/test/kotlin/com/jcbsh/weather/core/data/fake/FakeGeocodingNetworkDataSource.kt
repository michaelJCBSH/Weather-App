package com.jcbsh.weather.core.data.fake

import com.jcbsh.weather.core.network.dto.GeocodingResultDto
import com.jcbsh.weather.core.network.geocoding.GeocodingNetworkDataSource

class FakeGeocodingNetworkDataSource : GeocodingNetworkDataSource {
    var results: List<GeocodingResultDto> = emptyList()

    /** When set, [search] throws this instead of returning [results]. */
    var failure: Throwable? = null

    val requests = mutableListOf<Pair<String, String>>()

    override suspend fun search(name: String, languageCode: String): List<GeocodingResultDto> {
        requests += name to languageCode
        failure?.let { throw it }
        return results
    }
}
