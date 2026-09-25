package com.example.data.repository

import com.example.data.local.dao.ModelMetadataDao
import com.example.data.local.entity.ModelMetadataEntity
import kotlinx.coroutines.flow.Flow

class ModelRepository(private val modelMetadataDao: ModelMetadataDao) {

    fun observeMetadata(): Flow<ModelMetadataEntity?> {
        return modelMetadataDao.observeLatestMetadata()
    }

    suspend fun getMetadata(): ModelMetadataEntity? {
        return modelMetadataDao.getLatestMetadata()
    }

    suspend fun saveMetadata(metadata: ModelMetadataEntity) {
        modelMetadataDao.insertOrUpdate(metadata)
    }
}
