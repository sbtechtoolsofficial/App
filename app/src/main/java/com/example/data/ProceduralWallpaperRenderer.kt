package com.example.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import com.example.model.ProceduralStyle
import com.example.model.WallpaperSource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

object ProceduralWallpaperRenderer {

    fun render(
        source: WallpaperSource.ProceduralSource,
        width: Int,
        height: Int
    ): Bitmap {
        val safeW = width.coerceAtLeast(360)
        val safeH = height.coerceAtLeast(640)
        val bitmap = Bitmap.createBitmap(safeW, safeH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val w = safeW.toFloat()
        val h = safeH.toFloat()

        val primary = source.primaryColorHex.toInt()
        val secondary = source.secondaryColorHex.toInt()
        val accent = source.accentColorHex.toInt()

        when (source.style) {
            ProceduralStyle.SUNSET_MESH -> drawSunsetMesh(canvas, w, h, primary, secondary, accent)
            ProceduralStyle.OCEAN_CONTOUR -> drawOceanContour(canvas, w, h, primary, secondary, accent)
            ProceduralStyle.NOIR_AURORA -> drawNoirAurora(canvas, w, h, primary, secondary, accent)
            ProceduralStyle.BAUHAUS_DUSK -> drawBauhausDusk(canvas, w, h, primary, secondary, accent)
            ProceduralStyle.PRISM_RIBBON -> drawPrismRibbon(canvas, w, h, primary, secondary, accent)
            ProceduralStyle.SOLAR_ECLIPSE -> drawSolarEclipse(canvas, w, h, primary, secondary, accent)
            ProceduralStyle.MISTY_PEAKS -> drawMistyPeaks(canvas, w, h, primary, secondary, accent)
            ProceduralStyle.VELVET_NOIR -> drawVelvetNoir(canvas, w, h, primary, secondary, accent)
        }
        return bitmap
    }

    private fun drawSunsetMesh(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, w * 0.4f, h,
                intArrayOf(primary, secondary, darken(secondary, 0.45f)),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        val orb1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.78f, h * 0.34f, w * 0.75f,
                intArrayOf(withAlpha(accent, 230), withAlpha(accent, 75), Color.TRANSPARENT),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.78f, h * 0.34f, w * 0.75f, orb1)

        val orb2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.18f, h * 0.72f, w * 0.85f,
                intArrayOf(withAlpha(primary, 200), withAlpha(secondary, 80), Color.TRANSPARENT),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.18f, h * 0.72f, w * 0.85f, orb2)

