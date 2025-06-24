package com.claudio.coloraid.data.loader

import android.content.Context
import android.util.Log
import com.claudio.coloraid.data.utils.ColorEntry
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.IOException

fun loadBasicPalette(context: Context): List<ColorEntry> {
    val path = "basic_colors.json"
    return try {
        val inputStream = try {
            context.assets.open(path)
        } catch (e: IOException) {
            Log.e("PaletteLoader", "Arquivo não encontrado em: /android_asset/$path")
            throw IllegalStateException("Arquivo JSON não encontrado em: /android_asset/$path")
        }

        val mapper = ObjectMapper().registerKotlinModule()
        val result = mapper.readValue(inputStream, object : TypeReference<List<ColorEntry>>() {})
        Log.i("PaletteLoader", "Paleta carregada com ${result.size} cores")

        if (result.isEmpty()) {
            Log.w("PaletteLoader", "A paleta foi carregada mas está vazia")
        }

        result
    } catch (e: Exception) {
        Log.e("PaletteLoader", "Erro ao carregar paleta de cores", e)
        emptyList()
    }
}

