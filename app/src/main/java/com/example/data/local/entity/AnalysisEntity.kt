package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyses")
data class AnalysisEntity(
    @PrimaryKey
    val id: String,
    val userId: Long,
    val userEmail: String,
    val originalImageUri: String,
    val sampleIdentifier: String? = null,
    val imageName: String,
    val imageDimensions: String,
    val fileSizeFormatted: String,
    val finalPrediction: String, // NORMAL, CYST, TUMOR, STONE, or MODEL_DISAGREEMENT
    val svmPrediction: String,
    val decisionTreePrediction: String,
    val isAgreement: Boolean,
    val confidence: Double?, // null if not available per strict medical AI rule
    val processingTimeSec: Double,
    val modelStatus: String, // "TRAINED" or "DEVELOPMENT_MODE"
    val explainabilityNote: String = "Explainability visualization is not available for this model configuration.",
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
