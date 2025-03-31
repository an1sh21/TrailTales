package com.example.trail_tales_front_end_one.android.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter

/**
 * An optimized image component that efficiently loads and resizes large drawables.
 * Uses ImageCache to avoid redundant loading of images.
 * 
 * @param imageResId The resource ID of the image to load
 * @param contentDescription Content description for accessibility
 * @param modifier Modifier for the image
 * @param alignment Alignment of the image within the component
 * @param contentScale How to scale the image
 * @param alpha Alpha value for the image
 * @param colorFilter Optional color filter to apply to the image
 * @param filterQuality Quality of filtering when scaling the image
 * @param maxWidth Maximum width to resize the image to (default 300dp worth of pixels)
 * @param maxHeight Maximum height to resize the image to (default 300dp worth of pixels)
 */
@Composable
fun OptimizedImage(
    imageResId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = FilterQuality.Medium,
    maxWidth: Int = 600,
    maxHeight: Int = 600
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    // Generate a cache key based on the resource ID and dimensions
    val cacheKey = remember(imageResId, maxWidth, maxHeight) {
        ImageCache.generateKey(imageResId, maxWidth, maxHeight)
    }
    
    // Try to get the bitmap from cache first
    val cachedBitmap = remember(cacheKey) { ImageCache.get(cacheKey) }
    
    // If we have a cached bitmap, use it immediately
    if (cachedBitmap != null) {
        bitmap = cachedBitmap
    }
    
    // Move loading logic to LaunchedEffect instead of direct launch call
    LaunchedEffect(imageResId, maxWidth, maxHeight) {
        // Only load if bitmap is still null
        if (bitmap == null) {
            try {
                // Load the drawable
                val drawable = context.resources.getDrawable(imageResId, context.theme)
                
                // Convert to bitmap
                val originalBitmap = drawable.toBitmap()
                
                // Calculate the scaling factors
                val scaleFactor = minOf(
                    maxWidth.toFloat() / originalBitmap.width,
                    maxHeight.toFloat() / originalBitmap.height
                )
                
                // Only scale down, never up
                val resultBitmap = if (scaleFactor < 1) {
                    val scaledWidth = (originalBitmap.width * scaleFactor).toInt()
                    val scaledHeight = (originalBitmap.height * scaleFactor).toInt()
                    
                    // Create a scaled bitmap
                    Bitmap.createScaledBitmap(
                        originalBitmap,
                        scaledWidth,
                        scaledHeight,
                        true
                    )
                } else {
                    // No scaling needed, use original
                    originalBitmap
                }
                
                // Cache the result for future use
                ImageCache.put(cacheKey, resultBitmap)
                
                // Set the result
                bitmap = resultBitmap
            } catch (e: Exception) {
                // Fallback to default image loading if there's an error
                // No need to handle here as we'll use the default Image if bitmap is null
            }
        }
    }
    
    if (bitmap == null) {
        // Show placeholder while loading
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter
        )
    } else {
        // Show the optimized bitmap image
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality
        )
    }
} 