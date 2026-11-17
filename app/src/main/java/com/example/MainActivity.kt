package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.WallpaperViewModel
import com.example.ui.screens.StudioGalleryScreen
import com.example.ui.screens.WallpaperPreviewScreen
import com.example.ui.theme.WallCraftTheme

private const val ROUTE_STUDIO = "studio"
private const val ROUTE_PREVIEW = "preview"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WallCraftTheme {
                WallCraftApp()
            }
        }
    }
}

@Composable
fun WallCraftApp(
    viewModel: WallpaperViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.importUserPhoto(uri) {
                navController.navigate(ROUTE_PREVIEW) {
                    launchSingleTop = true
                }
            }
        }
    }

    LaunchedEffect(uiState.importError) {
        val error = uiState.importError
        if (error != null) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearImportError()
        }
    }

    val launchPhotoPicker: () -> Unit = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = ROUTE_STUDIO,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable(ROUTE_STUDIO) {
                StudioGalleryScreen(
                    uiState = uiState,
                    onSelectTab = viewModel::selectTab,
                    onSelectCategory = viewModel::selectCategory,
                    onWallpaperClick = { item ->
                        viewModel.openWallpaperPreview(item)
                        navController.navigate(ROUTE_PREVIEW) {
                            launchSingleTop = true
                        }
                    },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onPickPhotoClick = launchPhotoPicker,
                    onRemoveUserPhoto = viewModel::removeUserPhoto
                )
            }

            composable(ROUTE_PREVIEW) {
                val selected = uiState.selectedWallpaper
                if (selected != null) {
                    WallpaperPreviewScreen(
                        item = selected,
                        uiState = uiState,
                        onBack = {
                            viewModel.closeWallpaperPreview()
                            navController.popBackStack()
                        },
                        onSelectOverlayMode = viewModel::setOverlayMode,
                        onDimChange = viewModel::setDimFraction,
                        onToggleBlur = viewModel::toggleBlur,
                        onSelectScaleMode = viewModel::setScaleMode,
                        onZoomChange = viewModel::setZoom,
                        onTransformGesture = viewModel::updatePanAndZoom,
                        onSetPan = viewModel::setPan,
                        onResetCrop = viewModel::resetCrop,
                        onToggleFavorite = { viewModel.toggleFavorite(selected.id) },
                        onApplyWallpaper = viewModel::applySelectedWallpaper,
                        onDismissApplyResult = viewModel::dismissApplyResult
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 84.dp)
        )
    }
}
