package com.jcbsh.weather.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.jcbsh.weather.core.database.WeatherDatabase
import com.jcbsh.weather.core.database.entity.SavedLocationEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SavedLocationDaoTest {
    private lateinit var database: WeatherDatabase
    private lateinit var dao: SavedLocationDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.savedLocationDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun `observeAll returns rows oldest first`() = runTest {
        dao.insert(entity(id = 2, savedAt = 200))
        dao.insert(entity(id = 1, savedAt = 100))

        dao.observeAll().test {
            assertEquals(listOf(1L, 2L), awaitItem().map { it.id })
        }
    }

    @Test
    fun `inserting an existing id keeps the original row`() = runTest {
        dao.insert(entity(id = 1, savedAt = 100))
        dao.insert(entity(id = 1, savedAt = 999, name = "Changed"))

        dao.observeAll().test {
            assertEquals(listOf(entity(id = 1, savedAt = 100)), awaitItem())
        }
    }

    @Test
    fun `deleteById removes only that row`() = runTest {
        dao.insert(entity(id = 1, savedAt = 100))
        dao.insert(entity(id = 2, savedAt = 200))

        dao.deleteById(1)

        dao.observeAll().test {
            assertEquals(listOf(2L), awaitItem().map { it.id })
        }
    }

    private fun entity(id: Long, savedAt: Long, name: String = "Place $id") = SavedLocationEntity(
        id = id,
        name = name,
        region = "Region",
        country = "Country",
        countryCode = "CC",
        latitude = 1.0,
        longitude = 2.0,
        timezone = "UTC",
        savedAtEpochMillis = savedAt,
    )
}
