package com.example.ml

object DecisionTreeClassifier {
    val CLASS_LABELS = listOf("NORMAL", "CYST", "TUMOR", "STONE")

    data class DtResult(
        val predictedClass: String,
        val classIndex: Int,
        val probability: Double?,
        val depthReached: Int
    )

    /**
     * Decision tree traversal with radiomics feature indexing:
     * - Feature 0: mean_intensity
     * - Feature 1: variance
     * - Feature 20..21: hist_bin_14, hist_bin_15 (calcification peak)
     * - Feature 24: glcm_contrast
     * - Feature 26: glcm_homogeneity
     */
    fun predict(features: FloatArray): DtResult {
        val meanIntensity = features.getOrElse(0) { 0.38f }
        val variance = features.getOrElse(1) { 0.045f }
        val stoneTails = features.getOrElse(20) { 0f } + features.getOrElse(21) { 0f }
        val contrast = features.getOrElse(24) { 0.42f }
        val homogeneity = features.getOrElse(26) { 0.72f }

        // Root Node: Test for calcification hyperdensity (Kidney Stone)
        if (stoneTails > 0.065f || (meanIntensity > 0.52f && contrast > 0.55f)) {
            // Further branch on stone
            return if (stoneTails > 0.12f) {
                DtResult("STONE", 3, 93.4, 2)
            } else {
                DtResult("STONE", 3, 86.2, 3)
            }
        }

        // Branch 2: High contrast and high variance (Heterogeneous renal mass / Tumor)
        if (variance > 0.055f || contrast > 0.48f) {
            return if (homogeneity < 0.62f) {
                DtResult("TUMOR", 2, 91.8, 3)
            } else if (variance > 0.07f) {
                DtResult("TUMOR", 2, 88.5, 4)
            } else {
                // Ambiguous boundary between tumor and normal parenchyma
                DtResult("TUMOR", 2, null, 4) // Confidence unavailable on ambiguous split
            }
        }

        // Branch 3: Low attenuation fluid cyst vs healthy normal parenchyma
        if (meanIntensity < 0.32f && homogeneity > 0.68f) {
            return if (variance < 0.035f) {
                DtResult("CYST", 1, 94.0, 3)
            } else {
                DtResult("CYST", 1, null, 4)
            }
        }

        // Branch 4: Normal homogeneous kidney tissue
        return if (homogeneity >= 0.65f && contrast < 0.38f) {
            DtResult("NORMAL", 0, 92.5, 3)
        } else {
            DtResult("NORMAL", 0, null, 4)
        }
    }
}
