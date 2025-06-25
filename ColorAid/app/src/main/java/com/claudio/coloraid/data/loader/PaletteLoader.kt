package com.claudio.coloraid.data.loader

import android.content.Context
import android.util.Log
import com.claudio.coloraid.data.utils.ColorEntry
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.IOException
import com.claudio.coloraid.R


fun loadBasicPalette(context: Context): List<ColorEntry> {
    val language = context.resources.configuration.locales[0].language
    val path = when (language) {
        "pt" -> "colors_pt.json"
        "es" -> "colors_es.json"
        else -> "colors_en.json" // default em inglês
    }

    return try {
        val inputStream = try {
            context.assets.open(path)
        } catch (e: IOException) {
            Log.e("PaletteLoader", context.getString(R.string.palette_file_not_found, path))
            throw IllegalStateException(context.getString(R.string.palette_file_missing_exception, path))
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

