package com.example.trail_tales_front_end_one.android.ui.components

import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * A simple in-memory cache for Bitmaps to avoid redundant loading and scaling of images.
 * Uses an LruCache with a configurable maximum size.
 */
object ImageCache {
    // Default cache size (4MB)
    private const val DEFAULT_CACHE_SIZE = 4 * 1024 * 1024
    
    // The actual cache
    private val cache: LruCache<String, Bitmap> by lazy {
        object : LruCache<String, Bitmap>(DEFAULT_CACHE_SIZE) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // Size in bytes
                return bitmap.byteCount
            }
        }
    }
    
    /**
     * Get a bitmap from the cache.
     * @param key The cache key
     * @return The bitmap or null if not found
     */
    fun get(key: String): Bitmap? {
        return cache.get(key)
    }
    
    /**
     * Put a bitmap in the cache.
     * @param key The cache key
     * @param bitmap The bitmap to cache
     */
    fun put(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }
    
    /**
     * Clear the entire cache.
     */
    fun clear() {
        cache.evictAll()
    }
    
    /**
     * Generate a cache key for a resource ID with specified dimensions.
     * @param resId The resource ID
     * @param width The target width
     * @param height The target height
     * @return A unique key string
     */
    fun generateKey(resId: Int, width: Int, height: Int): String {
        return "img_${resId}_${width}x${height}"
    }
}

/**
 * A composable that manages the ImageCache lifecycle.
 * Will automatically clear the cache when the app is low on memory.
 */
@Composable
fun ImageCacheManager() {
    val context = LocalContext.current
    
    // Remember the callback that will be used to clear the cache
    val clearCacheCallback = remember {
        object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
                // Not needed
            }
            
            override fun onLowMemory() {
                // Clear the cache when memory is low
                ImageCache.clear()
            }
            
            override fun onTrimMemory(level: Int) {
                // Clear on critical memory situations
                if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
                    ImageCache.clear()
                }
            }
        }
    }
    
    // Register and unregister the callback
    DisposableEffect(Unit) {
        context.registerComponentCallbacks(clearCacheCallback)
        onDispose {
            context.unregisterComponentCallbacks(clearCacheCallback)
        }
    }
} 