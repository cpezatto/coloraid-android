package com.claudio.coloraid.data.utils

import com.claudio.coloraid.data.utils.ColorEntry
import android.graphics.Bitmap
import android.graphics.Color

object ColorUtils {
    fun findClosestColor(r: Int, g: Int, b: Int, palette: List<ColorEntry>): ColorEntry? {
        if (palette.isEmpty()) return null

        val targetLab = ColorEntry.Companion.rgbToLab(r, g, b)
        return palette.minByOrNull { color ->
            deltaE(targetLab, color.lab)
        }
    }

    private fun deltaE(lab1: Triple<Double, Double, Double>, lab2: Triple<Double, Double, Double>): Double {
        val (l1, a1, b1) = lab1
        val (l2, a2, b2) = lab2

        return Math.sqrt(
            (l1 - l2) * (l1 - l2) +
                    (a1 - a2) * (a1 - a2) +
                    (b1 - b2) * (b1 - b2)
        )
    }

    fun detectColorAt(bitmap: Bitmap, x: Int, y: Int): Int {
        return bitmap.getPixel(x, y)
    }
}