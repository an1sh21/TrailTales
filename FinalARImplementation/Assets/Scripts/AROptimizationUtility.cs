using UnityEngine;
using UnityEngine.XR.ARFoundation;
using UnityEngine.XR.ARSubsystems;

/// <summary>
/// Utility class to optimize AR settings for better image tracking
/// Attach this to the same GameObject that has ARSession and ARTrackedImageManager
/// </summary>
public class AROptimizationUtility : MonoBehaviour
{
    [Header("Image Tracking Settings")]
    [Tooltip("Maximum number of images to track simultaneously")]
    [Range(1, 20)]
    public int maxTrackedImages = 5;
    
    [Tooltip("When true, periodically resets the AR session to improve tracking")]
    public bool periodicSessionReset = false;
    
    [Tooltip("When true, displays debug visualization of tracked images")]
    public bool debugMode = false;
    
    [Header("Components")]
    public ARTrackedImageManager trackedImageManager;
    public ARCameraManager cameraManager;
    public ARSession arSession;
    
    // Visual indicators for tracked images in debug mode
    private GameObject[] debugVisualizers;
    private float sessionResetTimer = 0f;
    private float resetInterval = 120f; // Reset every 2 minutes if enabled
    
    void OnEnable()
    {
        if (trackedImageManager == null)
            trackedImageManager = GetComponent<ARTrackedImageManager>();
            
        if (cameraManager == null) 
            cameraManager = FindObjectOfType<ARCameraManager>();
            
        if (arSession == null)
            arSession = FindObjectOfType<ARSession>();
            
        ApplyOptimalSettings();
        
        if (debugMode)
            SetupDebugVisualizers();
    }
    
    void Update()
    {
        // Handle periodic session reset if enabled
        if (periodicSessionReset && arSession != null)
        {
            sessionResetTimer += Time.deltaTime;
            if (sessionResetTimer > resetInterval)
            {
                arSession.Reset();
                sessionResetTimer = 0f;
                Debug.Log("Performing periodic AR session reset");
            }
        }
    }
    
    void ApplyOptimalSettings()
    {
        if (trackedImageManager != null)
        {
            // Set maximum number of tracked images (may not be supported in all versions)
            try
            {
                var maxTrackedImagesField = trackedImageManager.GetType().GetField("m_MaxNumberOfMovingImages", 
                    System.Reflection.BindingFlags.Instance | System.Reflection.BindingFlags.NonPublic);
                
                if (maxTrackedImagesField != null)
                {
                    maxTrackedImagesField.SetValue(trackedImageManager, maxTrackedImages);
                    Debug.Log($"Set maximum tracked images to {maxTrackedImages}");
                }
            }
            catch (System.Exception e)
            {
                Debug.LogWarning($"Could not set max tracked images: {e.Message}");
            }
                
            // Enable tracking
            trackedImageManager.enabled = true;
        }
        
        if (arSession != null)
        {
            // Reset the AR session to apply new settings
            arSession.Reset();
        }
        
        // Log optimization settings
        Debug.Log($"AR Optimization applied: MaxImages={maxTrackedImages}, PeriodicReset={periodicSessionReset}");
    }
    
    void SetupDebugVisualizers()
    {
        if (!debugMode || trackedImageManager == null) return;
        
        // Create debug visualizers for tracked images
        debugVisualizers = new GameObject[maxTrackedImages];
        
        for (int i = 0; i < maxTrackedImages; i++)
        {
            debugVisualizers[i] = new GameObject($"ImageTracker_Debug_{i}");
            debugVisualizers[i].transform.parent = transform;
            
            // Add visual components for debugging
            var visualizer = debugVisualizers[i].AddComponent<MeshRenderer>();
            visualizer.material = new Material(Shader.Find("Unlit/Transparent"));
            visualizer.material.color = new Color(1f, 0f, 0f, 0.5f);
            
            debugVisualizers[i].AddComponent<MeshFilter>().mesh = CreateQuadMesh();
            debugVisualizers[i].SetActive(false);
        }
        
        // Subscribe to tracked images changed events
        trackedImageManager.trackablesChanged.AddListener(OnTrackedImagesChanged);
    }
    
    void OnTrackedImagesChanged(ARTrackablesChangedEventArgs<ARTrackedImage> eventArgs)
    {
        if (!debugMode) return;
        
        int index = 0;
        
        // Update visualizers for added and updated images
        foreach (var trackedImage in trackedImageManager.trackables)
        {
            if (index >= debugVisualizers.Length) break;
            
            if (trackedImage.trackingState == TrackingState.Tracking)
            {
                debugVisualizers[index].SetActive(true);
                
                // Match position and rotation of tracked image
                debugVisualizers[index].transform.position = trackedImage.transform.position;
                debugVisualizers[index].transform.rotation = trackedImage.transform.rotation;
                
                // Match size of reference image
                Vector2 imageSize = trackedImage.size;
                debugVisualizers[index].transform.localScale = new Vector3(imageSize.x, 0.001f, imageSize.y);
                
                // Set visualization color
                debugVisualizers[index].GetComponent<MeshRenderer>().material.color = new Color(0f, 1f, 0f, 0.5f);
                
                index++;
            }
        }
        
        // Disable unused visualizers
        for (int i = index; i < debugVisualizers.Length; i++)
        {
            debugVisualizers[i].SetActive(false);
        }
    }
    
    Mesh CreateQuadMesh()
    {
        Mesh mesh = new Mesh();
        
        // Create vertices
        mesh.vertices = new Vector3[]
        {
            new Vector3(-0.5f, 0f, -0.5f),
            new Vector3(0.5f, 0f, -0.5f),
            new Vector3(0.5f, 0f, 0.5f),
            new Vector3(-0.5f, 0f, 0.5f)
        };
        
        // Create triangles
        mesh.triangles = new int[] { 0, 1, 2, 0, 2, 3 };
        
        // Create UVs
        mesh.uv = new Vector2[]
        {
            new Vector2(0, 0),
            new Vector2(1, 0),
            new Vector2(1, 1),
            new Vector2(0, 1)
        };
        
        // Calculate normals
        mesh.RecalculateNormals();
        
        return mesh;
    }
    
    void OnDisable()
    {
        if (debugMode && trackedImageManager != null)
        {
            trackedImageManager.trackablesChanged.RemoveListener(OnTrackedImagesChanged);
        }
        
        // Clean up debug visualizers
        if (debugVisualizers != null)
        {
            foreach (var visualizer in debugVisualizers)
            {
                if (visualizer != null)
                    Destroy(visualizer);
            }
        }
    }
    
    /// <summary>
    /// Manually reset the AR session to potentially improve tracking
    /// </summary>
    public void ResetARSession()
    {
        if (arSession != null)
        {
            arSession.Reset();
            Debug.Log("AR Session manually reset");
        }
    }
} 