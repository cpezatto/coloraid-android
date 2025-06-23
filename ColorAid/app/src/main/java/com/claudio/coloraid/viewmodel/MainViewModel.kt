package com.claudio.coloraid.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color

class MainViewModel : ViewModel() {

    // Bitmap da imagem carregada
    var bitmap by mutableStateOf<Bitmap?>(null)
        private set

    // Cor detectada
    var selectedColor by mutableStateOf(Color.Transparent)
        private set

    // Nome da cor (ex: "Red", "Sky Blue")
    var selectedColorName by mutableStateOf("")
        private set

    // Coordenadas do toque
    var touchX by mutableStateOf(0f)
        private set
    var touchY by mutableStateOf(0f)
        private set

    // Atualiza o bitmap com uma nova imagem
    fun updateBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap
    }

    // Atualiza a cor detectada e a posição
    fun updateSelectedColor(color: Color, name: String, x: Float, y: Float) {
        selectedColor = color
        selectedColorName = name
        touchX = x
        touchY = y
    }
}
