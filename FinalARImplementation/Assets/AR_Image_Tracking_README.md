# AR Image Tracking - Best Practices

This document provides guidelines to ensure your AR image markers consistently track and display 3D objects in your application.

## Compatible with AR Foundation 4.x

The scripts and recommendations in this document are designed to work with AR Foundation 4.x versions. Some features available in AR Foundation 5.0+ (like confidence values and tracking modes) are not used in this implementation.

## Image Marker Selection

For optimal tracking results:

1. **High Contrast:** Choose images with high contrast and distinctive features
2. **Feature-Rich:** Select images with lots of details, irregular patterns, and unique elements
3. **Non-Reflective:** Avoid glossy, reflective surfaces that create glare
4. **Non-Repetitive:** Avoid repeating patterns (like grids or checkerboards)
5. **Opaque:** Choose opaque rather than transparent or translucent images
6. **Avoid Text-only:** Text alone provides poor tracking - include graphical elements
7. **Distinct:** Ensure markers are visually distinct from each other

## Image Marker Preparation

When preparing your image markers:

1. **Optimal Size:** Physical markers should be at least 10x10 cm (4x4 inches) for reliable detection
2. **Resolution:** Use high-resolution images (at least 300 DPI)
3. **Aspect Ratio:** Use the exact same aspect ratio in Unity as your physical markers
4. **Feature Points:** Look for images with lots of distinctive features for reliable tracking
5. **Print Quality:** Use high-quality printing on matte paper to avoid glare
6. **Flatten Markers:** Ensure physical markers lie completely flat, not curved or bent

## Unity AR Foundation Setup

For optimal configuration in Unity:

1. **Create Reference Library:** 
   - In the Unity Project panel, right-click → Create → XR → XRReferenceImageLibrary
   - Add your marker images with accurate physical sizes
   - Assign this library to your ARTrackedImageManager component

2. **Scene Setup:**
   - Make sure your scene has an ARSession component
   - Add ARSessionOrigin with ARCamera child
   - Add ARTrackedImageManager to your AR camera or another GameObject
   - Assign your XRReferenceImageLibrary to the ARTrackedImageManager
   - Add the ImageTracker script to the same GameObject as ARTrackedImageManager
   - Optionally add AROptimizationUtility for debug visualization

3. **Script Configuration:**
   - Set appropriate objectOffset to position objects relative to markers
   - Configure positionSmoothingFactor (lower = smoother but slower)
   - Set minTrackingTimeForStability (higher = more stable but slower to appear)
   - Set useAnchors to true for improved stability
   - Assign prefabs to the prefabsToSpawn list (make sure prefab names match image names)

## Object Placement Strategies

For consistent object placement:

1. **Stability Tracking:** Using tracking time to build stability
2. **Smooth Movement:** Applying position and rotation smoothing
3. **Anchors:** Using ARAnchors to stabilize position when possible
4. **Animation:** Scale-up objects when they first appear
5. **Persistence:** Maintaining object visibility despite brief tracking loss
6. **Tracking Time:** Requiring minimum tracking time before showing objects
7. **Camera Distance:** Position virtual objects within 0.3-3 meters from the camera

## Testing and Optimization

For best results:

1. **Lighting Conditions:** Test in various lighting conditions (bright/dim/mixed)
2. **Angle Variations:** Test tracking at different angles (0-60° from perpendicular)
3. **Distance Testing:** Verify detection works at various distances
4. **Motion Testing:** Test with different camera movement speeds
5. **Occlusion Testing:** Verify behavior when markers are partially covered
6. **Multiple Devices:** Test on different device models with varying camera qualities

## Troubleshooting

If tracking is inconsistent:

1. **Verify Library:** Ensure your reference image library is properly assigned
2. **Check Physical Markers:** Ensure they match sizes specified in Unity
3. **Lighting Check:** Improve lighting conditions to reduce shadows/glare
4. **Reduce Movement:** Move the camera more slowly during tracking
5. **Reduce Distance:** Move closer to the marker (30-100 cm is optimal)
6. **Debug Visualization:** Enable debug visualization to see tracking status
7. **AR Session Reset:** Try resetting the AR session if tracking fails consistently

## Implementation Notes

The scripts provided include:

1. **ImageTracker** - Provides consistent tracking with:
   - Smooth position and rotation transitions
   - Stability tracking based on tracking time
   - Scale-up animation on first appearance
   - AR anchors for improved stability

2. **AROptimizationUtility** - Optimizes AR settings for better image tracking:
   - Debug visualization of tracked images
   - Manual or periodic AR session reset
   - Customizable debug visualization

## Important Note for Older AR Foundation Versions

If upgrading to AR Foundation 5.0+ in the future, you'll gain access to:
- Confidence values for tracked images
- Additional tracking modes for different performance profiles
- Improved camera focus control

Apply these scripts to your AR camera or ARTrackedImageManager GameObject and adjust settings as needed. 