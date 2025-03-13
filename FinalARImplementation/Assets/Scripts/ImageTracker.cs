using UnityEngine;
using UnityEngine.XR.ARFoundation;
using UnityEngine.XR.ARSubsystems;
using System.Collections.Generic;

public class ImageTracker : MonoBehaviour
{
    [SerializeField] List<GameObject> prefabsToSpawn = new List<GameObject>();
    [SerializeField] Vector3 objectOffset = new Vector3(0f, 0.1f, 0f);
    [SerializeField] bool useAnchors = true;
    [SerializeField] float positionSmoothingFactor = 5f;
    [SerializeField] float rotationSmoothingFactor = 5f;
    [SerializeField] float scaleUpTime = 0.25f;
    [SerializeField] float minTrackingTimeForStability = 0.5f;
    
    private ARTrackedImageManager _trackedImageManager;
    private Dictionary<string, GameObject> _arObjects = new Dictionary<string, GameObject>();
    private Dictionary<string, float> _stabilityScores = new Dictionary<string, float>();
    private Dictionary<string, float> _trackingTimes = new Dictionary<string, float>();
    private Dictionary<string, ARAnchor> _anchors = new Dictionary<string, ARAnchor>();

    private void Awake()
    {
        // Rename the script to match the class name
        gameObject.name = "ImageTracker";
    }

    private void Start()
    {
        _trackedImageManager = GetComponent<ARTrackedImageManager>();
        if (_trackedImageManager == null)
        {
            Debug.LogError("ARTrackedImageManager is required on the same GameObject");
            return;
        }
        
        // Verify that a reference image library is assigned
        if (_trackedImageManager.referenceLibrary == null)
        {
            Debug.LogError("No reference image library assigned to ARTrackedImageManager");
            return;
        }
        
        _trackedImageManager.trackablesChanged.AddListener(OnTrackedImageChanged);
        PreloadObjects();
    }

    private void PreloadObjects()
    {
        foreach (GameObject prefab in prefabsToSpawn)
        {
            if (prefab == null) continue;
            
            // Create but hide AR objects initially
            var arObject = Instantiate(prefab, Vector3.zero, Quaternion.identity);
            arObject.name = prefab.name;
            arObject.SetActive(false);
            
            // Initialize with zero scale and animate up when detected
            arObject.transform.localScale = Vector3.zero;
            
            _arObjects.Add(arObject.name, arObject);
            _stabilityScores.Add(arObject.name, 0f);
            _trackingTimes.Add(arObject.name, 0f);
        }
    }

    private void OnDestroy()
    {
        if (_trackedImageManager != null)
        {
            _trackedImageManager.trackablesChanged.RemoveListener(OnTrackedImageChanged);
        }
    }

    private void OnTrackedImageChanged(ARTrackablesChangedEventArgs<ARTrackedImage> eventArgs)
    {
        // Add newly detected images
        foreach (var trackedImage in eventArgs.added)
        {
            UpdateTrackingStatus(trackedImage);
        }

        // Update existing tracked images
        foreach (var trackedImage in eventArgs.updated)
        {
            UpdateTrackingStatus(trackedImage);
        }

        // Handle removed images
        foreach (var trackedImage in eventArgs.removed)
        {
            string imageName = trackedImage.Value.referenceImage.name;
            if (_arObjects.ContainsKey(imageName))
            {
                // When image tracking is lost, gradually decrease stability
                _stabilityScores[imageName] = Mathf.Max(0, _stabilityScores[imageName] - 0.2f);
                _trackingTimes[imageName] = 0f;
                
                // Either hide the object or leave it at last position based on preference
                if (_stabilityScores[imageName] < 0.2f)
                {
                    _arObjects[imageName].SetActive(false);
                }
            }
            
            // Clean up anchor if we had one
            if (_anchors.ContainsKey(imageName) && _anchors[imageName] != null)
            {
                Destroy(_anchors[imageName]);
                _anchors.Remove(imageName);
            }
        }
    }

