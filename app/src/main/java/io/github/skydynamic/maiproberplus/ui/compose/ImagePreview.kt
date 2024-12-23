package io.github.skydynamic.maiproberplus.ui.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.Bitmap
import io.github.skydynamic.maiproberplus.R
import kotlin.math.max
import kotlin.math.min
import io.github.skydynamic.maiproberplus.Application.Companion.application

// TODO: 完善它，现在是能用就行
@Composable
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
fun ImagePreview(
    image: Bitmap,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.75f)),
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                    ) {
                        IconButton(
                            onClick = {
                                application.saveImageToGallery(
                                    image,
                                    "${System.currentTimeMillis()}.jpg"
                                )
                            }
                        ) {
                            Icon(painterResource(R.drawable.save_24px), null)
                        }

                        IconButton(
                            onClick = onDismiss
                        ) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                }

                var scale by remember { mutableFloatStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val maxWidth = constraints.maxWidth.toFloat()
                    val maxHeight = constraints.maxHeight.toFloat()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RectangleShape)
                    ) {
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale *= zoom
                                        scale = scale.coerceIn(1f, 4f)

                                        val maxX = (maxWidth * (scale - 1)) / 2
                                        val maxY = (maxHeight * (scale - 1)) / 2

                                        val newOffsetX = min(max(offset.x + pan.x, -maxX), maxX)
                                        val newOffsetY = min(max(offset.y + pan.y, -maxY), maxY)

                                        offset = Offset(newOffsetX, newOffsetY)
                                    }
                                    detectTapGestures(
                                        onTap = {
                                            onDismiss()
                                        },
                                        onDoubleTap = {
                                            scale = if (scale < 2f) {
                                                2f
                                            } else {
                                                1f
                                            }
                                            val imageWidth = maxWidth * scale
                                            val imageHeight = maxHeight * scale
                                            val centerX = (maxWidth - imageWidth) / 2
                                            val centerY = (maxHeight - imageHeight) / 2
                                            offset = Offset(centerX, centerY)
                                        }
                                    )
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()

                                        val scaledDragAmount = Offset(dragAmount.x * scale, dragAmount.y * scale)

                                        val maxX = (maxWidth * (scale - 1)) / 2
                                        val maxY = (maxHeight * (scale - 1)) / 2

                                        offset = Offset(
                                            x = min(max(offset.x + scaledDragAmount.x, -maxX), maxX),
                                            y = min(max(offset.y + scaledDragAmount.y, -maxY), maxY)
                                        )
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

