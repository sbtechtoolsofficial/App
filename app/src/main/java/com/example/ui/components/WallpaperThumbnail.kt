package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ProceduralWallpaperRenderer
import com.example.model.WallpaperCropConfig
import com.example.model.WallpaperItem
import com.example.model.WallpaperScaleMode
import com.example.model.WallpaperSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

private object ThumbnailBitmapCache {
    private val cache = ConcurrentHashMap<String, ImageBitmap>()

    fun getOrRender(
        id: String,
        source: WallpaperSource.ProceduralSource,
        width: Int = 450,
        height: Int = 800
    ): ImageBitmap {
        return cache.getOrPut(id) {
            ProceduralWallpaperRenderer.render(source, width, height).asImageBitmap()
        }
    }
}

@Composable
fun WallpaperThumbnail(
    item: WallpaperItem,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    when (val source = item.source) {
        is WallpaperSource.DrawableResSource -> {
            Image(
                painter = painterResource(id = source.resId),
                contentDescription = item.title,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        is WallpaperSource.ProceduralSource -> {
            val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = item.id) {
                value = withContext(Dispatchers.Default) {
                    ThumbnailBitmapCache.getOrRender(item.id, source)
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = item.title,
                    modifier = modifier,
                    contentScale = contentScale
                )
            } else {
                Box(
                    modifier = modifier.background(Color(source.primaryColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = item.accentColor,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        is WallpaperSource.UserPhotoSource -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(source.filePath))
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}

/**
 * Interactive WYSIWYG preview canvas matching the exact crop, scale, blur, and dim math
 * used by [ProceduralWallpaperRenderer.processBitmapForScreen].
 */
@Composable
fun CroppedWallpaperPreviewCanvas(
    item: WallpaperItem,
    cropConfig: WallpaperCropConfig,
    blurEnabled: Boolean,
    dimFraction: Float,
    interactiveCropEnabled: Boolean,
    onTransformGesture: (zoomDelta: Float, panXNormalized: Float, panYNormalized: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var intrinsicSize by remember(item.id) { mutableStateOf(1080f to 1920f) }

    val highResBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = item.id) {
        value = withContext(Dispatchers.IO) {
            when (val src = item.source) {
                is WallpaperSource.ProceduralSource -> {
                    val bmp = ProceduralWallpaperRenderer.render(src, 1080, 1920)
                    intrinsicSize = bmp.width.toFloat() to bmp.height.toFloat()
                    bmp.asImageBitmap()
                }
                is WallpaperSource.DrawableResSource -> {
                    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeResource(context.resources, src.resId, opts)
                    if (opts.outWidth > 0 && opts.outHeight > 0) {
                        intrinsicSize = opts.outWidth.toFloat() to opts.outHeight.toFloat()
                    }
                    null
                }
                is WallpaperSource.UserPhotoSource -> {
                    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeFile(src.filePath, opts)
                    if (opts.outWidth > 0 && opts.outHeight > 0) {
                        intrinsicSize = opts.outWidth.toFloat() to opts.outHeight.toFloat()
                    }
                    null
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val density = LocalDensity.current
        val containerW = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)
        val containerH = with(density) { maxHeight.toPx() }.coerceAtLeast(1f)
        val (srcW, srcH) = intrinsicSize

        val baseScale = when (cropConfig.scaleMode) {
            WallpaperScaleMode.FILL_SCREEN -> max(containerW / srcW, containerH / srcH)
            WallpaperScaleMode.FIT_FRAME -> min(containerW / srcW, containerH / srcH)
        }
        val effectiveScale = baseScale * cropConfig.zoom.coerceIn(1f, 3f)
        val drawnW = srcW * effectiveScale
        val drawnH = srcH * effectiveScale
        val maxOffsetX = max(0f, (drawnW - containerW) / 2f)
        val maxOffsetY = max(0f, (drawnH - containerH) / 2f)

        val translateX = cropConfig.panX.coerceIn(-1f, 1f) * maxOffsetX
        val translateY = cropConfig.panY.coerceIn(-1f, 1f) * maxOffsetY

        val contentScale = when (cropConfig.scaleMode) {
            WallpaperScaleMode.FILL_SCREEN -> ContentScale.Crop
            WallpaperScaleMode.FIT_FRAME -> ContentScale.Fit
        }

        val blurModifier = if (blurEnabled) {
            Modifier.blur(16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
        } else {
            Modifier
        }

        // Ambient blurred fill behind FIT_FRAME images when letterboxed
        if (drawnW < containerW - 1f || drawnH < containerH - 1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(26.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
            ) {
                RenderWallpaperImage(
                    item = item,
                    proceduralBitmap = highResBitmap,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.43f))
                )
            }
        }

        val gestureModifier = if (interactiveCropEnabled) {
            Modifier.pointerInput(cropConfig.scaleMode, containerW, containerH, srcW, srcH) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val nextZoom = (cropConfig.zoom * zoom).coerceIn(1f, 3f)
                    val nextDrawnW = srcW * baseScale * nextZoom
                    val nextDrawnH = srcH * baseScale * nextZoom
                    val nextMaxX = max(0f, (nextDrawnW - containerW) / 2f)
                    val nextMaxY = max(0f, (nextDrawnH - containerH) / 2f)

                    val curTx = cropConfig.panX * nextMaxX
                    val curTy = cropConfig.panY * nextMaxY
                    val nextPanX = if (nextMaxX > 1f) ((curTx + pan.x) / nextMaxX).coerceIn(-1f, 1f) else 0f
                    val nextPanY = if (nextMaxY > 1f) ((curTy + pan.y) / nextMaxY).coerceIn(-1f, 1f) else 0f
                    onTransformGesture(zoom, nextPanX, nextPanY)
                }
            }
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(blurModifier)
                .then(gestureModifier),
            contentAlignment = Alignment.Center
        ) {
            RenderWallpaperImage(
                item = item,
                proceduralBitmap = highResBitmap,
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = cropConfig.zoom
                        scaleY = cropConfig.zoom
                        translationX = translateX
                        translationY = translateY
                    }
            )
        }

        // Live Icon Contrast Dim scrim
        if (dimFraction > 0.005f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimFraction.coerceIn(0f, 0.6f)))
            )
        }
    }
}

@Composable
private fun RenderWallpaperImage(
    item: WallpaperItem,
    proceduralBitmap: ImageBitmap?,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    when (val source = item.source) {
        is WallpaperSource.DrawableResSource -> {
            Image(
                painter = painterResource(id = source.resId),
                contentDescription = item.title,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        is WallpaperSource.ProceduralSource -> {
            if (proceduralBitmap != null) {
                Image(
                    bitmap = proceduralBitmap,
                    contentDescription = item.title,
                    modifier = modifier,
                    contentScale = contentScale
                )
            } else {
                Box(modifier = modifier.background(Color(source.primaryColorHex)))
            }
        }
        is WallpaperSource.UserPhotoSource -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(source.filePath))
                    .crossfade(false)
                    .build(),
                contentDescription = item.title,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}
