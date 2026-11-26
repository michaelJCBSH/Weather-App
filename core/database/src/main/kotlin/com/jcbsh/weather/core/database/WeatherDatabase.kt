package com.jcbsh.weather.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jcbsh.weather.core.database.dao.SavedLocationDao
import com.jcbsh.weather.core.database.entity.SavedLocationEntity

@Database(
    entities = [SavedLocationEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun savedLocationDao(): SavedLocationDao

    companion object {
        const val Name = "weather.db"
    }
}
