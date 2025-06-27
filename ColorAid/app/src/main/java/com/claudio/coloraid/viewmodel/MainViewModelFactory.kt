package com.claudio.coloraid.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.claudio.coloraid.data.loader.loadPalette
import com.claudio.coloraid.domain.usecase.DetectColorUseCase
import com.claudio.coloraid.palette.PaletteType


class MainViewModelFactory(
    private val context: Context,
    private val paletteType: PaletteType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val palette = loadPalette(context, paletteType)
        val useCase = DetectColorUseCase(palette)
        return MainViewModel(useCase) as T
    }
}
