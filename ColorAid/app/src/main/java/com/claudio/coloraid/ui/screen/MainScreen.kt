package com.claudio.coloraid.ui.screen

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.BoxWithConstraints
import com.claudio.coloraid.data.utils.ColorUtils
import com.claudio.coloraid.data.loader.loadBasicPalette
import com.claudio.coloraid.viewmodel.MainViewModel
import android.graphics.BitmapFactory
import java.io.File
import android.provider.MediaStore
import android.graphics.ImageDecoder
import androidx.core.content.FileProvider
import android.Manifest
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var crossPosition by remember { mutableStateOf<Offset?>(null) }
    var imageBoxSize by remember { mutableStateOf(IntSize.Zero) }
    var imageBoxOffset by remember { mutableStateOf(Offset.Zero) }
    var showDialog by remember { mutableStateOf(false) }

    val bitmap = viewModel.bitmap
    val selectedColor = viewModel.selectedColor
    val colorName = viewModel.selectedColorName

    val palette = remember { loadBasicPalette(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            val bmp: Bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    .copy(Bitmap.Config.ARGB_8888, false)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, false)
            }
            viewModel.updateBitmap(bmp)
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                val stream = context.contentResolver.openInputStream(uri)
                stream?.use {
                    val tempFile = File(context.cacheDir, "temp_photo.jpg")
                    tempFile.outputStream().use { output ->
                        it.copyTo(output)
                    }

                    val exif = ExifInterface(tempFile.absolutePath)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )

                    val originalBitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                    val rotatedBitmap = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
                        else -> originalBitmap
                    }

                    viewModel.updateBitmap(rotatedBitmap)
                }
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            photoUri?.let { takePictureLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Permissão da câmera negada.", Toast.LENGTH_SHORT).show()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalHeight = maxHeight
        val infoAreaHeight = totalHeight * 0.25f
        val imageAreaHeight = totalHeight - infoAreaHeight

        Column(modifier = Modifier.fillMaxSize()) {
            // Área da imagem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageAreaHeight)
                    .onGloballyPositioned { coordinates ->
                        imageBoxSize = coordinates.size
                        imageBoxOffset = coordinates.positionInRoot()
                    }
            ) {
                bitmap?.let { bmp ->
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

                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Fit,
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

                                        viewModel.detectColorAt(bmp, realX, realY)
                                    } else {
                                        viewModel.clearSelectedColor()
                                    }
                                }

                                awaitPointerEventScope {
                                    while (true) {
                                        val down = awaitFirstDown()
                                        crossPosition = down.position
                                        updateColorAt(down.position)

                                        drag(down.id) { change ->
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
            }

            // Área de informações da cor
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(infoAreaHeight)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showDialog = true }) {
                    Text("Select Image")
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Escolha uma opção") },
                        text = {
                            Column {
                                TextButton(onClick = {
                                    showDialog = false
                                    launcher.launch("image/*")
                                }) {
                                    Text("Selecionar da Galeria")
                                }

                                TextButton(onClick = {
                                    showDialog = false
                                    val photoFile = File.createTempFile("photo_", ".jpg", context.cacheDir).apply {
                                        createNewFile()
                                        deleteOnExit()
                                    }
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.provider",
                                        photoFile
                                    )
                                    photoUri = uri
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }) {
                                    Text("Tirar Foto com a Câmera")
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {}
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(selectedColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "RGB: (${(selectedColor.red * 255).toInt()}, ${(selectedColor.green * 255).toInt()}, ${(selectedColor.blue * 255).toInt()}) Nome: $colorName",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(angle) }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}