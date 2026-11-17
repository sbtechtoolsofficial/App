package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FitScreen
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ApplyWallpaperState
import com.example.model.PreviewOverlayMode
import com.example.model.WallpaperItem
import com.example.model.WallpaperScaleMode
import com.example.model.WallpaperTarget
import com.example.ui.WallpaperUiState
import com.example.ui.components.CroppedWallpaperPreviewCanvas
import com.example.ui.components.HomeScreenMockupOverlay
import com.example.ui.components.LockScreenMockupOverlay
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperPreviewScreen(
    item: WallpaperItem,
    uiState: WallpaperUiState,
    onBack: () -> Unit,
    onSelectOverlayMode: (PreviewOverlayMode) -> Unit,
    onDimChange: (Float) -> Unit,
    onToggleBlur: () -> Unit,
    onSelectScaleMode: (WallpaperScaleMode) -> Unit,
    onZoomChange: (Float) -> Unit,
    onTransformGesture: (Float, Float, Float) -> Unit,
    onSetPan: (Float, Float) -> Unit,
    onResetCrop: () -> Unit,
    onToggleFavorite: () -> Unit,
    onApplyWallpaper: (WallpaperTarget) -> Unit,
    onDismissApplyResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val haptic = LocalHapticFeedback.current
    var showTargetSheet by remember { mutableStateOf(false) }
    // Panel tab: 0 = Crop & Scale, 1 = Blur & Dim, -1 = Collapsed
    var activePanel by remember { mutableIntStateOf(if (item.isUserPhoto) 0 else -1) }
    val isFavorite = item.id in uiState.favoriteIds
    val isApplying = uiState.applyState is ApplyWallpaperState.Applying

    LaunchedEffect(uiState.applyState) {
        if (uiState.applyState is ApplyWallpaperState.Success) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(2800)
            onDismissApplyResult()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Full-bleed WYSIWYG cropped, scaled, blurred, and dimmed wallpaper canvas
        CroppedWallpaperPreviewCanvas(
            item = item,
            cropConfig = uiState.cropConfig,
            blurEnabled = uiState.blurEnabled,
            dimFraction = uiState.dimFraction,
            interactiveCropEnabled = true,
            onTransformGesture = onTransformGesture,
            modifier = Modifier.fillMaxSize()
        )

        // Realistic Home Screen or Lock Screen overlay
        when (uiState.overlayMode) {
            PreviewOverlayMode.HOME_SCREEN -> HomeScreenMockupOverlay()
            PreviewOverlayMode.LOCK_SCREEN -> LockScreenMockupOverlay()
            PreviewOverlayMode.CLEAN -> Unit
        }

        // Top Bar: Back, Overlay Mode Switcher, Favorite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("preview_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back to gallery",
                    tint = Color.White
                )
            }

            OverlayModeSwitcher(
                selectedMode = uiState.overlayMode,
                onSelectMode = onSelectOverlayMode
            )

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("preview_favorite_button")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavorite) "Saved" else "Save wallpaper",
                    tint = if (isFavorite) Color(0xFFFF6B4A) else Color.White
                )
            }
        }

        // Feedback Banner on Success / Error
        AnimatedVisibility(
            visible = uiState.applyState is ApplyWallpaperState.Success || uiState.applyState is ApplyWallpaperState.Error,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 72.dp, start = 20.dp, end = 20.dp)
        ) {
            val isSuccess = uiState.applyState is ApplyWallpaperState.Success
            val bannerText = when (val state = uiState.applyState) {
                is ApplyWallpaperState.Success -> when (state.target) {
                    WallpaperTarget.HOME_SCREEN -> stringResource(R.string.wallpaper_applied_home)
                    WallpaperTarget.LOCK_SCREEN -> stringResource(R.string.wallpaper_applied_lock)
                    WallpaperTarget.BOTH -> stringResource(R.string.wallpaper_applied_both)
                }
                is ApplyWallpaperState.Error -> state.message
                else -> ""
            }
            Surface(
                shape = CircleShape,
                color = if (isSuccess) Color(0xFF0F766E) else MaterialTheme.colorScheme.errorContainer,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = if (isSuccess) Color(0xFFA7F3D0) else MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = bannerText,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSuccess) Color.White else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Bottom Floating Studio Controls & Primary CTA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 540.dp)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Expandable Editor Dock (Crop & Scale OR Blur & Dim)
            AnimatedVisibility(visible = activePanel != -1) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF11151E).copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (activePanel == 0) {
                        CropAndScalePanel(
                            uiState = uiState,
                            onSelectScaleMode = onSelectScaleMode,
                            onZoomChange = onZoomChange,
                            onSetPan = onSetPan,
                            onResetCrop = onResetCrop
                        )
                    } else if (activePanel == 1) {
                        LookAndDimPanel(
                            dimFraction = uiState.dimFraction,
                            blurEnabled = uiState.blurEnabled,
                            onDimChange = onDimChange,
                            onToggleBlur = onToggleBlur
                        )
                    }
                }
            }

            // Quick studio toolbar + Primary "Set Home Wallpaper" CTA
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF11151E).copy(alpha = 0.9f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Studio tool switcher row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StudioToolChip(
                            label = "Crop & Scale",
                            icon = Icons.Rounded.Crop,
                            selected = activePanel == 0,
                            onClick = { activePanel = if (activePanel == 0) -1 else 0 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_crop_panel")
                        )
                        StudioToolChip(
                            label = "Dim & Blur",
                            icon = Icons.Rounded.Tune,
                            selected = activePanel == 1,
                            onClick = { activePanel = if (activePanel == 1) -1 else 1 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_look_panel")
                        )
                    }

                    // Primary CTA Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onApplyWallpaper(WallpaperTarget.HOME_SCREEN) },
                            enabled = !isApplying,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(vertical = 15.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("set_home_wallpaper_button")
                        ) {
                            if (isApplying) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.applying_wallpaper))
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.set_home_wallpaper),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        FilledTonalIconButton(
                            onClick = { showTargetSheet = true },
                            enabled = !isApplying,
                            shape = RoundedCornerShape(18.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.14f),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .size(52.dp)
                                .testTag("more_targets_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PhoneAndroid,
                                contentDescription = "Choose screen target"
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTargetSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTargetSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Set Wallpaper Target",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Apply your cropped & adjusted wallpaper to:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                TargetOptionRow(
                    title = "Home Screen",
                    subtitle = "Sets wallpaper behind your app icons & widgets",
                    icon = Icons.Rounded.Home,
                    onClick = {
                        showTargetSheet = false
                        onApplyWallpaper(WallpaperTarget.HOME_SCREEN)
                    },
                    modifier = Modifier.testTag("target_home_option")
                )
                TargetOptionRow(
                    title = "Lock Screen",
                    subtitle = "Sets wallpaper on your device lock screen",
                    icon = Icons.Rounded.Lock,
                    onClick = {
                        showTargetSheet = false
                        onApplyWallpaper(WallpaperTarget.LOCK_SCREEN)
                    },
                    modifier = Modifier.testTag("target_lock_option")
                )
                TargetOptionRow(
                    title = "Both Home & Lock",
                    subtitle = "Apply seamlessly across both screens",
                    icon = Icons.Rounded.PhoneAndroid,
                    onClick = {
                        showTargetSheet = false
                        onApplyWallpaper(WallpaperTarget.BOTH)
                    },
                    modifier = Modifier.testTag("target_both_option")
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun CropAndScalePanel(
    uiState: WallpaperUiState,
    onSelectScaleMode: (WallpaperScaleMode) -> Unit,
    onZoomChange: (Float) -> Unit,
    onSetPan: (Float, Float) -> Unit,
    onResetCrop: () -> Unit
) {
    val crop = uiState.cropConfig
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pinch or drag image to frame",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            TextButton(
                onClick = onResetCrop,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("reset_crop_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = null,
                    tint = Color(0xFFFF6B4A),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Reset",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF6B4A)
                )
            }
        }

        // Scale Mode Switcher: Fill Screen vs Fit Photo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScaleModePill(
                label = WallpaperScaleMode.FILL_SCREEN.label,
                icon = Icons.Rounded.Fullscreen,
                selected = crop.scaleMode == WallpaperScaleMode.FILL_SCREEN,
                onClick = { onSelectScaleMode(WallpaperScaleMode.FILL_SCREEN) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("scale_mode_fill")
            )
            ScaleModePill(
                label = WallpaperScaleMode.FIT_FRAME.label,
                icon = Icons.Rounded.FitScreen,
                selected = crop.scaleMode == WallpaperScaleMode.FIT_FRAME,
                onClick = { onSelectScaleMode(WallpaperScaleMode.FIT_FRAME) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("scale_mode_fit")
            )
        }

        // Zoom Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Scale / Zoom",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    text = "${((crop.zoom * 10f).roundToInt() / 10f)}x",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF6B4A),
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = crop.zoom,
                onValueChange = onZoomChange,
                valueRange = 1f..3f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFF6B4A),
                    activeTrackColor = Color(0xFFFF6B4A),
                    inactiveTrackColor = Color.White.copy(alpha = 0.22f)
                ),
                modifier = Modifier.testTag("crop_zoom_slider")
            )
        }

        // Quick Horizontal Framing Presets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Left" to 1f,
                "Center" to 0f,
                "Right" to -1f
            ).forEach { (label, targetPanX) ->
                val active = kotlin.math.abs(crop.panX - targetPanX) < 0.12f
                Surface(
                    shape = CircleShape,
                    color = if (active) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .clickable { onSetPan(targetPanX, crop.panY) }
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LookAndDimPanel(
    dimFraction: Float,
    blurEnabled: Boolean,
    onDimChange: (Float) -> Unit,
    onToggleBlur: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dim_label),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = "${(dimFraction * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFF6B4A)
            )
        }
        Slider(
            value = dimFraction,
            onValueChange = onDimChange,
            valueRange = 0f..0.55f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF6B4A),
                activeTrackColor = Color(0xFFFF6B4A),
                inactiveTrackColor = Color.White.copy(alpha = 0.22f)
            ),
            modifier = Modifier.testTag("dim_slider")
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (blurEnabled) Color(0xFFFF6B4A).copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
            border = BorderStroke(
                1.dp,
                if (blurEnabled) Color(0xFFFF6B4A) else Color.White.copy(alpha = 0.16f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onToggleBlur)
                .minimumInteractiveComponentSize()
                .testTag("blur_toggle_chip")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BlurOn,
                        contentDescription = null,
                        tint = if (blurEnabled) Color(0xFFFF6B4A) else Color.White
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.blur_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                        Text(
                            text = "Makes Home Screen icons & text stand out",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                Text(
                    text = if (blurEnabled) "ON" else "OFF",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (blurEnabled) Color(0xFFFF6B4A) else Color.White.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
private fun ScaleModePill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFFFF6B4A).copy(alpha = 0.24f) else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFFFF6B4A) else Color.White.copy(alpha = 0.16f)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color(0xFFFF6B4A) else Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StudioToolChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(
            1.dp,
            if (selected) Color.White.copy(alpha = 0.45f) else Color.Transparent
        ),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color(0xFFFF6B4A) else Color.White,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun OverlayModeSwitcher(
    selectedMode: PreviewOverlayMode,
    onSelectMode: (PreviewOverlayMode) -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.48f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            listOf(
                PreviewOverlayMode.CLEAN to "Clean",
                PreviewOverlayMode.HOME_SCREEN to "Home",
                PreviewOverlayMode.LOCK_SCREEN to "Lock"
            ).forEach { (mode, label) ->
                val isSelected = selectedMode == mode
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color.White else Color.Transparent,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSelectMode(mode) }
                        .testTag("overlay_mode_${mode.name.lowercase()}")
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.Black else Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
