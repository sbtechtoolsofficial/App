package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.data.ProceduralWallpaperRenderer
import com.example.model.ProceduralStyle
import com.example.model.WallpaperCropConfig
import com.example.model.WallpaperScaleMode
import com.example.model.WallpaperSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches WallCraft`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WallCraft", appName)
    }

    @Test
    fun `fill screen crop and zoom produces exact screen dimensions`() {
        val source = Bitmap.createBitmap(1600, 900, Bitmap.Config.ARGB_8888)
        val processed = ProceduralWallpaperRenderer.processBitmapForScreen(
            source = source,
            targetWidth = 1080,
            targetHeight = 2400,
            dimFraction = 0.2f,
            blurEnabled = false,
            cropConfig = WallpaperCropConfig(
                scaleMode = WallpaperScaleMode.FILL_SCREEN,
                zoom = 1.8f,
                panX = -0.5f,
                panY = 0.25f
            )
        )
        assertEquals(1080, processed.width)
        assertEquals(2400, processed.height)
    }

    @Test
    fun `fit frame with blur produces exact screen dimensions`() {
        val procedural = ProceduralWallpaperRenderer.render(
            WallpaperSource.ProceduralSource(
                style = ProceduralStyle.SUNSET_MESH,
                primaryColorHex = 0xFF1E293B,
                secondaryColorHex = 0xFF4C1D95,
                accentColorHex = 0xFFFF6B4A
            ),
            width = 800,
            height = 600
        )
        val processed = ProceduralWallpaperRenderer.processBitmapForScreen(
            source = procedural,
            targetWidth = 720,
            targetHeight = 1440,
            dimFraction = 0.1f,
            blurEnabled = true,
            cropConfig = WallpaperCropConfig(
                scaleMode = WallpaperScaleMode.FIT_FRAME,
                zoom = 1f,
                panX = 0f,
                panY = 0f
            )
        )
        assertNotNull(processed)
        assertEquals(720, processed.width)
        assertEquals(1440, processed.height)
    }
}
