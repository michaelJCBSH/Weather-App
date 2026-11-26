package com.jcbsh.weather.di

import com.jcbsh.weather.BuildConfig
import com.jcbsh.weather.core.common.logging.Logger
import com.jcbsh.weather.core.common.logging.NoOpLogger
import com.jcbsh.weather.core.common.logging.TimberLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object LoggingModule {
    @Provides
    fun providesLogger(timberLogger: Provider<TimberLogger>): Logger =
        if (BuildConfig.DEBUG) timberLogger.get() else NoOpLogger
}
