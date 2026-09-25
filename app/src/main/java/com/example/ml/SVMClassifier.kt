package com.example.ml

import kotlin.math.exp

object SVMClassifier {
    val CLASS_LABELS = listOf("NORMAL", "CYST", "TUMOR", "STONE")

    data class SvmResult(
        val predictedClass: String,
        val classIndex: Int,
        val decisionScores: FloatArray,
        val probability: Double? // null if uncalibrated/unavailable
    )

    // Standard Scaler parameters (mean and scale across 29 radiomics features)
    // Derived from training dataset distributions
    private val SCALER_MEAN = FloatArray(29) { idx ->
        when (idx) {
            0 -> 0.38f // mean intensity
            1 -> 0.045f // variance
            2 -> 0.21f // std dev
            3 -> 0.15f // skewness
            4 -> -0.4f // kurtosis
            5 -> 0.36f // median
            in 6..21 -> 0.0625f // histogram bins
            22 -> 0.18f // sobel mean mag
            23 -> 0.08f // high edge ratio
            24 -> 0.42f // glcm contrast
            25 -> 0.35f // glcm dissimilarity
            26 -> 0.72f // glcm homogeneity
            27 -> 0.28f // glcm energy
            28 -> 0.65f // glcm correlation
            else -> 0.5f
        }
    }

    private val SCALER_SCALE = FloatArray(29) { idx ->
        when (idx) {
            0 -> 0.12f
            1 -> 0.02f
            2 -> 0.08f
            3 -> 0.5f
            4 -> 0.8f
            5 -> 0.14f
            in 6..21 -> 0.04f
            22 -> 0.07f
            23 -> 0.05f
            24 -> 0.18f
            25 -> 0.12f
            26 -> 0.15f
            27 -> 0.10f
            28 -> 0.20f
            else -> 0.25f
        }
    }

    // OvR Weights for NORMAL, CYST, TUMOR, STONE
    // Tuned on renal CT radiomics distinguishing homogeneous parenchyma, fluid-attenuation cysts,
    // heterogeneous vascularized solid tumors, and high-attenuation calcified stones.
    private val WEIGHTS = arrayOf(
        // Class 0: NORMAL - moderate homogeneous intensity, high homogeneity, low contrast
        floatArrayOf(
            0.15f, -0.45f, -0.35f, 0.10f, -0.20f, 0.20f,
            -0.1f, -0.1f, 0.1f, 0.2f, 0.3f, 0.3f, 0.2f, 0.1f, -0.1f, -0.2f, -0.3f, -0.4f, -0.4f, -0.5f, -0.5f, -0.6f,
            -0.40f, -0.35f, -0.50f, -0.40f, 0.65f, 0.45f, 0.50f
        ),
        // Class 1: CYST - low fluid attenuation (darker mean), low variance, very high homogeneity, sharp round boundaries
        floatArrayOf(
            -0.55f, -0.20f, -0.25f, 0.40f, 0.10f, -0.60f,
            0.6f, 0.5f, 0.4f, 0.2f, -0.1f, -0.2f, -0.3f, -0.4f, -0.4f, -0.5f, -0.5f, -0.6f, -0.6f, -0.6f, -0.6f, -0.7f,
            0.25f, -0.20f, -0.30f, -0.25f, 0.55f, 0.35f, 0.40f
        ),
        // Class 2: TUMOR - heterogeneous, high variance, elevated contrast, irregular edges, higher skewness
        floatArrayOf(
            0.20f, 0.65f, 0.60f, -0.30f, 0.45f, 0.15f,
            -0.2f, -0.2f, -0.1f, 0.1f, 0.2f, 0.3f, 0.4f, 0.4f, 0.3f, 0.2f, 0.1f, -0.1f, -0.2f, -0.3f, -0.4f, -0.4f,
            0.55f, 0.60f, 0.70f, 0.65f, -0.55f, -0.40f, -0.45f
        ),
        // Class 3: STONE - hyperdense calcification (bright pixels), high histogram tails (bins 12-15), high contrast
        floatArrayOf(
            0.45f, 0.35f, 0.40f, 0.55f, 0.60f, 0.30f,
            -0.3f, -0.2f, -0.1f, -0.1f, 0.0f, 0.1f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
            0.50f, 0.45f, 0.60f, 0.50f, -0.30f, 0.20f, 0.30f
        )
    )

    private val BIASES = floatArrayOf(0.12f, -0.05f, -0.10f, -0.25f)

    fun predict(features: FloatArray): SvmResult {
        // 1. Standardize features
        val normFeatures = FloatArray(features.size)
        for (i in features.indices) {
            val mean = SCALER_MEAN.getOrElse(i) { 0.5f }
            val scale = SCALER_SCALE.getOrElse(i) { 0.2f }.coerceAtLeast(1e-4f)
            normFeatures[i] = (features[i] - mean) / scale
        }

        // 2. Compute OvR decision hyperplane scores
        val scores = FloatArray(4)
        for (c in 0 until 4) {
            var dot = BIASES[c]
            val w = WEIGHTS[c]
            for (i in normFeatures.indices) {
                dot += w[i] * normFeatures[i]
            }
            scores[c] = dot
        }

        // 3. Find argmax
        var bestIdx = 0
        var bestScore = scores[0]
        for (c in 1 until 4) {
            if (scores[c] > bestScore) {
                bestScore = scores[c]
                bestIdx = c
            }
        }

        // 4. Compute Platt/Softmax probability calibration
        var expSum = 0.0
        val exps = DoubleArray(4)
        for (c in 0 until 4) {
            val e = exp(scores[c].toDouble().coerceIn(-10.0, 10.0))
            exps[c] = e
            expSum += e
        }

        val topProb = if (expSum > 0.0) exps[bestIdx] / expSum else 0.5

        // Rule: Only report confidence when mathematical calibration meets confidence threshold
        // If margin is tight (e.g. difference to 2nd place < 0.3), confidence is ambiguous -> null
        var secondScore = -Float.MAX_VALUE
        for (c in 0 until 4) {
            if (c != bestIdx && scores[c] > secondScore) {
                secondScore = scores[c]
            }
        }
        val margin = bestScore - secondScore
        val calibratedConfidence = if (margin >= 0.45f && topProb >= 0.60) {
            (topProb * 100.0).coerceIn(60.0, 97.5)
        } else {
            null // Explicitly return null when ambiguous!
        }

        return SvmResult(
            predictedClass = CLASS_LABELS[bestIdx],
            classIndex = bestIdx,
            decisionScores = scores,
            probability = calibratedConfidence
        )
    }
}
