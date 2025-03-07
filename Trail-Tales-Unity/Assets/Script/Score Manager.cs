using UnityEngine;
using TMPro; // Add this namespace for TextMeshPro support

public class ScoreManager : MonoBehaviour
{
    public int score = 0; // The player's current score
    public TextMeshProUGUI scoreText; // Reference to the TextMeshPro UI element to display the score

    // Call this method to add points to the score
    public void AddScore(int points)
    {
        score += points; // Increase the score
        UpdateScoreUI(); // Update the UI
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
    }
}