    private void UpdateTrackingStatus(ARTrackedImage trackedImage)
    {
        if (trackedImage == null) return;
        
        string imageName = trackedImage.referenceImage.name;
        
        // Skip if we don't have a corresponding prefab for this image
        if (!_arObjects.ContainsKey(imageName)) return;
        
        GameObject arObject = _arObjects[imageName];
        
        // Update tracking state based on TrackingState
        if (trackedImage.trackingState == TrackingState.Tracking)
        {
            // Increase tracking time for this image
            _trackingTimes[imageName] += Time.deltaTime;
            
            // Only consider stable if it's been tracking for a minimum time
            bool isStable = _trackingTimes[imageName] >= minTrackingTimeForStability;
            
            // Increase stability up to a maximum of 1.0
            if (isStable)
            {
                _stabilityScores[imageName] = Mathf.Min(1.0f, _stabilityScores[imageName] + Time.deltaTime);
            }
            
            // First time we detect with good stability, add anchor if enabled
            if (isStable && useAnchors && !_anchors.ContainsKey(imageName))
            {
                ARAnchor newAnchor = trackedImage.gameObject.AddComponent<ARAnchor>();
                _anchors[imageName] = newAnchor;
                arObject.transform.parent = newAnchor.transform;
            }
            
            // Show the object
            if (!arObject.activeSelf && isStable)
            {
                arObject.SetActive(true);
                // Reset scale to zero for animation
                arObject.transform.localScale = Vector3.zero;
            }
            
            // Calculate target position and rotation
            Vector3 targetPosition = trackedImage.transform.position + trackedImage.transform.rotation * objectOffset;
            Quaternion targetRotation = trackedImage.transform.rotation;
            
            // Apply position/rotation for non-anchored objects with smoothing
            if (!useAnchors || !_anchors.ContainsKey(imageName))
            {
                // Apply smoothing based on stability
                float lerpFactor = positionSmoothingFactor * Time.deltaTime * _stabilityScores[imageName];
                arObject.transform.position = Vector3.Lerp(arObject.transform.position, targetPosition, lerpFactor);
                
                float slerpFactor = rotationSmoothingFactor * Time.deltaTime * _stabilityScores[imageName];
                arObject.transform.rotation = Quaternion.Slerp(arObject.transform.rotation, targetRotation, slerpFactor);
            }
            else if (_anchors.ContainsKey(imageName) && _anchors[imageName] != null)
            {
                // For anchored objects, just update the offset from the anchor
                arObject.transform.localPosition = objectOffset;
            }
            
            // Only animate scale if the object is visible
            if (arObject.activeSelf)
            {
                // Animate scale up
                float targetScale = Mathf.Min(1.0f, arObject.transform.localScale.x + Time.deltaTime / scaleUpTime);
                arObject.transform.localScale = new Vector3(targetScale, targetScale, targetScale);
            }
        }
        else
        {
            // Decrease stability when tracking is poor
            _stabilityScores[imageName] = Mathf.Max(0, _stabilityScores[imageName] - Time.deltaTime);
            _trackingTimes[imageName] = Mathf.Max(0, _trackingTimes[imageName] - Time.deltaTime * 2); // Decrease tracking time faster
            
            if (_stabilityScores[imageName] < 0.3f && arObject.activeSelf)
            {
                arObject.SetActive(false);
            }
        }
    }

    // Helper method to manually apply image visibility based on a condition
    public void ShowHideObjectByName(string imageName, bool show)
    {
        if (_arObjects.ContainsKey(imageName))
        {
            _arObjects[imageName].SetActive(show);
            if (show && _arObjects[imageName].transform.localScale.x < 0.1f)
            {
                // Reset scale for animation
                _arObjects[imageName].transform.localScale = Vector3.zero;
            }
        }
    }
}