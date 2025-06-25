package com.claudio.coloraid.domain.usecase

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import com.claudio.coloraid.data.utils.ColorUtils
import com.claudio.coloraid.data.utils.ColorEntry
import kotlin.math.min
import com.claudio.coloraid.data.utils.detectColorAt


class DetectColorUseCase(private val palette: List<ColorEntry>) {

    fun execute(bitmap: Bitmap, x: Int, y: Int): Pair<Color, String> {
        val (r, g, b) = detectColorAt(bitmap, x, y)
        val color = Color(r / 255f, g / 255f, b / 255f)
        val name = ColorUtils.findClosestColor(r, g, b, palette)?.name ?: "Unknown"
        return color to name
    }
}
