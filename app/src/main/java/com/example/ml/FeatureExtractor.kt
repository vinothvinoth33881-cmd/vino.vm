package com.example.ml

import kotlin.math.pow
import kotlin.math.sqrt

object FeatureExtractor {
    const val FEATURE_COUNT = 29

    val FEATURE_NAMES = listOf(
        "mean_intensity",
        "variance",
        "std_dev",
        "skewness",
        "kurtosis",
        "median",
        // 16-bin histogram
        "hist_bin_0", "hist_bin_1", "hist_bin_2", "hist_bin_3",
        "hist_bin_4", "hist_bin_5", "hist_bin_6", "hist_bin_7",
        "hist_bin_8", "hist_bin_9", "hist_bin_10", "hist_bin_11",
        "hist_bin_12", "hist_bin_13", "hist_bin_14", "hist_bin_15",
        // Sobel Edge features
        "sobel_mean_magnitude",
        "sobel_high_gradient_ratio",
        // GLCM radiomics texture descriptors
        "glcm_contrast",
        "glcm_dissimilarity",
        "glcm_homogeneity",
        "glcm_energy",
        "glcm_correlation"
    )

    /**
     * Extracts radiomics features from the 64x64 normalized grayscale pixel array.
     */
    fun extract(grid: Array<FloatArray>): FloatArray {
        val h = grid.size
        val w = grid[0].size
        val totalPixels = h * w
        val flat = FloatArray(totalPixels)
        var idx = 0
        var sum = 0.0

        for (y in 0 until h) {
            for (x in 0 until w) {
                val v = grid[y][x]
                flat[idx++] = v
                sum += v
            }
        }

        val mean = (sum / totalPixels).toFloat()

        // Variance, skewness, kurtosis
        var varSum = 0.0
        var skewSum = 0.0
        var kurtSum = 0.0

        for (v in flat) {
            val diff = (v - mean).toDouble()
            varSum += diff * diff
            skewSum += diff.pow(3)
            kurtSum += diff.pow(4)
        }

        val variance = (varSum / totalPixels).toFloat()
        val stdDev = sqrt(variance)
        val skewness = if (stdDev > 1e-6) (skewSum / (totalPixels * stdDev.toDouble().pow(3))).toFloat() else 0f
        val kurtosis = if (stdDev > 1e-6) (kurtSum / (totalPixels * stdDev.toDouble().pow(4))).toFloat() - 3.0f else 0f

        val sorted = flat.clone()
        sorted.sort()
        val median = sorted[totalPixels / 2]

        // 16-bin normalized histogram
        val hist = FloatArray(16)
        for (v in flat) {
            val bin = (v * 16.0f).toInt().coerceIn(0, 15)
            hist[bin] += 1.0f
        }
        for (b in 0 until 16) {
            hist[b] /= totalPixels.toFloat()
        }

        // Spatial Sobel edge gradients (3x3 kernel)
        var edgeSum = 0.0
        var highEdgeCount = 0
        val edgeEvaluatedPixels = (h - 2) * (w - 2)

        for (y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                // Sobel Gx
                val gx = (grid[y-1][x+1] + 2f * grid[y][x+1] + grid[y+1][x+1]) -
                         (grid[y-1][x-1] + 2f * grid[y][x-1] + grid[y+1][x-1])
                // Sobel Gy
                val gy = (grid[y+1][x-1] + 2f * grid[y+1][x] + grid[y+1][x+1]) -
                         (grid[y-1][x-1] + 2f * grid[y-1][x] + grid[y-1][x+1])

                val mag = sqrt((gx * gx + gy * gy).toDouble()).toFloat()
                edgeSum += mag
                if (mag > 0.35f) highEdgeCount++
            }
        }

        val meanEdgeMag = if (edgeEvaluatedPixels > 0) (edgeSum / edgeEvaluatedPixels).toFloat() else 0f
        val highEdgeRatio = if (edgeEvaluatedPixels > 0) highEdgeCount.toFloat() / edgeEvaluatedPixels else 0f

        // Simplified 8-level GLCM (horizontal offset dx=1, dy=0)
        val glcmLevels = 8
        val glcm = Array(glcmLevels) { FloatArray(glcmLevels) }
        var pairCount = 0

        for (y in 0 until h) {
            for (x in 0 until w - 1) {
                val i = (grid[y][x] * (glcmLevels - 1)).toInt().coerceIn(0, glcmLevels - 1)
                val j = (grid[y][x + 1] * (glcmLevels - 1)).toInt().coerceIn(0, glcmLevels - 1)
                glcm[i][j] += 1f
                glcm[j][i] += 1f // symmetric
                pairCount += 2
            }
        }

        if (pairCount > 0) {
            for (i in 0 until glcmLevels) {
                for (j in 0 until glcmLevels) {
                    glcm[i][j] /= pairCount.toFloat()
                }
            }
        }

        var contrast = 0f
        var dissimilarity = 0f
        var homogeneity = 0f
        var energy = 0f
        var meanI = 0f
        var meanJ = 0f

        for (i in 0 until glcmLevels) {
            for (j in 0 until glcmLevels) {
                val p = glcm[i][j]
                val diff = (i - j).toFloat()
                contrast += p * diff * diff
                dissimilarity += p * kotlin.math.abs(diff)
                homogeneity += p / (1f + diff * diff)
                energy += p * p
                meanI += i * p
                meanJ += j * p
            }
        }

        var varI = 0f
        var varJ = 0f
        for (i in 0 until glcmLevels) {
            for (j in 0 until glcmLevels) {
                val p = glcm[i][j]
                varI += p * (i - meanI) * (i - meanI)
                varJ += p * (j - meanJ) * (j - meanJ)
            }
        }

        var correlation = 0f
        val stdI = sqrt(varI)
        val stdJ = sqrt(varJ)
        if (stdI > 1e-5 && stdJ > 1e-5) {
            var cov = 0f
            for (i in 0 until glcmLevels) {
                for (j in 0 until glcmLevels) {
                    cov += glcm[i][j] * (i - meanI) * (j - meanJ)
                }
            }
            correlation = (cov / (stdI * stdJ)).coerceIn(-1f, 1f)
        }

        // Assemble 29 features
        val features = FloatArray(FEATURE_COUNT)
        features[0] = mean
        features[1] = variance
        features[2] = stdDev
        features[3] = skewness
        features[4] = kurtosis
        features[5] = median

        for (b in 0 until 16) {
            features[6 + b] = hist[b]
        }

        features[22] = meanEdgeMag
        features[23] = highEdgeRatio
        features[24] = contrast
        features[25] = dissimilarity
        features[26] = homogeneity
        features[27] = energy
        features[28] = correlation

        return features
    }
}
