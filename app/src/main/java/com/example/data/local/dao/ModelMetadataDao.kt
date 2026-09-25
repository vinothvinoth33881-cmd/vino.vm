package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ModelMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelMetadataDao {
    @Query("SELECT * FROM model_metadata ORDER BY lastUpdated DESC LIMIT 1")
    fun observeLatestMetadata(): Flow<ModelMetadataEntity?>

    @Query("SELECT * FROM model_metadata ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestMetadata(): ModelMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(metadata: ModelMetadataEntity)
}
