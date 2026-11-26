package com.jcbsh.weather.feature.locations.presentation

import com.jcbsh.weather.core.domain.model.Location

/** "Region, Country" for a list row's second line, skipping missing parts; null when both are missing. */
internal fun Location.regionAndCountry(): String? =
    listOfNotNull(region, country).joinToString(separator = ", ").ifEmpty { null }
