const admin = require('firebase-admin');
const serviceAccount = require('./path-to-your-service-account-key.json');
const axios = require('axios');

// Initialize the app
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

async function generateTestToken() {
  try {
    // Method 1: Create a new test user (if needed)
    let testUser;
    try {
      testUser = await admin.auth().getUserByEmail('test@example.com');
      console.log('Test user already exists');
    } catch (error) {
      // User doesn't exist, create a new one
      testUser = await admin.auth().createUser({
        email: 'test@example.com',
        password: 'password123',
        displayName: 'Test User'
      });
      console.log('Created new test user');
    }

    // Generate a custom token for this user
    const customToken = await admin.auth().createCustomToken(testUser.uid);
    console.log('Custom token:', customToken);
    
    // Method 2: Even easier - create a token with custom claims for any UID
    const uid = 'test-user-123'; // Can be any string
    const additionalClaims = {
      email: 'test@example.com',
      role: 'user'
    };
    
    const customTokenWithClaims = await admin.auth().createCustomToken(uid, additionalClaims);
    console.log('Custom token with claims:', customTokenWithClaims);
    
    return customTokenWithClaims;
  } catch (error) {
    console.error('Error generating token:', error);
  }
}

// Using Firebase client SDK to exchange tokens
const firebase = require('firebase/app');
require('firebase/auth');

const firebaseConfig = {
  // Your Firebase project config (get from Firebase console)
  apiKey: "your-api-key",
  authDomain: "your-project.firebaseapp.com",
  projectId: "your-project",
  // ...other config
};

firebase.initializeApp(firebaseConfig);

async function getIdTokenFromCustomToken(customToken) {
  const apiKey = 'your-firebase-api-key'; // From Firebase console
  
  try {
    const response = await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=${apiKey}`,
      {
        token: customToken,
        returnSecureToken: true
      }
    );
    
    return response.data.idToken;
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

// Use both functions together
async function generateTestIdToken() {
  const customToken = await generateTestToken();
  const idToken = await getIdTokenFromCustomToken(customToken);
  return idToken;
}

generateTestIdToken();

const API_BASE_URL = 'http://localhost:3000';
const FIREBASE_API_KEY = 'your-firebase-api-key';

// Get an ID token for testing
async function getTestIdToken() {
  try {
    // Create a test user or use existing
    let uid;
    try {
      const userRecord = await admin.auth().getUserByEmail('test@example.com');
      uid = userRecord.uid;
    } catch (error) {
      const newUser = await admin.auth().createUser({
        email: 'test@example.com',
        password: 'password123'
      });
      uid = newUser.uid;
    }
    
    // Create custom token
    const customToken = await admin.auth().createCustomToken(uid);
    
    // Exchange for ID token
    const response = await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=${FIREBASE_API_KEY}`,
      {
        token: customToken,
        returnSecureToken: true
      }
    );
    
    return response.data.idToken;
  } catch (error) {
    console.error('Failed to get test token:', error);
    throw error;
  }
}

// Test all endpoints
async function testAllEndpoints() {
  try {
    // Get token
    const token = await getTestIdToken();
    console.log('Got token:', token.substring(0, 20) + '...');
    
    // Create authenticated client
    const client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    // Test endpoints
    console.log('\n=== Testing Stories Endpoint ===');
    const storiesResponse = await client.get('/api/stories/location123');
    console.log('✅ Success:', storiesResponse.data);
    
    console.log('\n=== Testing Story Unlock ===');
    const unlockResponse = await client.post('/api/stories/unlock', {
      storyId: 'story123'
    });
    console.log('✅ Success:', unlockResponse.data);
    
    // Add more endpoint tests...
    
  } catch (error) {
    console.error('❌ Test failed:', error.response?.data || error.message);
  }
}

testAllEndpoints(); 