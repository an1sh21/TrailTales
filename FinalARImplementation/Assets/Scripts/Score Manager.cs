using UnityEngine;
using TMPro; // Add this namespace for TextMeshPro support

// Fix: Add reference to KMPIntegration if it's in a different namespace
// If KMPIntegration is in a namespace, add: using YourNamespace;

public class ScoreManager : MonoBehaviour
{
    public int score = 0; // The player's current score
    public TextMeshProUGUI scoreText; // Reference to the TextMeshPro UI element to display the score
    
    // Reference to KMPIntegration (add this)
    private KMPIntegration kmpIntegration;
    
    void Start()
    {
        // Get reference to KMPIntegration singleton
        kmpIntegration = KMPIntegration.Instance;
        
        // Initialize score display
        UpdateScoreUI();
    }

    // Call this method to add points to the score
    public void AddScore(int points)
    {
        score += points; // Increase the score
        UpdateScoreUI(); // Update the UI
        
        // Send score to KMP only if integration is available
        if (kmpIntegration != null)
        {
            kmpIntegration.SendScore(score);
        }
        else
        {
            Debug.LogWarning("KMPIntegration not found. Score update not sent to KMP app.");
        }
    }

    // Updates the score text in the UI
    private void UpdateScoreUI()
    {
        if (scoreText != null)
        {
            scoreText.text = "Score: " + score; // Update the text to show the current score
        }
    }

    // Optional: Reset the score to 0
    public void ResetScore()
    {
        score = 0;
        UpdateScoreUI();
        
        // Reset score in KMP as well
        if (kmpIntegration != null)
        {
            kmpIntegration.SendScore(score);
        }
    }
}