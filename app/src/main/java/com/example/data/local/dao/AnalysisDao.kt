package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analyses WHERE userId = :userId ORDER BY timestamp DESC")
    fun observeAnalyses(userId: Long): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analyses WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentAnalyses(userId: Long, limit: Int = 5): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analyses WHERE id = :id LIMIT 1")
    suspend fun getAnalysisById(id: String): AnalysisEntity?

    @Query("SELECT * FROM analyses WHERE id = :id LIMIT 1")
    fun observeAnalysisById(id: String): Flow<AnalysisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity)

    @Query("DELETE FROM analyses WHERE id = :id")
    suspend fun deleteAnalysisById(id: String)

    @Query("SELECT COUNT(*) FROM analyses WHERE userId = :userId")
    fun observeTotalCount(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyses WHERE userId = :userId AND finalPrediction = :prediction")
    fun observeCountByPrediction(userId: Long, prediction: String): Flow<Int>
}
