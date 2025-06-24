package com.claudio.coloraid.domain.usecase

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import com.claudio.coloraid.data.utils.ColorUtils
import com.claudio.coloraid.data.utils.ColorEntry
import kotlin.math.min

class DetectColorUseCase(private val palette: List<ColorEntry>) {

    fun execute(bitmap: Bitmap, x: Int, y: Int): Pair<Color, String> {
        val pixel = bitmap.getPixel(x, y)
        val color = Color(pixel)

        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF

        val closest = ColorUtils.findClosestColor(r, g, b, palette)
        val name = closest?.name ?: "Unknown"

        return Pair(color, name)
    }
}
