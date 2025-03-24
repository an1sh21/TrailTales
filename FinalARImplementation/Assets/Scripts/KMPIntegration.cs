using UnityEngine;
using System;
using System.Collections.Generic;

#if UNITY_ANDROID
using UnityEngine.Android;
#endif

/// <summary>
/// This script manages communication between Unity and KMP on Android
/// It handles sending game data (scores, collected items) to the KMP frontend
/// Place this script on a persistent GameObject in your scene
/// </summary>
public class KMPIntegration : MonoBehaviour
{
    // Singleton instance
    private static KMPIntegration _instance;
    public static KMPIntegration Instance
    {
        get
        {
            if (_instance == null)
            {
                GameObject go = new GameObject("KMPIntegration");
                _instance = go.AddComponent<KMPIntegration>();
                DontDestroyOnLoad(go);
            }
            return _instance;
        }
    }

    // Callback interface used by KMP to receive data
    private AndroidJavaObject kmpCallback;

    // Initialize the integration
    private void Awake()
    {
        if (_instance != null && _instance != this)
        {
            Destroy(gameObject);
            return;
        }

        _instance = this;
        DontDestroyOnLoad(gameObject);
        
        Debug.Log("KMPIntegration initialized");
    }

    // Register the KMP callback
    public void RegisterKMPCallback(AndroidJavaObject callback)
    {
        kmpCallback = callback;
        Debug.Log("KMP callback registered");
    }

    // Send score to KMP frontend
    public void SendScore(int score)
    {
        Debug.Log($"KMPIntegration - Score update: {score}");
        
        if (kmpCallback != null)
        {
            try
            {
                kmpCallback.Call("onScoreUpdated", score);
                Debug.Log($"Score {score} sent to KMP app");
            }
            catch (Exception e)
            {
                Debug.LogError($"Error sending score to KMP: {e.Message}");
            }
        }
        else
        {
            Debug.Log("KMP callback not registered yet. Score update stored locally only.");
        }
    }

    // Send collected item data to KMP frontend
    public void SendCollectedItem(string itemId, string itemDetails)
    {
        Debug.Log($"KMPIntegration - Item collected: {itemId} - {itemDetails}");
        
        if (kmpCallback != null)
        {
            try
            {
                kmpCallback.Call("onItemCollected", itemId, itemDetails);
                Debug.Log($"Item collected notification sent to KMP: {itemId} - {itemDetails}");
            }
            catch (Exception e)
            {
                Debug.LogError($"Error sending item collection to KMP: {e.Message}");
            }
        }
        else
        {
            Debug.Log("KMP callback not registered yet. Item collection stored locally only.");
        }
    }

    // Method to be called from your game code whenever AR session status changes
    public void NotifyARSessionStatus(bool isActive)
    {
        Debug.Log($"KMPIntegration - AR session status: {isActive}");
        
        if (kmpCallback != null)
        {
            try
            {
                kmpCallback.Call("onARSessionStatusChanged", isActive);
                Debug.Log($"AR session status updated: {isActive}");
            }
            catch (Exception e)
            {
                Debug.LogError($"Error sending AR session status to KMP: {e.Message}");
            }
        }
        else
        {
            Debug.Log("KMP callback not registered yet. AR session status stored locally only.");
        }
    }
} 