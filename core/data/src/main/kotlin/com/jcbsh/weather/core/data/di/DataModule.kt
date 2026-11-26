package com.jcbsh.weather.core.data.di

import com.jcbsh.weather.core.data.repository.DefaultLocationSearchRepository
import com.jcbsh.weather.core.data.repository.DefaultSavedLocationRepository
import com.jcbsh.weather.core.domain.repository.LocationSearchRepository
import com.jcbsh.weather.core.domain.repository.SavedLocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun bindsLocationSearchRepository(impl: DefaultLocationSearchRepository): LocationSearchRepository

    @Binds
    fun bindsSavedLocationRepository(impl: DefaultSavedLocationRepository): SavedLocationRepository

    companion object {
        @Provides
        fun providesClock(): Clock = Clock.systemUTC()
    }
}
