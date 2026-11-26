package com.jcbsh.weather.core.data.mapper

import com.jcbsh.weather.core.database.entity.SavedLocationEntity
import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.network.dto.GeocodingResultDto
import java.time.Instant

internal fun GeocodingResultDto.toLocation() = Location(
    id = id,
    name = name,
    region = admin1?.takeIf { it.isNotBlank() },
    country = country?.takeIf { it.isNotBlank() },
    countryCode = countryCode?.takeIf { it.isNotBlank() },
    latitude = latitude,
    longitude = longitude,
    timezone = timezone?.takeIf { it.isNotBlank() },
)

internal fun Location.toEntity(savedAt: Instant) = SavedLocationEntity(
    id = id,
    name = name,
    region = region,
    country = country,
    countryCode = countryCode,
    latitude = latitude,
    longitude = longitude,
    timezone = timezone,
    savedAtEpochMillis = savedAt.toEpochMilli(),
)

internal fun SavedLocationEntity.toSavedLocation() = SavedLocation(
    location = Location(
        id = id,
        name = name,
        region = region,
        country = country,
        countryCode = countryCode,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
    ),
    savedAt = Instant.ofEpochMilli(savedAtEpochMillis),
)
