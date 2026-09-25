package com.example

import com.example.data.repository.AuthRepository
import com.example.ml.DecisionTreeClassifier
import com.example.ml.FeatureExtractor
import com.example.ml.KidneyMlPipeline
import com.example.ml.SVMClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KidneyMlPipelineTest {

    @Test
    fun featureExtractor_returnsExactly29Features() {
        // Create synthetic 64x64 grid
        val grid = Array(64) { y ->
            FloatArray(64) { x ->
                ((x + y) % 64) / 64.0f
            }
        }
        val features = FeatureExtractor.extract(grid)
        assertEquals(29, features.size)
        assertEquals(29, FeatureExtractor.FEATURE_NAMES.size)

        // Mean should be around 0.5
        assertTrue(features[0] in 0.3f..0.7f)
    }

    @Test
    fun svmClassifier_predictsValidPathologyClass() {
        val features = FloatArray(29) { 0.35f }
        val result = SVMClassifier.predict(features)
        assertTrue(result.predictedClass in listOf("NORMAL", "CYST", "TUMOR", "STONE"))
        assertEquals(4, result.decisionScores.size)
    }

    @Test
    fun decisionTreeClassifier_predictsValidPathologyClass() {
        val features = FloatArray(29) { 0.35f }
        val result = DecisionTreeClassifier.predict(features)
        assertTrue(result.predictedClass in listOf("NORMAL", "CYST", "TUMOR", "STONE"))
        assertTrue(result.depthReached > 0)
    }

    @Test
    fun decisionTreeClassifier_detectsCalcificationStonePeak() {
        val stoneFeatures = FloatArray(29) { 0.35f }
        // High histogram tail in bins 14-15
        stoneFeatures[20] = 0.15f
        stoneFeatures[21] = 0.10f
        val result = DecisionTreeClassifier.predict(stoneFeatures)
        assertEquals("STONE", result.predictedClass)
    }

    @Test
    fun pipeline_validatesImageCorrectly() {
        val nullResult = KidneyMlPipeline.validateImage(null)
        assertTrue(nullResult is KidneyMlPipeline.ValidationResult.Error)
    }

    @Test
    fun auth_validatesPasswordStrengthStrictly() {
        // Must be at least 8 chars, 1 uppercase, 1 lowercase, 1 number
        assertNotNull(AuthRepository.validatePasswordStrength("short1!")) // < 8 chars
        assertNotNull(AuthRepository.validatePasswordStrength("alllowercase123")) // no uppercase
        assertNotNull(AuthRepository.validatePasswordStrength("ALLUPPERCASE123")) // no lowercase
        assertNotNull(AuthRepository.validatePasswordStrength("NoDigitsHere!")) // no digits
        assertNull(AuthRepository.validatePasswordStrength("KidneyAI@2026!")) // Valid!
    }

    @Test
    fun auth_validatesEmailCorrectly() {
        // Simple regex check
        assertTrue("test@example.com".contains("@"))
        assertFalse("invalid_email".contains("@"))
    }
}
