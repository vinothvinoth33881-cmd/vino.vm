package com.example.ml

import android.graphics.Bitmap
import android.os.SystemClock

object KidneyMlPipeline {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }

    data class PipelineOutput(
        val originalWidth: Int,
        val originalHeight: Int,
        val features: FloatArray,
        val svmPrediction: String,
        val decisionTreePrediction: String,
        val finalPrediction: String,
        val isAgreement: Boolean,
        val confidence: Double?,
        val processingTimeSec: Double,
        val modelStatus: String,
        val explainabilityNote: String = "Explainability visualization is not available for this model configuration."
    )

    fun validateImage(bitmap: Bitmap?): ValidationResult {
        if (bitmap == null) {
            return ValidationResult.Error("Please upload a valid medical image.")
        }
        if (bitmap.width < 16 || bitmap.height < 16) {
            return ValidationResult.Error("Image dimensions are too small for diagnostic analysis.")
        }
        if (bitmap.byteCount == 0) {
            return ValidationResult.Error("Unable to process this image: image file appears empty or corrupted.")
        }
        return ValidationResult.Valid
    }

    fun executeAnalysis(
        bitmap: Bitmap,
        isDevMode: Boolean = false,
        devMockClass: String? = null
    ): PipelineOutput {
        val startTime = SystemClock.elapsedRealtime()

        if (isDevMode) {
            // Development Mode rule:
            // "When true: Allow development-only mock data.
            // Clearly display: 'Development Mode — Mock predictions are not medical predictions.'
            // Never hide mock mode from the user."
            val mockClass = devMockClass ?: "CYST"
            val processingTime = ((SystemClock.elapsedRealtime() - startTime) + 320) / 1000.0

            return PipelineOutput(
                originalWidth = bitmap.width,
                originalHeight = bitmap.height,
                features = FloatArray(FeatureExtractor.FEATURE_COUNT) { 0f },
                svmPrediction = mockClass,
                decisionTreePrediction = mockClass,
                finalPrediction = mockClass,
                isAgreement = true,
                confidence = null, // In mock mode, confidence is never fabricated!
                processingTimeSec = processingTime,
                modelStatus = "DEVELOPMENT_MODE",
                explainabilityNote = "Development Mode active. Explainability visualization is not available for this model configuration."
            )
        }

        // PRODUCTION TRAINED PIPELINE:
        // 1. Preprocessing (Resize 64x64, Grayscale, Normalization)
        val preprocessed = ImagePreprocessor.process(bitmap)

        // 2. Feature Extraction (Moments, Histogram, Sobel, GLCM)
        val features = FeatureExtractor.extract(preprocessed.resizedGrayscale)

        // 3. SVM Inference
        val svmResult = SVMClassifier.predict(features)

        // 4. Decision Tree Inference
        val dtResult = DecisionTreeClassifier.predict(features)

        // 5. Model Agreement / Disagreement Check
        val isAgreement = svmResult.predictedClass == dtResult.predictedClass
        val finalClass = if (isAgreement) {
            svmResult.predictedClass
        } else {
            "MODEL_DISAGREEMENT"
        }

        // 6. Confidence Calibration:
        // If agreement, take the average calibrated confidence if both are available,
        // or the reliable one. If disagreement or either is null, return null!
        val confidence: Double? = if (isAgreement) {
            when {
                svmResult.probability != null && dtResult.probability != null -> {
                    (svmResult.probability + dtResult.probability) / 2.0
                }
                svmResult.probability != null -> svmResult.probability
                dtResult.probability != null -> dtResult.probability
                else -> null // Strict rule: Display "Confidence not available" rather than inventing
            }
        } else {
            null // In disagreement, confidence cannot be reliably calculated
        }

        val elapsed = (SystemClock.elapsedRealtime() - startTime).coerceAtLeast(15)
        val processingTime = elapsed / 1000.0

        return PipelineOutput(
            originalWidth = preprocessed.originalWidth,
            originalHeight = preprocessed.originalHeight,
            features = features,
            svmPrediction = svmResult.predictedClass,
            decisionTreePrediction = dtResult.predictedClass,
            finalPrediction = finalClass,
            isAgreement = isAgreement,
            confidence = confidence?.let { Math.round(it * 10.0) / 10.0 },
            processingTimeSec = Math.round(processingTime * 100.0) / 100.0,
            modelStatus = "TRAINED",
            explainabilityNote = "Explainability visualization is not available for this model configuration."
        )
    }
}
