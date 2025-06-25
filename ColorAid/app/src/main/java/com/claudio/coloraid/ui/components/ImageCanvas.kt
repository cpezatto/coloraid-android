package com.claudio.coloraid.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.input.pointer.consumePositionChange

@Composable
fun ImageCanvas(
    bitmap: ImageBitmap,
    onColorDetected: (x: Int, y: Int) -> Unit,
    onClearSelection: () -> Unit
) {
    var crossPosition by remember { mutableStateOf<Offset?>(null) }
    var imageBoxSize by remember { mutableStateOf(IntSize.Zero) }
    var imageBoxOffset by remember { mutableStateOf(Offset.Zero) }

    val bmp = bitmap

    val bitmapRatio = bmp.width.toFloat() / bmp.height.toFloat()
    val boxRatio = imageBoxSize.width.toFloat() / imageBoxSize.height.toFloat()

    val renderedWidth: Float
    val renderedHeight: Float
    val leftOffset: Float
    val topOffset: Float

    if (bitmapRatio > boxRatio) {
        renderedWidth = imageBoxSize.width.toFloat()
        renderedHeight = renderedWidth / bitmapRatio
        topOffset = (imageBoxSize.height - renderedHeight) / 2f
        leftOffset = 0f
    } else {
        renderedHeight = imageBoxSize.height.toFloat()
        renderedWidth = renderedHeight * bitmapRatio
        leftOffset = (imageBoxSize.width - renderedWidth) / 2f
        topOffset = 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                imageBoxSize = coordinates.size
                imageBoxOffset = coordinates.positionInRoot()
            }
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = "Selected Image",
            modifier = Modifier.fillMaxSize()
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(bmp, imageBoxSize) {
                fun updateColorAt(position: Offset) {
                    val localX = position.x - imageBoxOffset.x - leftOffset
                    val localY = position.y - imageBoxOffset.y - topOffset

                    if (localX in 0f..renderedWidth && localY in 0f..renderedHeight) {
                        val scaleX = bmp.width / renderedWidth
                        val scaleY = bmp.height / renderedHeight

                        val realX = (localX * scaleX).toInt().coerceIn(0, bmp.width - 1)
                        val realY = (localY * scaleY).toInt().coerceIn(0, bmp.height - 1)

                        onColorDetected(realX, realY)
                    } else {
                        onClearSelection()
                    }
                }

                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue

                        if (change.pressed) {
                            crossPosition = change.position
                            updateColorAt(change.position)
                            change.consume()
                        }
                    }
                }
            }
        )

        crossPosition?.let { pos ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = Color.Red,
                    start = Offset(x = pos.x, y = 0f),
                    end = Offset(x = pos.x, y = size.height),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Red,
                    start = Offset(x = 0f, y = pos.y),
                    end = Offset(x = size.width, y = pos.y),
                    strokeWidth = 2f
                )
            }
        }
    }
}
