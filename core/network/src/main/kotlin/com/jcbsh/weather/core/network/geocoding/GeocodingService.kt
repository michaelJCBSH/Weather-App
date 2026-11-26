package com.jcbsh.weather.core.network.geocoding

import com.jcbsh.weather.core.network.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit API for https://geocoding-api.open-meteo.com/v1/search. */
interface GeocodingService {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int,
        @Query("language") language: String,
        @Query("format") format: String = "json",
    ): GeocodingResponseDto
}
