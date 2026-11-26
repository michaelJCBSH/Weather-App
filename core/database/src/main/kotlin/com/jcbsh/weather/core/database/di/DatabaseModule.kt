package com.jcbsh.weather.core.database.di

import android.content.Context
import androidx.room.Room
import com.jcbsh.weather.core.database.WeatherDatabase
import com.jcbsh.weather.core.database.dao.SavedLocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(context, WeatherDatabase::class.java, WeatherDatabase.Name).build()

    @Provides
    fun providesSavedLocationDao(database: WeatherDatabase): SavedLocationDao = database.savedLocationDao()
}
