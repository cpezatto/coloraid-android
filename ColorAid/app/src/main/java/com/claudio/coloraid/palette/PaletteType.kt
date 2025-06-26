package com.claudio.coloraid.palette

enum class PaletteType(val filePrefix: String, val hasTranslations: Boolean) {
    BASIC("basic_colors", true),
    STANDARD("standard_colors", true),
    WEB("web_colors", false),
    XKCD("xkcd_colors", true)
}