        val horizonLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(Color.WHITE, 32)
            style = Paint.Style.STROKE
            strokeWidth = w * 0.004f
        }
        canvas.drawLine(0f, h * 0.58f, w, h * 0.58f, horizonLine)
    }

    private fun drawOceanContour(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        val bg = Paint().apply {
            shader = LinearGradient(0f, 0f, 0f, h, primary, secondary, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, w, h, bg)

        val contourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        val layers = 14
        for (i in 0 until layers) {
            val fraction = i / layers.toFloat()
            val baseY = h * (0.32f + fraction * 0.62f)
            val path = Path()
            path.moveTo(-w * 0.05f, baseY)
            val steps = 40
            for (s in 0..steps) {
                val x = (s / steps.toFloat()) * w * 1.1f - w * 0.05f
                val wave = sin((s / steps.toFloat()) * PI * 2.2 + i * 0.55) * (h * 0.032f) +
                    cos((s / steps.toFloat()) * PI * 1.1 - i * 0.3) * (h * 0.018f)
                path.lineTo(x, (baseY + wave).toFloat())
            }
            contourPaint.strokeWidth = w * (0.0028f + fraction * 0.002f)
            contourPaint.color = withAlpha(accent, (38 + fraction * 120).toInt().coerceIn(0, 255))
            canvas.drawPath(path, contourPaint)
        }
    }

    private fun drawNoirAurora(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        canvas.drawColor(Color.BLACK)

        val curtain1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.25f, h * 0.48f, w * 0.9f,
                intArrayOf(withAlpha(primary, 210), withAlpha(secondary, 95), Color.TRANSPARENT),
                floatArrayOf(0f, 0.48f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.25f, h * 0.48f, w * 0.9f, curtain1)

        val curtain2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.85f, h * 0.65f, w * 0.85f,
                intArrayOf(withAlpha(accent, 195), withAlpha(primary, 55), Color.TRANSPARENT),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.85f, h * 0.65f, w * 0.85f, curtain2)

        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(Color.WHITE, 140)
        }
        for (i in 0..48) {
            val sx = ((i * 197 + 43) % 100) / 100f * w
            val sy = ((i * 313 + 17) % 58) / 100f * h
            val r = w * (0.0014f + (i % 3) * 0.0008f)
            canvas.drawCircle(sx, sy, r, starPaint)
        }
    }

    private fun drawBauhausDusk(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        val bg = Paint().apply {
            shader = LinearGradient(0f, 0f, w, h, primary, darken(primary, 0.72f), Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, w, h, bg)

        val archPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = secondary
        }
        val archRect = RectF(w * 0.14f, h * 0.28f, w * 0.86f, h * 0.96f)
        canvas.drawRoundRect(archRect, w * 0.36f, w * 0.36f, archPaint)

        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accent
        }
        canvas.drawCircle(w * 0.5f, h * 0.46f, w * 0.2f, sunPaint)

        val cutPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = darken(primary, 0.55f)
        }
        canvas.drawRect(w * 0.14f, h * 0.64f, w * 0.86f, h * 0.96f, cutPaint)

        val stripePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(Color.WHITE, 48)
            strokeWidth = w * 0.006f
        }
        for (i in 0..5) {
            val y = h * (0.68f + i * 0.045f)
            canvas.drawLine(w * 0.22f, y, w * 0.78f, y, stripePaint)
        }
    }

    private fun drawPrismRibbon(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        val bg = Paint().apply {
            shader = LinearGradient(0f, 0f, 0f, h, darken(primary, 0.25f), darken(secondary, 0.2f), Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, w, h, bg)

        val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (i in 0..4) {
            val offset = i * h * 0.075f
            val path = Path().apply {
                moveTo(-w * 0.1f, h * 0.38f + offset)
                cubicTo(
                    w * 0.32f, h * 0.22f + offset,
                    w * 0.68f, h * 0.64f + offset,
                    w * 1.15f, h * 0.42f + offset
                )
                lineTo(w * 1.15f, h * 0.54f + offset)
                cubicTo(
                    w * 0.68f, h * 0.76f + offset,
                    w * 0.32f, h * 0.34f + offset,
                    -w * 0.1f, h * 0.50f + offset
                )
                close()
            }
            val color = when (i % 3) {
                0 -> withAlpha(primary, 165)
                1 -> withAlpha(secondary, 150)
                else -> withAlpha(accent, 145)
            }
            ribbonPaint.shader = LinearGradient(
                0f, h * 0.3f, w, h * 0.7f,
                color, withAlpha(accent, 45),
                Shader.TileMode.CLAMP
            )
            canvas.drawPath(path, ribbonPaint)
        }
    }

    private fun drawSolarEclipse(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        canvas.drawColor(Color.BLACK)

        val halo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.5f, h * 0.45f, w * 0.62f,
                intArrayOf(accent, primary, withAlpha(secondary, 60), Color.TRANSPARENT),
                floatArrayOf(0.32f, 0.46f, 0.72f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(w * 0.5f, h * 0.45f, w * 0.62f, halo)

        val core = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
        }
        canvas.drawCircle(w * 0.5f, h * 0.45f, w * 0.24f, core)

        val rim = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = w * 0.008f
            color = withAlpha(accent, 220)
        }
        canvas.drawCircle(w * 0.5f, h * 0.45f, w * 0.243f, rim)
    }

    private fun drawMistyPeaks(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        val sky = Paint().apply {
            shader = LinearGradient(0f, 0f, 0f, h * 0.7f, primary, accent, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, w, h, sky)

        val moon = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(Color.WHITE, 210)
        }
        canvas.drawCircle(w * 0.72f, h * 0.26f, w * 0.085f, moon)

        val ridgeColors = listOf(
            blendColors(secondary, accent, 0.45f),
            blendColors(secondary, primary, 0.35f),
            secondary,
            darken(secondary, 0.55f)
        )
        ridgeColors.forEachIndexed { idx, col ->
            val baseY = h * (0.50f + idx * 0.11f)
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, baseY)
                val peaks = 7
                for (p in 1..peaks) {
                    val px = (p / peaks.toFloat()) * w
                    val offset = sin(p * 1.4 + idx * 1.7) * (h * 0.06f)
                    lineTo(px, (baseY - offset).toFloat())
                }
                lineTo(w, h)
                close()
            }
            canvas.drawPath(path, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = col })
        }
    }

    private fun drawVelvetNoir(
        canvas: Canvas, w: Float, h: Float,
        primary: Int, secondary: Int, accent: Int
    ) {
        canvas.drawColor(Color.BLACK)
        val foldPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (i in 0..6) {
            val cx = w * (0.2f + (i % 3) * 0.35f)
            val cy = h * (0.25f + i * 0.11f)
            val radius = w * 0.78f
            val c = if (i % 2 == 0) primary else secondary
            foldPaint.shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(withAlpha(c, 125), withAlpha(accent, 35), Color.TRANSPARENT),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx, cy, radius, foldPaint)
        }
    }

    fun processBitmapForScreen(
        source: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        dimFraction: Float,
        blurEnabled: Boolean,
        cropConfig: com.example.model.WallpaperCropConfig = com.example.model.WallpaperCropConfig()
    ): Bitmap {
        val safeTargetW = targetWidth.coerceAtLeast(360)
        val safeTargetH = targetHeight.coerceAtLeast(640)

        val srcW = source.width.toFloat()
        val srcH = source.height.toFloat()

        val baseScale = when (cropConfig.scaleMode) {
            com.example.model.WallpaperScaleMode.FILL_SCREEN -> max(safeTargetW / srcW, safeTargetH / srcH)
            com.example.model.WallpaperScaleMode.FIT_FRAME -> min(safeTargetW / srcW, safeTargetH / srcH)
        }
        val effectiveScale = baseScale * cropConfig.zoom.coerceIn(1f, 3f)
        val drawnW = (srcW * effectiveScale).roundToInt().coerceAtLeast(1)
        val drawnH = (srcH * effectiveScale).roundToInt().coerceAtLeast(1)

        val output = Bitmap.createBitmap(safeTargetW, safeTargetH, Bitmap.Config.ARGB_8888)
        val outCanvas = Canvas(output)

        // When FIT_FRAME leaves letterbox space, render a softly blurred ambient fill behind the photo
        if (drawnW < safeTargetW || drawnH < safeTargetH) {
            val fillScale = max(safeTargetW / srcW, safeTargetH / srcH)
            val fillW = (srcW * fillScale).roundToInt().coerceAtLeast(safeTargetW)
            val fillH = (srcH * fillScale).roundToInt().coerceAtLeast(safeTargetH)
            val scaledBg = Bitmap.createScaledBitmap(source, fillW, fillH, true)
            val croppedBg = Bitmap.createBitmap(
                scaledBg,
                ((fillW - safeTargetW) / 2).coerceAtLeast(0),
                ((fillH - safeTargetH) / 2).coerceAtLeast(0),
                safeTargetW,
                safeTargetH
            )
            if (scaledBg != source && scaledBg != croppedBg) scaledBg.recycle()
            val ambientBg = fastBlurBitmap(croppedBg, radius = 16)
            croppedBg.recycle()
            outCanvas.drawBitmap(ambientBg, 0f, 0f, null)
            ambientBg.recycle()
            outCanvas.drawColor(Color.argb(110, 0, 0, 0))
        }

        val scaledFg = Bitmap.createScaledBitmap(source, drawnW, drawnH, true)
        val maxOffsetX = max(0f, (drawnW - safeTargetW) / 2f)
        val maxOffsetY = max(0f, (drawnH - safeTargetH) / 2f)
        val translateX = cropConfig.panX.coerceIn(-1f, 1f) * maxOffsetX
        val translateY = cropConfig.panY.coerceIn(-1f, 1f) * maxOffsetY
        val left = (safeTargetW - drawnW) / 2f + translateX
        val top = (safeTargetH - drawnH) / 2f + translateY

        outCanvas.drawBitmap(scaledFg, left, top, Paint(Paint.FILTER_BITMAP_FLAG))
        if (scaledFg != source) scaledFg.recycle()

        val finalBitmap = if (blurEnabled) {
            val blurred = fastBlurBitmap(output, radius = 14)
            output.recycle()
            blurred
        } else {
            output
        }

        if (dimFraction > 0.01f) {
            val dimCanvas = Canvas(finalBitmap)
            val alpha = (dimFraction.coerceIn(0f, 0.6f) * 255).roundToInt()
            dimCanvas.drawColor(Color.argb(alpha, 0, 0, 0))
        }
        return finalBitmap
    }

    private fun fastBlurBitmap(source: Bitmap, radius: Int): Bitmap {
        val downscaleFactor = 6
        val smallW = (source.width / downscaleFactor).coerceAtLeast(32)
        val smallH = (source.height / downscaleFactor).coerceAtLeast(32)
        val small = Bitmap.createScaledBitmap(source, smallW, smallH, true)
        val blurredSmall = boxBlur(small, radius.coerceIn(2, 18))
        small.recycle()
        val upscaled = Bitmap.createScaledBitmap(blurredSmall, source.width, source.height, true)
        blurredSmall.recycle()
        return upscaled
    }

    private fun boxBlur(bitmap: Bitmap, radius: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val temp = IntArray(w * h)
        val div = radius * 2 + 1

        for (pass in 0..1) {
            for (y in 0 until h) {
                var rSum = 0
                var gSum = 0
                var bSum = 0
                val rowOffset = y * w
                for (dx in -radius..radius) {
                    val c = pix[rowOffset + dx.coerceIn(0, w - 1)]
                    rSum += (c shr 16) and 0xFF
                    gSum += (c shr 8) and 0xFF
                    bSum += c and 0xFF
                }
                for (x in 0 until w) {
                    temp[rowOffset + x] =
                        (0xFF shl 24) or ((rSum / div) shl 16) or ((gSum / div) shl 8) or (bSum / div)
                    val nextX = (x + radius + 1).coerceIn(0, w - 1)
                    val prevX = (x - radius).coerceIn(0, w - 1)
                    val cNext = pix[rowOffset + nextX]
                    val cPrev = pix[rowOffset + prevX]
                    rSum += ((cNext shr 16) and 0xFF) - ((cPrev shr 16) and 0xFF)
                    gSum += ((cNext shr 8) and 0xFF) - ((cPrev shr 8) and 0xFF)
                    bSum += (cNext and 0xFF) - (cPrev and 0xFF)
                }
            }
            for (x in 0 until w) {
                var rSum = 0
                var gSum = 0
                var bSum = 0
                for (dy in -radius..radius) {
                    val c = temp[dy.coerceIn(0, h - 1) * w + x]
                    rSum += (c shr 16) and 0xFF
                    gSum += (c shr 8) and 0xFF
                    bSum += c and 0xFF
                }
                for (y in 0 until h) {
                    pix[y * w + x] =
                        (0xFF shl 24) or ((rSum / div) shl 16) or ((gSum / div) shl 8) or (bSum / div)
                    val nextY = (y + radius + 1).coerceIn(0, h - 1)
                    val prevY = (y - radius).coerceIn(0, h - 1)
                    val cNext = temp[nextY * w + x]
                    val cPrev = temp[prevY * w + x]
                    rSum += ((cNext shr 16) and 0xFF) - ((cPrev shr 16) and 0xFF)
                    gSum += ((cNext shr 8) and 0xFF) - ((cPrev shr 8) and 0xFF)
                    bSum += (cNext and 0xFF) - (cPrev and 0xFF)
                }
            }
        }
        return Bitmap.createBitmap(pix, w, h, Bitmap.Config.ARGB_8888)
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun darken(color: Int, factor: Float): Int {
        return Color.rgb(
            (Color.red(color) * factor).roundToInt().coerceIn(0, 255),
            (Color.green(color) * factor).roundToInt().coerceIn(0, 255),
            (Color.blue(color) * factor).roundToInt().coerceIn(0, 255)
        )
    }

    private fun blendColors(c1: Int, c2: Int, ratio: Float): Int {
        val inv = 1f - ratio
        return Color.rgb(
            (Color.red(c1) * inv + Color.red(c2) * ratio).roundToInt().coerceIn(0, 255),
            (Color.green(c1) * inv + Color.green(c2) * ratio).roundToInt().coerceIn(0, 255),
            (Color.blue(c1) * inv + Color.blue(c2) * ratio).roundToInt().coerceIn(0, 255)
        )
    }
}
