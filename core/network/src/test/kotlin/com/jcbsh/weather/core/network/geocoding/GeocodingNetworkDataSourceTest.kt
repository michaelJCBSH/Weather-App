package com.jcbsh.weather.core.network.geocoding

import com.jcbsh.weather.core.network.di.NetworkModule
import com.jcbsh.weather.core.network.dto.GeocodingResultDto
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class GeocodingNetworkDataSourceTest {
    private val server = MockWebServer()
    private lateinit var dataSource: GeocodingNetworkDataSource

    @Before
    fun setUp() {
        server.start()
        val service = NetworkModule.createGeocodingService(
            baseUrl = server.url("/").toString(),
            json = NetworkModule.providesJson(),
            client = OkHttpClient(),
        )
        dataSource = RetrofitGeocodingNetworkDataSource(service)
    }

    @After
    fun tearDown() = server.close()

    @Test
    fun `sends name, count, language and format`() = runTest {
        server.enqueue(jsonResponse("geocoding/search_no_results.json"))

        dataSource.search(name = "Springfield", languageCode = "en")

        val url = server.takeRequest().url
        assertEquals("/v1/search", url.encodedPath)
        assertEquals("Springfield", url.queryParameter("name"))
        assertEquals("10", url.queryParameter("count"))
        assertEquals("en", url.queryParameter("language"))
        assertEquals("json", url.queryParameter("format"))
    }

    @Test
    fun `parses recorded results`() = runTest {
        server.enqueue(jsonResponse("geocoding/search_springfield.json"))

        val results = dataSource.search(name = "Springfield", languageCode = "en")

        assertEquals(3, results.size)
        assertEquals(
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
            results.first(),
        )
    }

    @Test
    fun `missing results field means no matches`() = runTest {
        server.enqueue(jsonResponse("geocoding/search_no_results.json"))

        assertTrue(dataSource.search(name = "zzqqxx", languageCode = "en").isEmpty())
    }

    @Test(expected = HttpException::class)
    fun `throws on HTTP error`() = runTest {
        server.enqueue(MockResponse.Builder().code(500).build())

        dataSource.search(name = "Springfield", languageCode = "en")
    }

    private fun jsonResponse(resource: String): MockResponse {
        val body = javaClass.classLoader!!.getResource(resource)!!.readText()
        return MockResponse.Builder()
            .code(200)
            .addHeader("Content-Type", "application/json")
            .body(body)
            .build()
    }
}
