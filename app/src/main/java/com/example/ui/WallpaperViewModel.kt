package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WallpaperRepository
import com.example.model.ApplyWallpaperState
import com.example.model.MainTab
import com.example.model.PreviewOverlayMode
import com.example.model.WallpaperCategory
import com.example.model.WallpaperCropConfig
import com.example.model.WallpaperItem
import com.example.model.WallpaperScaleMode
import com.example.model.WallpaperTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WallpaperUiState(
    val curatedWallpapers: List<WallpaperItem> = emptyList(),
    val userPhotos: List<WallpaperItem> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val activeHomeWallpaperId: String? = null,
    val selectedCategory: WallpaperCategory = WallpaperCategory.ALL,
    val currentTab: MainTab = MainTab.STUDIO,
    val selectedWallpaper: WallpaperItem? = null,
    val overlayMode: PreviewOverlayMode = PreviewOverlayMode.HOME_SCREEN,
    val dimFraction: Float = 0.08f,
    val blurEnabled: Boolean = false,
    val cropConfig: WallpaperCropConfig = WallpaperCropConfig(),
    val applyState: ApplyWallpaperState = ApplyWallpaperState.Idle,
    val isImportingPhoto: Boolean = false,
    val importError: String? = null
) {
    val allAvailableWallpapers: List<WallpaperItem>
        get() = userPhotos + curatedWallpapers

    val filteredCuratedWallpapers: List<WallpaperItem>
        get() = if (selectedCategory == WallpaperCategory.ALL) {
            curatedWallpapers
        } else {
            curatedWallpapers.filter { it.category == selectedCategory }
        }

    val favoriteWallpapers: List<WallpaperItem>
        get() = allAvailableWallpapers.filter { it.id in favoriteIds }

    val activeHomeWallpaper: WallpaperItem?
        get() = allAvailableWallpapers.firstOrNull { it.id == activeHomeWallpaperId }
}

