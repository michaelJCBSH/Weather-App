package com.jcbsh.weather.core.network.di

import com.jcbsh.weather.core.network.geocoding.GeocodingNetworkDataSource
import com.jcbsh.weather.core.network.geocoding.GeocodingService
import com.jcbsh.weather.core.network.geocoding.RetrofitGeocodingNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val GeocodingBaseUrl = "https://geocoding-api.open-meteo.com/"

    @Provides
    @Singleton
    fun providesJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient()

    @Provides
    @Singleton
    fun providesGeocodingService(json: Json, client: OkHttpClient): GeocodingService =
        createGeocodingService(GeocodingBaseUrl, json, client)

    /** Builds the [GeocodingService]; tests point [baseUrl] at a MockWebServer. */
    fun createGeocodingService(baseUrl: String, json: Json, client: OkHttpClient): GeocodingService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeocodingService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkBindingsModule {
    @Binds
    fun bindsGeocodingNetworkDataSource(impl: RetrofitGeocodingNetworkDataSource): GeocodingNetworkDataSource
}
