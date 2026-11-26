package com.jcbsh.weather.core.domain.model

/**
 * A named place with a latitude and longitude, as returned by geocoding search.
 *
 * @property id Open-Meteo geocoding id; stable across searches.
 * @property region First-level administrative area (state, province), when known.
 * @property timezone IANA timezone id, when known.
 */
data class Location(
    val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val countryCode: String?,
    val latitude: Double,
    val longitude: Double,
    val timezone: String?,
)
