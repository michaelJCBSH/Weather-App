package com.jcbsh.weather.core.data.testdata

import com.jcbsh.weather.core.domain.model.Location

/** Sample [Location]s for tests and snapshots. */
object TestLocations {
    val SpringfieldMissouri = Location(
        id = 4409896,
        name = "Springfield",
        region = "Missouri",
        country = "United States",
        countryCode = "US",
        latitude = 37.21533,
        longitude = -93.29824,
        timezone = "America/Chicago",
    )

    val Sydney = Location(
        id = 2147714,
        name = "Sydney",
        region = "New South Wales",
        country = "Australia",
        countryCode = "AU",
        latitude = -33.86785,
        longitude = 151.20732,
        timezone = "Australia/Sydney",
    )

    /** A place with no region, as some geocoding results have. */
    val Singapore = Location(
        id = 1880252,
        name = "Singapore",
        region = null,
        country = "Singapore",
        countryCode = "SG",
        latitude = 1.28967,
        longitude = 103.85007,
        timezone = "Asia/Singapore",
    )
}
