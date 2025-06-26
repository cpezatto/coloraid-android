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
import com.claudio.coloraid.data.loader.loadPalette
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
import com.claudio.coloraid.data.utils.rotateBitmap
import com.claudio.coloraid.ui.components.ImageCanvas
import androidx.compose.ui.res.stringResource
import com.claudio.coloraid.R

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val bitmap = viewModel.bitmap
    val selectedColor = viewModel.selectedColor
    val colorName = viewModel.selectedColorName

    val palette = remember { loadPalette(context, "standard") }

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
            Toast.makeText(context, context.getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalHeight = maxHeight
        val infoAreaHeight = totalHeight * 0.25f
        val imageAreaHeight = totalHeight - infoAreaHeight
        val red = (selectedColor.red * 255).toInt()
        val green = (selectedColor.green * 255).toInt()
        val blue = (selectedColor.blue * 255).toInt()

        Column(modifier = Modifier.fillMaxSize()) {
            // Área da imagem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageAreaHeight)
            ) {
                bitmap?.let { bmp ->

                    ImageCanvas(
                        bitmap = bmp.asImageBitmap(),
                        onColorDetected = { x, y -> viewModel.detectColorAt(bmp, x, y)},
                        onClearSelection = { viewModel.clearSelectedColor() }
                    )
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
                    Text(stringResource(R.string.select_image))
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(stringResource(R.string.select_image_title)) },
                        text = {
                            Column {
                                TextButton(onClick = {
                                    showDialog = false
                                    launcher.launch("image/*")
                                }) {
                                    Text(stringResource(R.string.gallery_option))
                                }

                                TextButton(onClick = {
                                    showDialog = false
                                    photoUri = viewModel.createPhotoUri(context)
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }) {
                                    Text(stringResource(R.string.camera_option))
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
                    text = stringResource(R.string.color_rgb_name, red, green, blue, colorName),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
