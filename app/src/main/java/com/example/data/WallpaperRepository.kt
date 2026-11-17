package com.example.data

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.R
import com.example.model.ProceduralStyle
import com.example.model.WallpaperCategory
import com.example.model.WallpaperCropConfig
import com.example.model.WallpaperItem
import com.example.model.WallpaperSource
import com.example.model.WallpaperTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

private val Context.wallpaperDataStore: DataStore<Preferences> by preferencesDataStore(name = "wallcraft_prefs")

class WallpaperRepository(private val context: Context) {

    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorite_wallpaper_ids")
        val ACTIVE_HOME_ID = stringPreferencesKey("active_home_wallpaper_id")
        val USER_PHOTO_PATHS = stringSetPreferencesKey("user_photo_paths")
    }

    val curatedWallpapers: List<WallpaperItem> = listOf(
        WallpaperItem(
            id = "curated_alpine_dusk",
            title = "Alpine Dusk",
            subtitle = "Misty indigo peaks & coral horizon",
            category = WallpaperCategory.NATURE,
            source = WallpaperSource.DrawableResSource(R.drawable.img_wall_alpine_dusk),
            accentColor = Color(0xFFFF7D6B)
        ),
        WallpaperItem(
            id = "curated_silk_waves",
            title = "Silk Ribbons",
            subtitle = "Cobalt & amber fluid glass folds",
            category = WallpaperCategory.ABSTRACT,
            source = WallpaperSource.DrawableResSource(R.drawable.img_wall_silk_waves),
            accentColor = Color(0xFF60A5FA)
        ),
        WallpaperItem(
            id = "curated_desert_arch",
            title = "Sandstone Crescent",
            subtitle = "Terracotta arch under quiet night sky",
            category = WallpaperCategory.MINIMAL,
            source = WallpaperSource.DrawableResSource(R.drawable.img_wall_desert_arch),
            accentColor = Color(0xFFE07A5F)
        ),
        WallpaperItem(
            id = "curated_botanical_mist",
            title = "Botanical Dew",
            subtitle = "Moody emerald leaves for OLED screens",
            category = WallpaperCategory.AMOLED,
            source = WallpaperSource.DrawableResSource(R.drawable.img_wall_botanical_mist),
            accentColor = Color(0xFF34D399)
        ),
        WallpaperItem(
            id = "proc_sunset_mesh",
            title = "Coral Horizon",
            subtitle = "Warm ambient studio mesh",
            category = WallpaperCategory.MINIMAL,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.SUNSET_MESH,
                primaryColorHex = 0xFF1E293B,
                secondaryColorHex = 0xFF4C1D95,
                accentColorHex = 0xFFFF6B4A
            ),
            accentColor = Color(0xFFFF6B4A)
        ),
        WallpaperItem(
            id = "proc_noir_aurora",
            title = "Midnight Aurora",
            subtitle = "True black OLED northern glow",
            category = WallpaperCategory.AMOLED,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.NOIR_AURORA,
                primaryColorHex = 0xFF0D9488,
                secondaryColorHex = 0xFF2563EB,
                accentColorHex = 0xFFA855F7
            ),
            accentColor = Color(0xFF2DD4BF)
        ),
        WallpaperItem(
            id = "proc_ocean_contour",
            title = "Tidal Contours",
            subtitle = "Topographic deep sea lines",
            category = WallpaperCategory.NATURE,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.OCEAN_CONTOUR,
                primaryColorHex = 0xFF0B132B,
                secondaryColorHex = 0xFF1C2541,
                accentColorHex = 0xFF5BC0BE
            ),
            accentColor = Color(0xFF5BC0BE)
        ),
        WallpaperItem(
            id = "proc_bauhaus_dusk",
            title = "Bauhaus Arch",
            subtitle = "Geometric warm ochre study",
            category = WallpaperCategory.MINIMAL,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.BAUHAUS_DUSK,
                primaryColorHex = 0xFF1E2433,
                secondaryColorHex = 0xFFE6CCB2,
                accentColorHex = 0xFFE76F51
            ),
            accentColor = Color(0xFFE76F51)
        ),
        WallpaperItem(
            id = "proc_solar_eclipse",
            title = "Solar Corona",
            subtitle = "Pure black center with amber halo",
            category = WallpaperCategory.AMOLED,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.SOLAR_ECLIPSE,
                primaryColorHex = 0xFFEA580C,
                secondaryColorHex = 0xFF7C2D12,
                accentColorHex = 0xFFFDBA74
            ),
            accentColor = Color(0xFFFB923C)
        ),
        WallpaperItem(
            id = "proc_prism_ribbon",
            title = "Prism Drift",
            subtitle = "Layered translucent color waves",
            category = WallpaperCategory.ABSTRACT,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.PRISM_RIBBON,
                primaryColorHex = 0xFF6366F1,
                secondaryColorHex = 0xFFEC4899,
                accentColorHex = 0xFF22D3EE
            ),
            accentColor = Color(0xFF818CF8)
        ),
        WallpaperItem(
            id = "proc_misty_peaks",
            title = "Dawn Ridges",
            subtitle = "Layered mountain silhouettes",
            category = WallpaperCategory.NATURE,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.MISTY_PEAKS,
                primaryColorHex = 0xFF1E1B4B,
                secondaryColorHex = 0xFF0F172A,
                accentColorHex = 0xFFF472B6
            ),
            accentColor = Color(0xFFF472B6)
        ),
        WallpaperItem(
            id = "proc_velvet_noir",
            title = "Velvet Obsidian",
            subtitle = "Subtle indigo & plum silk shadows",
            category = WallpaperCategory.ABSTRACT,
            source = WallpaperSource.ProceduralSource(
                style = ProceduralStyle.VELVET_NOIR,
                primaryColorHex = 0xFF4F46E5,
                secondaryColorHex = 0xFFBE185D,
                accentColorHex = 0xFF38BDF8
            ),
            accentColor = Color(0xFFA78BFA)
        )
    )

    val favoriteIdsFlow: Flow<Set<String>> = context.wallpaperDataStore.data.map { prefs ->
        prefs[Keys.FAVORITES] ?: emptySet()
    }

    val activeHomeWallpaperIdFlow: Flow<String?> = context.wallpaperDataStore.data.map { prefs ->
        prefs[Keys.ACTIVE_HOME_ID]
    }

    val userPhotosFlow: Flow<List<WallpaperItem>> = context.wallpaperDataStore.data.map { prefs ->
        val entries = prefs[Keys.USER_PHOTO_PATHS] ?: emptySet()
        entries.mapNotNull { entry ->
            val parts = entry.split("|", limit = 2)
            val id = parts.getOrNull(0) ?: return@mapNotNull null
            val path = parts.getOrNull(1) ?: return@mapNotNull null
            val file = File(path)
            if (!file.exists()) return@mapNotNull null
            WallpaperItem(
                id = id,
                title = "Personal Photo",
                subtitle = "Uploaded from device",
                category = WallpaperCategory.ALL,
                source = WallpaperSource.UserPhotoSource(file.absolutePath),
                accentColor = Color(0xFFFF6B4A),
                isUserPhoto = true
            )
        }.reversed()
    }

    suspend fun toggleFavorite(wallpaperId: String) {
        context.wallpaperDataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITES] ?: emptySet()
            prefs[Keys.FAVORITES] = if (wallpaperId in current) {
                current - wallpaperId
            } else {
                current + wallpaperId
            }
        }
    }

    suspend fun importPickedPhoto(uri: Uri): Result<WallpaperItem> = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.filesDir, "custom_wallpapers").apply { mkdirs() }
            val id = "user_${UUID.randomUUID().toString().take(8)}"
            val destFile = File(dir, "$id.jpg")

            val orientation = context.contentResolver.openInputStream(uri)?.use { exifStream ->
                runCatching {
                    ExifInterface(exifStream).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
            } ?: ExifInterface.ORIENTATION_NORMAL

            // Downsample huge camera photos cleanly (max ~2880px) to avoid OOM
            val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, boundsOpts)
            } ?: error("Unable to read selected image")

            val decodeOpts = BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(boundsOpts, 2160, 2880)
            }
            val decoded = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOpts)
            } ?: error("Unsupported image format")

            val oriented = applyExifOrientation(decoded, orientation)
            if (oriented != decoded) decoded.recycle()

            FileOutputStream(destFile).use { out ->
                oriented.compress(Bitmap.CompressFormat.JPEG, 94, out)
            }
            oriented.recycle()

            context.wallpaperDataStore.edit { prefs ->
                val existing = prefs[Keys.USER_PHOTO_PATHS] ?: emptySet()
                prefs[Keys.USER_PHOTO_PATHS] = existing + "$id|${destFile.absolutePath}"
            }

            WallpaperItem(
                id = id,
                title = "Personal Photo",
                subtitle = "Uploaded from device",
                category = WallpaperCategory.ALL,
                source = WallpaperSource.UserPhotoSource(destFile.absolutePath),
                accentColor = Color(0xFFFF6B4A),
                isUserPhoto = true
            )
        }
    }

    suspend fun removeUserPhoto(item: WallpaperItem) = withContext(Dispatchers.IO) {
        val source = item.source as? WallpaperSource.UserPhotoSource ?: return@withContext
        File(source.filePath).delete()
        context.wallpaperDataStore.edit { prefs ->
            val existing = prefs[Keys.USER_PHOTO_PATHS] ?: emptySet()
            prefs[Keys.USER_PHOTO_PATHS] = existing.filterNot { it.startsWith("${item.id}|") }.toSet()
            val favs = prefs[Keys.FAVORITES] ?: emptySet()
            if (item.id in favs) prefs[Keys.FAVORITES] = favs - item.id
        }
    }

    suspend fun applyWallpaper(
        item: WallpaperItem,
        target: WallpaperTarget,
        dimFraction: Float,
        blurEnabled: Boolean,
        cropConfig: WallpaperCropConfig
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val (screenW, screenH) = getScreenDimensions()
            val rawBitmap = loadSourceBitmap(item.source, screenW, screenH)
            val processed = ProceduralWallpaperRenderer.processBitmapForScreen(
                source = rawBitmap,
                targetWidth = screenW,
                targetHeight = screenH,
                dimFraction = dimFraction,
                blurEnabled = blurEnabled,
                cropConfig = cropConfig
            )
            if (rawBitmap != processed) {
                rawBitmap.recycle()
            }

            val wallpaperManager = WallpaperManager.getInstance(context)
            try {
                val flags = when (target) {
                    WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
                    WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
                    WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wallpaperManager.setBitmap(processed, null, true, flags)
            } finally {
                processed.recycle()
            }

            if (target == WallpaperTarget.HOME_SCREEN || target == WallpaperTarget.BOTH) {
                context.wallpaperDataStore.edit { prefs ->
                    prefs[Keys.ACTIVE_HOME_ID] = item.id
                }
            }
        }
    }

    private fun applyExifOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun loadSourceBitmap(
        source: WallpaperSource,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap {
        return when (source) {
            is WallpaperSource.DrawableResSource -> {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeResource(context.resources, source.resId, options)
                options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
                options.inJustDecodeBounds = false
                BitmapFactory.decodeResource(context.resources, source.resId, options)
                    ?: error("Failed to decode wallpaper resource")
            }
            is WallpaperSource.ProceduralSource -> {
                ProceduralWallpaperRenderer.render(source, targetWidth, targetHeight)
            }
            is WallpaperSource.UserPhotoSource -> {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(source.filePath, options)
                options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
                options.inJustDecodeBounds = false
                BitmapFactory.decodeFile(source.filePath, options)
                    ?: error("Failed to decode selected photo")
            }
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight * 1.5f || width > reqWidth * 1.5f) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }

    private fun getScreenDimensions(): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            val w = bounds.width().coerceAtLeast(1080)
            val h = bounds.height().coerceAtLeast(1920)
            w to h
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(metrics)
            metrics.widthPixels.coerceAtLeast(1080) to metrics.heightPixels.coerceAtLeast(1920)
        }
    }
}
