package com.jcbsh.weather.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jcbsh.weather.core.database.entity.SavedLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {
    /** All Saved Locations, oldest first. */
    @Query("SELECT * FROM saved_locations ORDER BY saved_at ASC, id ASC")
    fun observeAll(): Flow<List<SavedLocationEntity>>

    /** Inserts [entity]; does nothing if a row with the same id exists. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: SavedLocationEntity)

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteById(id: Long)
}
