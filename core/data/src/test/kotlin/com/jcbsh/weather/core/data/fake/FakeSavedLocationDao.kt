package com.jcbsh.weather.core.data.fake

import com.jcbsh.weather.core.database.dao.SavedLocationDao
import com.jcbsh.weather.core.database.entity.SavedLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** In-memory [SavedLocationDao] that mirrors the real query's ordering and conflict rules. */
class FakeSavedLocationDao : SavedLocationDao {
    private val rows = MutableStateFlow<Map<Long, SavedLocationEntity>>(emptyMap())

    /** When set, writes throw this. */
    var writeFailure: Exception? = null

    override fun observeAll(): Flow<List<SavedLocationEntity>> =
        rows.map { it.values.sortedWith(compareBy({ row -> row.savedAtEpochMillis }, { row -> row.id })) }

    override suspend fun insert(entity: SavedLocationEntity) {
        writeFailure?.let { throw it }
        rows.update { current -> if (entity.id in current) current else current + (entity.id to entity) }
    }

    override suspend fun deleteById(id: Long) {
        writeFailure?.let { throw it }
        rows.update { it - id }
    }
}
