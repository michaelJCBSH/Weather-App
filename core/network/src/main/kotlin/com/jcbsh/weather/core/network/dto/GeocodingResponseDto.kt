package com.jcbsh.weather.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Open-Meteo geocoding `/v1/search` response. `results` is absent when nothing matches. */
@Serializable
data class GeocodingResponseDto(
    val results: List<GeocodingResultDto> = emptyList(),
)

@Serializable
data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("country_code") val countryCode: String? = null,
    val country: String? = null,
    val admin1: String? = null,
    val timezone: String? = null,
)