private data class StudioEditorState(
    val selectedCategory: WallpaperCategory = WallpaperCategory.ALL,
    val currentTab: MainTab = MainTab.STUDIO,
    val selectedWallpaper: WallpaperItem? = null,
    val overlayMode: PreviewOverlayMode = PreviewOverlayMode.HOME_SCREEN,
    val dimFraction: Float = 0.08f,
    val blurEnabled: Boolean = false,
    val cropConfig: WallpaperCropConfig = WallpaperCropConfig(),
    val applyState: ApplyWallpaperState = ApplyWallpaperState.Idle,
    val isImportingPhoto: Boolean = false,
    val importError: String? = null
)

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WallpaperRepository(application.applicationContext)
    private val editorState = MutableStateFlow(StudioEditorState())

    val uiState: StateFlow<WallpaperUiState> = combine(
        repository.favoriteIdsFlow,
        repository.activeHomeWallpaperIdFlow,
        repository.userPhotosFlow,
        editorState
    ) { favorites, activeId, userPhotos, editor ->
        WallpaperUiState(
            curatedWallpapers = repository.curatedWallpapers,
            userPhotos = userPhotos,
            favoriteIds = favorites,
            activeHomeWallpaperId = activeId,
            selectedCategory = editor.selectedCategory,
            currentTab = editor.currentTab,
            selectedWallpaper = editor.selectedWallpaper,
            overlayMode = editor.overlayMode,
            dimFraction = editor.dimFraction,
            blurEnabled = editor.blurEnabled,
            cropConfig = editor.cropConfig,
            applyState = editor.applyState,
            isImportingPhoto = editor.isImportingPhoto,
            importError = editor.importError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WallpaperUiState(curatedWallpapers = repository.curatedWallpapers)
    )

    fun selectTab(tab: MainTab) {
        editorState.update { it.copy(currentTab = tab) }
    }

    fun selectCategory(category: WallpaperCategory) {
        editorState.update { it.copy(selectedCategory = category) }
    }

    fun openWallpaperPreview(item: WallpaperItem) {
        editorState.update {
            it.copy(
                selectedWallpaper = item,
                overlayMode = PreviewOverlayMode.HOME_SCREEN,
                dimFraction = if (item.isUserPhoto) 0.12f else 0.06f,
                blurEnabled = false,
                cropConfig = WallpaperCropConfig(),
                applyState = ApplyWallpaperState.Idle
            )
        }
    }

    fun closeWallpaperPreview() {
        editorState.update {
            it.copy(
                selectedWallpaper = null,
                applyState = ApplyWallpaperState.Idle
            )
        }
    }

    fun setOverlayMode(mode: PreviewOverlayMode) {
        editorState.update { it.copy(overlayMode = mode) }
    }

    fun setDimFraction(dim: Float) {
        editorState.update { it.copy(dimFraction = dim.coerceIn(0f, 0.55f)) }
    }

    fun toggleBlur() {
        editorState.update { it.copy(blurEnabled = !it.blurEnabled) }
    }

    fun setScaleMode(mode: WallpaperScaleMode) {
        editorState.update {
            it.copy(
                cropConfig = it.cropConfig.copy(
                    scaleMode = mode,
                    zoom = 1f,
                    panX = 0f,
                    panY = 0f
                )
            )
        }
    }

    fun setZoom(zoom: Float) {
        editorState.update {
            it.copy(cropConfig = it.cropConfig.copy(zoom = zoom.coerceIn(1f, 3f)))
        }
    }

    fun updatePanAndZoom(zoomDelta: Float, panXNormalized: Float, panYNormalized: Float) {
        editorState.update { state ->
            val nextZoom = (state.cropConfig.zoom * zoomDelta).coerceIn(1f, 3f)
            state.copy(
                cropConfig = state.cropConfig.copy(
                    zoom = nextZoom,
                    panX = panXNormalized.coerceIn(-1f, 1f),
                    panY = panYNormalized.coerceIn(-1f, 1f)
                )
            )
        }
    }

    fun setPan(panX: Float, panY: Float) {
        editorState.update {
            it.copy(
                cropConfig = it.cropConfig.copy(
                    panX = panX.coerceIn(-1f, 1f),
                    panY = panY.coerceIn(-1f, 1f)
                )
            )
        }
    }

    fun resetCrop() {
        editorState.update {
            it.copy(cropConfig = WallpaperCropConfig())
        }
    }

    fun toggleFavorite(wallpaperId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(wallpaperId)
        }
    }

    fun importUserPhoto(uri: Uri, onImported: (WallpaperItem) -> Unit) {
        viewModelScope.launch {
            editorState.update { it.copy(isImportingPhoto = true, importError = null) }
            val result = repository.importPickedPhoto(uri)
            result.onSuccess { item ->
                editorState.update {
                    it.copy(
                        isImportingPhoto = false,
                        currentTab = MainTab.MY_PHOTOS
                    )
                }
                openWallpaperPreview(item)
                onImported(item)
            }.onFailure { err ->
                editorState.update {
                    it.copy(
                        isImportingPhoto = false,
                        importError = err.message ?: "Failed to import photo"
                    )
                }
            }
        }
    }

    fun clearImportError() {
        editorState.update { it.copy(importError = null) }
    }

    fun removeUserPhoto(item: WallpaperItem) {
        viewModelScope.launch {
            repository.removeUserPhoto(item)
            if (editorState.value.selectedWallpaper?.id == item.id) {
                closeWallpaperPreview()
            }
        }
    }

    fun applySelectedWallpaper(target: WallpaperTarget) {
        val current = editorState.value.selectedWallpaper ?: return
        val dim = editorState.value.dimFraction
        val blur = editorState.value.blurEnabled
        val crop = editorState.value.cropConfig

        viewModelScope.launch {
            editorState.update { it.copy(applyState = ApplyWallpaperState.Applying) }
            val result = repository.applyWallpaper(
                item = current,
                target = target,
                dimFraction = dim,
                blurEnabled = blur,
                cropConfig = crop
            )
            result.onSuccess {
                editorState.update { it.copy(applyState = ApplyWallpaperState.Success(target)) }
            }.onFailure { err ->
                editorState.update {
                    it.copy(
                        applyState = ApplyWallpaperState.Error(
                            err.message ?: "Unable to set wallpaper"
                        )
                    )
                }
            }
        }
    }

    fun dismissApplyResult() {
        editorState.update { it.copy(applyState = ApplyWallpaperState.Idle) }
    }
}
