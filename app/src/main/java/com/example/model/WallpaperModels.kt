package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class WallpaperCategory(val label: String) {
    ALL("All"),
    NATURE("Nature"),
    ABSTRACT("Abstract"),
    MINIMAL("Minimal"),
    AMOLED("AMOLED")
}

enum class ProceduralStyle {
    SUNSET_MESH,
    OCEAN_CONTOUR,
    NOIR_AURORA,
    BAUHAUS_DUSK,
    PRISM_RIBBON,
    SOLAR_ECLIPSE,
    MISTY_PEAKS,
    VELVET_NOIR
}

sealed interface WallpaperSource {
    data class DrawableResSource(@DrawableRes val resId: Int) : WallpaperSource
    data class ProceduralSource(
        val style: ProceduralStyle,
        val primaryColorHex: Long,
        val secondaryColorHex: Long,
        val accentColorHex: Long
    ) : WallpaperSource
    data class UserPhotoSource(val filePath: String) : WallpaperSource
}

data class WallpaperItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: WallpaperCategory,
    val source: WallpaperSource,
    val accentColor: Color,
    val isUserPhoto: Boolean = false
)

enum class WallpaperScaleMode(val label: String) {
    FILL_SCREEN("Fill Screen"),
    FIT_FRAME("Fit Photo")
}

data class WallpaperCropConfig(
    val scaleMode: WallpaperScaleMode = WallpaperScaleMode.FILL_SCREEN,
    val zoom: Float = 1f,
    val panX: Float = 0f, // Normalized -1f..1f
    val panY: Float = 0f  // Normalized -1f..1f
)

enum class WallpaperTarget(val displayName: String) {
    HOME_SCREEN("Home Screen"),
    LOCK_SCREEN("Lock Screen"),
    BOTH("Home & Lock")
}

enum class PreviewOverlayMode {
    CLEAN,
    HOME_SCREEN,
    LOCK_SCREEN
}

enum class MainTab {
    STUDIO,
    SAVED,
    MY_PHOTOS
}

sealed interface ApplyWallpaperState {
    data object Idle : ApplyWallpaperState
    data object Applying : ApplyWallpaperState
    data class Success(val target: WallpaperTarget) : ApplyWallpaperState
    data class Error(val message: String) : ApplyWallpaperState
}
