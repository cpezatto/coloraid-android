package com.claudio.coloraid.data.loader

import android.content.Context
import android.util.Log
import com.claudio.coloraid.R
import com.claudio.coloraid.data.utils.ColorEntry
import com.claudio.coloraid.palette.PaletteType
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.IOException
import java.util.Locale

fun loadPalette(
    context: Context,
    paletteType: PaletteType,
    locale: Locale = Locale.getDefault()
): List<ColorEntry> {
    val language = if (paletteType.hasTranslations) locale.language else "en"
    val fileName = "palettes/${paletteType.filePrefix}_$language.json"

    return try {
        val inputStream = try {
            context.assets.open(fileName)
        } catch (e: IOException) {
            Log.e("PaletteLoader", context.getString(R.string.palette_file_not_found, fileName))
            throw IllegalStateException(context.getString(R.string.palette_file_missing_exception, fileName))
        }

        val mapper = ObjectMapper().registerKotlinModule()
        val result = mapper.readValue(inputStream, object : TypeReference<List<ColorEntry>>() {})
        Log.i("PaletteLoader", context.getString(R.string.palette_loaded_successfully, result.size))

        if (result.isEmpty()) {
            Log.w("PaletteLoader", context.getString(R.string.palette_loaded_but_empty))
        }

        result
    } catch (e: Exception) {
        Log.e("PaletteLoader", context.getString(R.string.palette_loading_error), e)
        emptyList()
    }
}
