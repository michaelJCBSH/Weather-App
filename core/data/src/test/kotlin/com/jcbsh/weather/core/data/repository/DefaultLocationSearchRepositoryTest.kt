package com.jcbsh.weather.core.data.repository

import com.jcbsh.weather.core.data.fake.FakeGeocodingNetworkDataSource
import com.jcbsh.weather.core.data.fake.RecordingLogger
import com.jcbsh.weather.core.data.testdata.TestLocations
import com.jcbsh.weather.core.domain.result.DataError
import com.jcbsh.weather.core.domain.result.DataResult
import com.jcbsh.weather.core.network.dto.GeocodingResultDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DefaultLocationSearchRepositoryTest {
    private val dataSource = FakeGeocodingNetworkDataSource()
    private val logger = RecordingLogger()

    @Test
    fun `maps results and trims the query`() = runTest {
        dataSource.results = listOf(
            GeocodingResultDto(
                id = 4409896,
                name = "Springfield",
                latitude = 37.21533,
                longitude = -93.29824,
                countryCode = "US",
                country = "United States",
                admin1 = "Missouri",
                timezone = "America/Chicago",
            ),
        )
        val repository = DefaultLocationSearchRepository(dataSource, logger, StandardTestDispatcher(testScheduler))

        val result = repository.search(query = "  Springfield ", languageCode = "en")

        assertEquals(DataResult.Success(listOf(TestLocations.SpringfieldMissouri)), result)
        assertEquals(listOf("Springfield" to "en"), dataSource.requests)
    }

    @Test
    fun `IO failure is a network error`() = runTest {
        dataSource.failure = IOException("offline")
        val repository = DefaultLocationSearchRepository(dataSource, logger, StandardTestDispatcher(testScheduler))

        assertEquals(DataResult.Error(DataError.Network), repository.search("Sydney", "en"))
        assertEquals(listOf("Location search failed"), logger.messages)
    }

    @Test
    fun `HTTP failure is a server error`() = runTest {
        dataSource.failure = HttpException(Response.error<Any>(500, "".toResponseBody()))
        val repository = DefaultLocationSearchRepository(dataSource, logger, StandardTestDispatcher(testScheduler))

        assertEquals(DataResult.Error(DataError.Server), repository.search("Sydney", "en"))
    }

    @Test(expected = CancellationException::class)
    fun `cancellation is rethrown`() = runTest {
        dataSource.failure = CancellationException("cancelled")
        val repository = DefaultLocationSearchRepository(dataSource, logger, StandardTestDispatcher(testScheduler))

        repository.search("Sydney", "en")
    }
}
