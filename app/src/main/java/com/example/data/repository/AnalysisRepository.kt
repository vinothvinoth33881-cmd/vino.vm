package com.example.data.repository

import com.example.data.local.dao.AnalysisDao
import com.example.data.local.entity.AnalysisEntity
import kotlinx.coroutines.flow.Flow

class AnalysisRepository(private val analysisDao: AnalysisDao) {

    fun observeAnalyses(userId: Long): Flow<List<AnalysisEntity>> {
        return analysisDao.observeAnalyses(userId)
    }

    fun observeRecentAnalyses(userId: Long, limit: Int = 5): Flow<List<AnalysisEntity>> {
        return analysisDao.observeRecentAnalyses(userId, limit)
    }

    suspend fun getAnalysisById(id: String): AnalysisEntity? {
        return analysisDao.getAnalysisById(id)
    }

    fun observeAnalysisById(id: String): Flow<AnalysisEntity?> {
        return analysisDao.observeAnalysisById(id)
    }

    suspend fun saveAnalysis(analysis: AnalysisEntity) {
        analysisDao.insertAnalysis(analysis)
    }

    suspend fun deleteAnalysis(id: String) {
        analysisDao.deleteAnalysisById(id)
    }

    fun observeTotalCount(userId: Long): Flow<Int> {
        return analysisDao.observeTotalCount(userId)
    }

    fun observeCountByPrediction(userId: Long, prediction: String): Flow<Int> {
        return analysisDao.observeCountByPrediction(userId, prediction)
    }
}
