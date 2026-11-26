package com.jcbsh.weather.feature.locations.fake

import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.repository.LocationSearchRepository
import com.jcbsh.weather.core.domain.result.DataResult

class FakeLocationSearchRepository : LocationSearchRepository {
    /** Result returned for every query, unless [resultsByQuery] has an entry. */
    var result: DataResult<List<Location>> = DataResult.Success(emptyList())
    val resultsByQuery = mutableMapOf<String, DataResult<List<Location>>>()

    /** Every (query, languageCode) pair searched, in order. */
    val searches = mutableListOf<Pair<String, String>>()

    override suspend fun search(query: String, languageCode: String): DataResult<List<Location>> {
        searches += query to languageCode
        return resultsByQuery[query] ?: result
    }
}
