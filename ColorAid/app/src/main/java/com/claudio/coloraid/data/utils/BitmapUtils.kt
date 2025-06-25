package com.claudio.coloraid.data.utils

import android.graphics.Bitmap
import android.graphics.Matrix

fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(angle) }
    return Bitmap.createBitmap(
        source,
        0, 0,
        source.width,
        source.height,
        matrix,
        /* filter = */ true
    )
}