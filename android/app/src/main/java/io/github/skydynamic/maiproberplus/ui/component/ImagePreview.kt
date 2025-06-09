package io.github.skydynamic.maiproberplus.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import kotlin.math.max
import kotlin.math.min

@Composable
fun ImagePreview(
    image: Bitmap,
    onDismiss: () -> Unit
) {
    var imageInitialSize by remember { mutableStateOf(IntSize.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    var maxScale by remember { mutableFloatStateOf(1f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        do {
                            val event = awaitPointerEvent()
                            val zoomChange = event.calculateZoom()
                            scale *= zoomChange

                            var panChange = event.calculatePan()
                            panChange *= scale

                            val tempScale = scale.coerceIn(1f, maxScale)
                            var x = offset.x
                            if (tempScale * imageInitialSize.width > boxSize.width) {
                                val delta =
                                    tempScale * imageInitialSize.width - boxSize.width
                                x = (offset.x + panChange.x).coerceIn(-delta / 2, delta / 2)
                            }
                            var y = offset.y
                            if (tempScale * imageInitialSize.height > boxSize.height) {
                                val delta =
                                    tempScale * imageInitialSize.height - boxSize.height
                                y = (offset.y + panChange.y).coerceIn(-delta / 2, delta / 2)
                            }

                            if (x != offset.x || y != offset.y) {
                                offset = Offset(x, y)
                            }

                            event.changes.forEach {
                                if (it.positionChanged()) {
                                    it.consume()
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        scale = scale.coerceIn(1f, maxScale)
                        if (scale == 1f) {
                            offset = Offset.Zero
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        offset = Offset.Zero
                        scale = if (scale != 1.0f) {
                            1.0f
                        } else {
                            min(2f, min(maxScale, 5f))
                        }
                    }, onTap = {
                        onDismiss()
                    }
                )
            }
            .onSizeChanged {
                boxSize = it
                val xRatio = it.width.toFloat() / imageInitialSize.width
                val yRatio = it.height.toFloat() / imageInitialSize.height
                maxScale = min(5f, max(2f, max(xRatio, yRatio)))
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier
                .onSizeChanged {
                    imageInitialSize = it
                }
        )
    }
}
