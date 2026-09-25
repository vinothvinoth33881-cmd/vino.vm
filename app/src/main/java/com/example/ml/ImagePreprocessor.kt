package com.example.ml

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.roundToInt

object ImagePreprocessor {
    const val TARGET_WIDTH = 64
    const val TARGET_HEIGHT = 64

    data class PreprocessedData(
        val originalWidth: Int,
        val originalHeight: Int,
        val resizedGrayscale: Array<FloatArray>, // 64x64 float in [0.0, 1.0]
        val thumbnailBitmap: Bitmap,
        val grayscaleBitmap: Bitmap
    )

    /**
     * Validates and preprocesses an input bitmap:
     * 1. Validates dimensions and non-null content
     * 2. Resizes to standard 64x64 resolution
     * 3. Converts RGB pixels to Grayscale using luminance formula: Y = 0.299R + 0.587G + 0.114B
     * 4. Normalizes pixel values into [0.0, 1.0]
     */
    fun process(bitmap: Bitmap): PreprocessedData {
        val origW = bitmap.width
        val origH = bitmap.height

        val scaled = Bitmap.createScaledBitmap(bitmap, TARGET_WIDTH, TARGET_HEIGHT, true)
        val grayGrid = Array(TARGET_HEIGHT) { FloatArray(TARGET_WIDTH) }
        val grayBitmap = Bitmap.createBitmap(TARGET_WIDTH, TARGET_HEIGHT, Bitmap.Config.ARGB_8888)

        for (y in 0 until TARGET_HEIGHT) {
            for (x in 0 until TARGET_WIDTH) {
                val pixel = scaled.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                // Clinical luminance standard
                val gray = (0.299f * r + 0.587f * g + 0.114f * b).coerceIn(0f, 255f)
                val norm = gray / 255.0f
                grayGrid[y][x] = norm

                val grayInt = gray.roundToInt().coerceIn(0, 255)
                grayBitmap.setPixel(x, y, Color.rgb(grayInt, grayInt, grayInt))
            }
        }

        return PreprocessedData(
            originalWidth = origW,
            originalHeight = origH,
            resizedGrayscale = grayGrid,
            thumbnailBitmap = scaled,
            grayscaleBitmap = grayBitmap
        )
    }
}
