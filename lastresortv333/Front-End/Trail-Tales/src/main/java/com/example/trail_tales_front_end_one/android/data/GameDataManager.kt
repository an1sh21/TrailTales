package com.example.trail_tales_front_end_one.android.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameDataManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "TrailTalesGameData"
        private const val KEY_COLLECTED_ITEMS = "collected_items"
        private const val KEY_TOTAL_POINTS = "total_points"
    }

    // State for collected items
    var collectedItems: Set<String> by mutableStateOf(loadCollectedItems())
        private set

    // State for total points
    var totalPoints: Int by mutableStateOf(loadTotalPoints())
        private set

    private fun loadCollectedItems(): Set<String> {
        val json = prefs.getString(KEY_COLLECTED_ITEMS, "[]")
        val type = object : TypeToken<Set<String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptySet()
        } catch (e: Exception) {
            Log.e("GameDataManager", "Error loading collected items: ${e.message}")
            emptySet()
        }
    }

    private fun loadTotalPoints(): Int {
        return prefs.getInt(KEY_TOTAL_POINTS, 0)
    }

    fun addCollectedItem(itemName: String, points: Int) {
        if (!collectedItems.contains(itemName)) {
            val newItems = collectedItems + itemName
            collectedItems = newItems
            totalPoints += points
            
            // Save to SharedPreferences
            saveCollectedItems(newItems)
            saveTotalPoints(totalPoints)
            
            Log.d("GameDataManager", "Added item: $itemName, Points: $points, Total: $totalPoints")
        }
    }

    private fun saveCollectedItems(items: Set<String>) {
        val json = gson.toJson(items)
        prefs.edit().putString(KEY_COLLECTED_ITEMS, json).apply()
    }

    private fun saveTotalPoints(points: Int) {
        prefs.edit().putInt(KEY_TOTAL_POINTS, points).apply()
    }

    fun resetGameData() {
        collectedItems = emptySet()
        totalPoints = 0
        saveCollectedItems(emptySet())
        saveTotalPoints(0)
        Log.d("GameDataManager", "Game data reset")
    }
} 