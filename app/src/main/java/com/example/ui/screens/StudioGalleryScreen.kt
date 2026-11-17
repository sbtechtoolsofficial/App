package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.MainTab
import com.example.model.WallpaperCategory
import com.example.model.WallpaperItem
import com.example.ui.WallpaperUiState
import com.example.ui.components.WallpaperThumbnail

@Composable
fun StudioGalleryScreen(
    uiState: WallpaperUiState,
    onSelectTab: (MainTab) -> Unit,
    onSelectCategory: (WallpaperCategory) -> Unit,
    onWallpaperClick: (WallpaperItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPickPhotoClick: () -> Unit,
    onRemoveUserPhoto: (WallpaperItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 680.dp
        val gridColumns = when {
            maxWidth >= 960.dp -> 4
            maxWidth >= 600.dp -> 3
            else -> 2
        }

        if (isExpanded) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = uiState.currentTab == MainTab.STUDIO,
                        onClick = { onSelectTab(MainTab.STUDIO) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.currentTab == MainTab.STUDIO) Icons.Rounded.Collections else Icons.Outlined.Collections,
                                contentDescription = stringResource(R.string.nav_discover)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_discover)) },
                        modifier = Modifier.testTag("nav_tab_studio")
                    )
                    NavigationRailItem(
                        selected = uiState.currentTab == MainTab.SAVED,
                        onClick = { onSelectTab(MainTab.SAVED) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.currentTab == MainTab.SAVED) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = stringResource(R.string.nav_favorites)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_favorites)) },
                        modifier = Modifier.testTag("nav_tab_saved")
                    )
                    NavigationRailItem(
                        selected = uiState.currentTab == MainTab.MY_PHOTOS,
                        onClick = { onSelectTab(MainTab.MY_PHOTOS) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.currentTab == MainTab.MY_PHOTOS) Icons.Rounded.PhotoLibrary else Icons.Outlined.PhotoLibrary,
                                contentDescription = stringResource(R.string.nav_custom)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_custom)) },
                        modifier = Modifier.testTag("nav_tab_my_photos")
                    )
                }

                Scaffold(
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets(0)
                ) { innerPadding ->
                    GalleryContent(
                        uiState = uiState,
                        gridColumns = gridColumns,
                        onSelectCategory = onSelectCategory,
                        onWallpaperClick = onWallpaperClick,
                        onToggleFavorite = onToggleFavorite,
                        onPickPhotoClick = onPickPhotoClick,
                        onRemoveUserPhoto = onRemoveUserPhoto,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavigationBarItem(
                            selected = uiState.currentTab == MainTab.STUDIO,
                            onClick = { onSelectTab(MainTab.STUDIO) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == MainTab.STUDIO) Icons.Rounded.Collections else Icons.Outlined.Collections,
                                    contentDescription = stringResource(R.string.nav_discover)
                                )
                            },
                            label = { Text(stringResource(R.string.nav_discover)) },
                            modifier = Modifier.testTag("nav_tab_studio")
                        )
                        NavigationBarItem(
                            selected = uiState.currentTab == MainTab.SAVED,
                            onClick = { onSelectTab(MainTab.SAVED) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == MainTab.SAVED) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = stringResource(R.string.nav_favorites)
                                )
                            },
                            label = { Text(stringResource(R.string.nav_favorites)) },
                            modifier = Modifier.testTag("nav_tab_saved")
                        )
                        NavigationBarItem(
                            selected = uiState.currentTab == MainTab.MY_PHOTOS,
                            onClick = { onSelectTab(MainTab.MY_PHOTOS) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == MainTab.MY_PHOTOS) Icons.Rounded.PhotoLibrary else Icons.Outlined.PhotoLibrary,
                                    contentDescription = stringResource(R.string.nav_custom)
                                )
                            },
                            label = { Text(stringResource(R.string.nav_custom)) },
                            modifier = Modifier.testTag("nav_tab_my_photos")
                        )
                    }
                }
            ) { innerPadding ->
                GalleryContent(
                    uiState = uiState,
                    gridColumns = gridColumns,
                    onSelectCategory = onSelectCategory,
                    onWallpaperClick = onWallpaperClick,
                    onToggleFavorite = onToggleFavorite,
                    onPickPhotoClick = onPickPhotoClick,
                    onRemoveUserPhoto = onRemoveUserPhoto,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun GalleryContent(
    uiState: WallpaperUiState,
    gridColumns: Int,
    onSelectCategory: (WallpaperCategory) -> Unit,
    onWallpaperClick: (WallpaperItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPickPhotoClick: () -> Unit,
    onRemoveUserPhoto: (WallpaperItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedItems = when (uiState.currentTab) {
        MainTab.STUDIO -> uiState.filteredCuratedWallpapers
        MainTab.SAVED -> uiState.favoriteWallpapers
        MainTab.MY_PHOTOS -> uiState.userPhotos
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1040.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Editorial Header
            item(span = { GridItemSpan(gridColumns) }) {
                StudioHeader(
                    isImporting = uiState.isImportingPhoto,
                    onPickPhotoClick = onPickPhotoClick
                )
            }

            // Active Home Screen Banner or Quick Upload Hero
            if (uiState.currentTab == MainTab.STUDIO) {
                item(span = { GridItemSpan(gridColumns) }) {
                    val activeItem = uiState.activeHomeWallpaper
                    if (activeItem != null) {
                        ActiveHomeBannerCard(
                            item = activeItem,
                            onCustomizeClick = { onWallpaperClick(activeItem) },
                            onUploadClick = onPickPhotoClick
                        )
                    } else {
                        UploadDevicePhotoCard(
                            isImporting = uiState.isImportingPhoto,
                            onUploadClick = onPickPhotoClick
                        )
                    }
                }

                // Category Chips
                item(span = { GridItemSpan(gridColumns) }) {
                    CategoryFilterBar(
                        selected = uiState.selectedCategory,
                        onSelect = onSelectCategory
                    )
                }
            } else if (uiState.currentTab == MainTab.MY_PHOTOS) {
                item(span = { GridItemSpan(gridColumns) }) {
                    UploadDevicePhotoCard(
                        isImporting = uiState.isImportingPhoto,
                        onUploadClick = onPickPhotoClick
                    )
                }
            }

            // Empty States
            if (displayedItems.isEmpty()) {
                item(span = { GridItemSpan(gridColumns) }) {
                    EmptyTabState(
                        tab = uiState.currentTab,
                        onPickPhotoClick = onPickPhotoClick
                    )
                }
            } else {
                items(displayedItems, key = { it.id }) { item ->
                    WallpaperGridCard(
                        item = item,
                        isFavorite = item.id in uiState.favoriteIds,
                        isActiveHome = item.id == uiState.activeHomeWallpaperId,
                        showDelete = uiState.currentTab == MainTab.MY_PHOTOS,
                        onClick = { onWallpaperClick(item) },
                        onToggleFavorite = { onToggleFavorite(item.id) },
                        onDeleteClick = { onRemoveUserPhoto(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudioHeader(
    isImporting: Boolean,
    onPickPhotoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = stringResource(R.string.studio_tagline),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.gallery_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        FilledTonalButton(
            onClick = onPickPhotoClick,
            enabled = !isImporting,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("pick_photo_button")
        ) {
            if (isImporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.AddPhotoAlternate,
                    contentDescription = stringResource(R.string.pick_photo_button),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.pick_photo_button),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ActiveHomeBannerCard(
    item: WallpaperItem,
    onCustomizeClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onCustomizeClick)
            .testTag("active_home_banner"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            ) {
                WallpaperThumbnail(item = item, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = stringResource(R.string.active_home_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Tap to re-crop, adjust dim or switch",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onUploadClick,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddPhotoAlternate,
                    contentDescription = "Upload photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun UploadDevicePhotoCard(
    isImporting: Boolean,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !isImporting, onClick = onUploadClick)
            .testTag("upload_image_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                if (isImporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Upload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Use Your Own Image",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Upload from device, crop & scale to fit your Home Screen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Rounded.Crop,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun CategoryFilterBar(
    selected: WallpaperCategory,
    onSelect: (WallpaperCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(WallpaperCategory.entries) { category ->
            val isSelected = category == selected
            Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelect(category) }
                    .minimumInteractiveComponentSize()
                    .testTag("category_chip_${category.name.lowercase()}")
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WallpaperGridCard(
    item: WallpaperItem,
    isFavorite: Boolean,
    isActiveHome: Boolean,
    showDelete: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 15.5f)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("wallpaper_card_${item.id}"),
        shape = RoundedCornerShape(22.dp),
        border = if (isActiveHome) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            WallpaperThumbnail(
                item = item,
                modifier = Modifier.fillMaxSize()
            )

            // Bottom readability gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.82f)
                            )
                        )
                    )
            )

            // Top badges & action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = isActiveHome,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Active on Home",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Home",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                if (!isActiveHome) Spacer(modifier = Modifier.width(1.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (showDelete) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.36f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Remove photo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.36f))
                            .testTag("favorite_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from saved" else "Save wallpaper",
                            tint = if (isFavorite) Color(0xFFFF6B4A) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Title & category label at bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (item.isUserPhoto) "Tap to crop & set" else item.category.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.78f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EmptyTabState(
    tab: MainTab,
    onPickPhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (tab == MainTab.SAVED) Icons.Outlined.BookmarkBorder else Icons.Rounded.Wallpaper,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (tab == MainTab.SAVED) {
                stringResource(R.string.favorites_empty_title)
            } else {
                stringResource(R.string.my_photos_empty_title)
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (tab == MainTab.SAVED) {
                stringResource(R.string.favorites_empty_subtitle)
            } else {
                stringResource(R.string.my_photos_empty_subtitle)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (tab == MainTab.MY_PHOTOS) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onPickPhotoClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("empty_upload_photo_button")
            ) {
                Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Photo From Device")
            }
        }
    }
}
