package com.jcbsh.weather.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** A Saved Location row. [id] is the Open-Meteo geocoding id, so a place can only be saved once. */
@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    @ColumnInfo(name = "country_code") val countryCode: String?,
    val latitude: Double,
    val longitude: Double,
    val timezone: String?,
    @ColumnInfo(name = "saved_at") val savedAtEpochMillis: Long,
)
