package com.jcbsh.weather.core.domain.repository

import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.result.DataResult

/** Finds [Location]s by name through Open-Meteo geocoding. */
interface LocationSearchRepository {
    /**
     * @param query the place name typed by the user.
     * @param languageCode ISO-639 language for result names, e.g. "en".
     */
    suspend fun search(query: String, languageCode: String): DataResult<List<Location>>
}
