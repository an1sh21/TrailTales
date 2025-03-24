using UnityEngine;

public class CoinAnimation : MonoBehaviour
{
    public float rotationSpeed = 180f;  // Speed of rotation (degrees per second)
    public float bounceHeight = 0.1f;  // Height of the bounce
    public float bounceSpeed = 2f;  // Speed of the bounce motion

    private Vector3 startPosition;

    void Start()
    {
        // Store the original position of the coin for bounce calculation
        startPosition = transform.position;
    }

    void Update()
    {
        // Make the coin rotate around its local Y-axis (upwards)
        transform.Rotate(Vector3.up, rotationSpeed * Time.deltaTime, Space.World);

        // Make the coin bounce using a sine wave for smooth up-and-down motion
        float newY = startPosition.y + Mathf.Sin(Time.time * bounceSpeed) * bounceHeight;
        transform.position = new Vector3(transform.position.x, newY, transform.position.z);
    }
}
