package com.claudio.coloraid.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import com.claudio.coloraid.domain.usecase.DetectColorUseCase
import com.claudio.coloraid.data.utils.ColorEntry

class MainViewModel(
    private val detectColorUseCase: DetectColorUseCase
) : ViewModel() {

    var bitmap by mutableStateOf<Bitmap?>(null)
        private set

    var selectedColor by mutableStateOf(Color.Transparent)
        private set

    var selectedColorName by mutableStateOf("")
        private set

    var touchX by mutableStateOf(0f)
        private set

    var touchY by mutableStateOf(0f)
        private set

    fun updateBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap
    }

    fun detectColorAt(bitmap: Bitmap, x: Int, y: Int) {
        val (color, name) = detectColorUseCase.execute(bitmap, x, y)
        selectedColor = color
        selectedColorName = name
        touchX = x.toFloat()
        touchY = y.toFloat()
    }

    fun clearSelectedColor() {
        selectedColor = Color.Transparent
        selectedColorName = ""
        touchX = 0f
        touchY = 0f
    }
}
