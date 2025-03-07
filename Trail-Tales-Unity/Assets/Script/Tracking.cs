using UnityEngine;
using UnityEngine.XR.ARFoundation;
using UnityEngine.XR.ARSubsystems;
using System.Collections.Generic;

public class CoinPlacer : MonoBehaviour
{
    public ARTrackedImageManager trackedImageManager;  // AR manager for tracked images
    public GameObject coinPrefab;  // Reference to the 3D coin prefab
    public Vector3 positionOffset = new Vector3(0f, 0.1f, 0f);  // Offset for coin position

    private GameObject spawnedCoin = null;  // Reference to the instantiated coin

    void OnEnable()
    {
        // No longer using trackablesChanged directly.
        // We'll manually track images in the Update method
    }

    void Update()
    {
        // Check for tracked images and handle them every frame
        foreach (var trackedImage in trackedImageManager.trackables)
        {
            if (trackedImage.trackingState == TrackingState.Tracking)
            {
                // If the image is being tracked, place or update the coin
                if (trackedImage.trackingState == TrackingState.Tracking)
                {
                    if (spawnedCoin == null)
                    {
                        PlaceCoin(trackedImage);
                    }
                    else
                    {
                        UpdateCoinPosition(trackedImage);
                    }
                }
                else if (spawnedCoin != null)
                {
                    // Optionally, you can destroy the coin if tracking is lost.
                    Destroy(spawnedCoin);
                }
            }
        }
    }

    void PlaceCoin(ARTrackedImage trackedImage){
    if (trackedImage.trackingState == TrackingState.Tracking)
    {
        // Get the position of the tracked image and apply the offset
        Vector3 position = trackedImage.transform.position + positionOffset;
        Quaternion rotation = trackedImage.transform.rotation;

        // Instantiate the coin if it's not already created
        if (spawnedCoin == null)
        {
            spawnedCoin = Instantiate(coinPrefab, position, rotation);
            spawnedCoin.transform.SetParent(trackedImage.transform);  // Attach coin as a child of the tracked image
        }
    }
    }

    void UpdateCoinPosition(ARTrackedImage trackedImage){
    if (trackedImage.trackingState == TrackingState.Tracking && spawnedCoin != null)
    {
        // If the coin is a child of the tracked image, update its position and rotation
        spawnedCoin.transform.position = trackedImage.transform.position + positionOffset;
        spawnedCoin.transform.rotation = trackedImage.transform.rotation;
    }
    else if (spawnedCoin != null)
    {
        // If the image is no longer being tracked, stop it from following and update manually
        spawnedCoin.transform.position = trackedImage.transform.position + positionOffset;
        spawnedCoin.transform.rotation = trackedImage.transform.rotation;
        
        // Optionally, remove the parent-child relationship
        spawnedCoin.transform.SetParent(null);  // Detach coin from image
        
        // This ensures the coin stays in place while the image is out of view
    }
    }
}
