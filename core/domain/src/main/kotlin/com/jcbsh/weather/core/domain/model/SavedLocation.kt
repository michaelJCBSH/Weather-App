package com.jcbsh.weather.core.domain.model

import java.time.Instant

/** A [Location] the user has chosen to keep in their list. */
data class SavedLocation(
    val location: Location,
    val savedAt: Instant,
)
