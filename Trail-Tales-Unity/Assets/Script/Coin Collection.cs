using UnityEngine;
using UnityEngine.XR.ARFoundation;
using System.Collections.Generic;
using System.Collections;
using UnityEngine.Networking;
using System.Text;
public class ARCoinCollectorDirectTouch : MonoBehaviour
{
    public ARRaycastManager arRaycastManager; // AR Raycast Manager for raycasting
    public ScoreManager scoreManager; // Reference to the ScoreManager
    public AudioClip collectSound; // Sound to play when the coin is collected
    public float fadeDuration = 1f; // Duration of the fade effect
    public float shineIntensity = 5f; // Intensity of the shine effect
    public ParticleSystem collectParticleEffect; // Particle effect to play when the coin is collected

    // List of valid coin tags (add all your coin tags here)
    private List<string> validCoinTags = new List<string>()
    {
        "AeroSpatiale SA 365 Dauphin",
        "Boulton",
        "Douglas DC-3",
        "PAZMANY PL-2",
        "PT-6",
        "Westland Sikorsky S-51 Dragonfly"
    };

    private string backendURL = "https://3000/collectibles";


    private AudioSource audioSource;

    void Start()
    {
        // Ensure there's an AudioSource component
        audioSource = gameObject.AddComponent<AudioSource>();
        audioSource.playOnAwake = false;
        audioSource.clip = collectSound;
    }

    void Update()
    {
        // Check for touch input
        if (Input.touchCount > 0)
        {
            Touch touch = Input.GetTouch(0);

            // Check if the touch phase is began (just touched the screen)
            if (touch.phase == TouchPhase.Began)
            {
                // Perform a raycast from the touch position
                Ray ray = Camera.main.ScreenPointToRay(touch.position);
                RaycastHit hit;

                // Check if the ray hits an object with a valid coin tag
                if (Physics.Raycast(ray, out hit))
                {
                    if (validCoinTags.Contains(hit.collider.tag))
                    {
                        // Collect the coin
                        CollectCoin(hit.collider.gameObject);
                    }
                    else
                    {
                        Debug.Log("Raycast hit an object, but it's not a coin.");
                    }
                }
                else
                {
                    Debug.Log("Raycast did not hit anything.");
                }
            }
        }
    }

    private void CollectCoin(GameObject coin)
    {
        // Check if the coin is visible
        if (!IsCoinVisible(coin))
        {
            Debug.Log("Coin is not visible.");
            return;
        }

        // Play the collect sound
        if (collectSound != null)
        {
            audioSource.Play();
        }

        // Add points to the score (if a score manager is assigned)
        if (scoreManager != null)
        {
            scoreManager.AddScore(GetPointsForCoin(coin.tag));
        }

        // Play the particle effect at the coin's position
        if (collectParticleEffect != null)
        {
            ParticleSystem particles = Instantiate(collectParticleEffect, coin.transform.position, Quaternion.identity);
            particles.Play();
            Destroy(particles.gameObject, particles.main.duration);
        }

         
         
        StartCoroutine(SendCoinDataToBackend(coin.tag, GetPointsForCoin(coin.tag)));
        // Start the fade and shine effect
        StartCoroutine(FadeAndShine(coin));

    }


    private IEnumerator SendCoinDataToBackend(string coinTag, int points)
    {
        // Create JSON payload
        string jsonData = $"{{\"coin\": \"{coinTag}\", \"points\": {points}, \"timestamp\": \"{System.DateTime.UtcNow}\"}}";

        // Create a UnityWebRequest to send data to the backend
        using (UnityWebRequest request = new UnityWebRequest(backendURL, "POST"))
        {
            byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonData);
            request.uploadHandler = new UploadHandlerRaw(bodyRaw);
            request.downloadHandler = new DownloadHandlerBuffer();
            request.SetRequestHeader("Content-Type", "application/json");

            yield return request.SendWebRequest();

            if (request.result == UnityWebRequest.Result.Success)
            {
                Debug.Log("Coin data sent successfully: " + request.downloadHandler.text);
            }
            else
            {
                Debug.LogError("Error sending coin data: " + request.error);
            }
        }
    }


    private bool IsCoinVisible(GameObject coin)
    {
        // Get the coin's position in screen space
        Vector3 screenPosition = Camera.main.WorldToViewportPoint(coin.transform.position);

        // Check if the coin is within the camera's viewport (0 < x, y < 1)
        return screenPosition.x > 0 && screenPosition.x < 1 &&
               screenPosition.y > 0 && screenPosition.y < 1 &&
               screenPosition.z > 0; // Ensure the coin is in front of the camera
    }

    private int GetPointsForCoin(string coinTag)
    {
        // Dictionary to store points for each coin type (you can customize these values)
        System.Collections.Generic.Dictionary<string, int> coinPoints = new System.Collections.Generic.Dictionary<string, int>()
        {
            { "AeroSpatiale SA 365 Dauphin", 10 },
            { "Boulton", 20 },
            { "Douglas DC-3", 30 },
            { "PAZMANY PL-2", 40 },
            { "PT-6", 50 },
            { "Westland Sikorsky S-51 Dragonfly", 60 }
        };

        // Return the points for the coin's tag (default to 0 if the tag is not found)
        return coinPoints.ContainsKey(coinTag) ? coinPoints[coinTag] : 0;
    }

    private System.Collections.IEnumerator FadeAndShine(GameObject coin)
    {
        // Get the renderer and material of the coin
        Renderer renderer = coin.GetComponent<Renderer>();
        Material material = renderer.material;

        // Store the original color and emission color
        Color originalColor = material.color;
        Color originalEmission = material.GetColor("_EmissionColor");

        // Shine the coin brightly
        material.SetColor("_EmissionColor", Color.white * shineIntensity);
        material.EnableKeyword("_EMISSION");

        // Wait for a short time to show the shine effect
        yield return new WaitForSeconds(0.2f);

        // Gradually fade out the coin
        float elapsedTime = 0f;
        while (elapsedTime < fadeDuration)
        {
            float alpha = Mathf.Lerp(1f, 0f, elapsedTime / fadeDuration);
            material.color = new Color(originalColor.r, originalColor.g, originalColor.b, alpha);
            material.SetColor("_EmissionColor", originalEmission * alpha);
            elapsedTime += Time.deltaTime;
            yield return null;
        }

        // Destroy the coin after fading
        Destroy(coin);
    }

}