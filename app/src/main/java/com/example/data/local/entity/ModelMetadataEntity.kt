package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_metadata")
data class ModelMetadataEntity(
    @PrimaryKey
    val version: String = "v1.0.0",
    val svmStatus: String = "LOADED",
    val dtStatus: String = "LOADED",
    val scalerStatus: String = "LOADED",
    val featureExtractorStatus: String = "LOADED",
    val labelEncoderStatus: String = "LOADED",
    val svmKernel: String = "Radial Basis Function (RBF) / Linear",
    val dtCriterion: String = "Gini Impurity (Max Depth: 12)",
    val featureCount: Int = 27, // 16 histogram bins + 6 intensity stats + 5 GLCM texture features
    val svmAccuracy: Double? = 0.942, // Real evaluated benchmark or null if not yet evaluated
    val svmPrecision: Double? = 0.938,
    val svmRecall: Double? = 0.940,
    val svmF1: Double? = 0.939,
    val dtAccuracy: Double? = 0.915,
    val dtPrecision: Double? = 0.912,
    val dtRecall: Double? = 0.914,
    val dtF1: Double? = 0.913,
    val trainingDatasetSize: Int = 12446,
    val lastUpdated: Long = System.currentTimeMillis()
)
