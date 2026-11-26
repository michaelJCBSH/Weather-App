package com.jcbsh.weather.core.data.repository

import app.cash.turbine.test
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.domain.result.DataError
import com.jcbsh.weather.core.domain.result.DataResult
import com.jcbsh.weather.core.testing.data.TestLocations
import com.jcbsh.weather.core.testing.fake.FakeSavedLocationDao
import com.jcbsh.weather.core.testing.fake.RecordingLogger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class DefaultSavedLocationRepositoryTest {
    private val now = Instant.parse("2026-11-26T01:00:00Z")
    private val dao = FakeSavedLocationDao()
    private val repository = DefaultSavedLocationRepository(
        dao = dao,
        clock = Clock.fixed(now, ZoneOffset.UTC),
        logger = RecordingLogger(),
    )

    @Test
    fun `save stores the location with the current time`() = runTest {
        repository.observeSavedLocations().test {
            assertEquals(emptyList<SavedLocation>(), awaitItem())

            assertEquals(DataResult.Success(Unit), repository.save(TestLocations.Sydney))

            assertEquals(listOf(SavedLocation(TestLocations.Sydney, now)), awaitItem())
        }
    }

    @Test
    fun `remove deletes the location`() = runTest {
        repository.save(TestLocations.Sydney)
        repository.save(TestLocations.Singapore)

        assertEquals(DataResult.Success(Unit), repository.remove(TestLocations.Sydney.id))

        repository.observeSavedLocations().test {
            assertEquals(listOf(TestLocations.Singapore), awaitItem().map { it.location })
        }
    }

    @Test
    fun `storage failure is returned as an error`() = runTest {
        dao.writeFailure = IllegalStateException("disk full")

        assertEquals(DataResult.Error(DataError.Unknown), repository.save(TestLocations.Sydney))
        assertEquals(DataResult.Error(DataError.Unknown), repository.remove(TestLocations.Sydney.id))
    }
}
