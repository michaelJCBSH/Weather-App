package com.jcbsh.weather.core.data.mapper

import com.jcbsh.weather.core.data.testdata.TestLocations
import com.jcbsh.weather.core.database.entity.SavedLocationEntity
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.network.dto.GeocodingResultDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class LocationMappersTest {
    @Test
    fun `geocoding result maps to Location`() {
        val dto = GeocodingResultDto(
            id = 4409896,
            name = "Springfield",
            latitude = 37.21533,
            longitude = -93.29824,
            countryCode = "US",
            country = "United States",
            admin1 = "Missouri",
            timezone = "America/Chicago",
        )

        assertEquals(TestLocations.SpringfieldMissouri, dto.toLocation())
    }

    @Test
    fun `blank optional geocoding fields become null`() {
        val dto = GeocodingResultDto(
            id = 1,
            name = "Somewhere",
            latitude = 0.0,
            longitude = 0.0,
            countryCode = "",
            country = " ",
            admin1 = "",
            timezone = null,
        )

        val location = dto.toLocation()

        assertNull(location.region)
        assertNull(location.country)
        assertNull(location.countryCode)
        assertNull(location.timezone)
    }

    @Test
    fun `Location round-trips through the entity`() {
        val savedAt = Instant.parse("2026-11-26T01:00:00Z")

        val entity = TestLocations.Singapore.toEntity(savedAt)

        assertEquals(savedAt.toEpochMilli(), entity.savedAtEpochMillis)
        assertEquals(SavedLocation(TestLocations.Singapore, savedAt), entity.toSavedLocation())
    }

    @Test
    fun `entity columns map to Location fields`() {
        val entity = SavedLocationEntity(
            id = 7,
            name = "Name",
            region = "Region",
            country = "Country",
            countryCode = "CC",
            latitude = 1.5,
            longitude = 2.5,
            timezone = "UTC",
            savedAtEpochMillis = 42,
        )

        val location = entity.toSavedLocation().location

        assertEquals(7L, location.id)
        assertEquals("Region", location.region)
        assertEquals("CC", location.countryCode)
        assertEquals(2.5, location.longitude, 0.0)
    }
}
