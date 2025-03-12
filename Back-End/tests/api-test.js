const axios = require('axios');
require('dotenv').config();
const { exchangeCustomTokenForIdToken } = require('./exchange-token');
const { generateToken } = require('./firebase-token-generator');

// Configuration
const API_BASE_URL = 'http://localhost:3000';

/**
 * Tests all API endpoints
 * @param {string} idToken - Firebase ID token to use for authentication
 */
async function testAllEndpoints(idToken) {
  try {
    // Create axios client with authentication
    const client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Authorization': `Bearer ${idToken}`,
        'Content-Type': 'application/json'
      }
    });

    console.log('=== Starting API Endpoint Tests ===\n');

    // Test 1: Get stories endpoint
    try {
      console.log('Testing GET /api/stories/:locationId');
      const locationId = 'location123'; // Replace with an actual location ID
      const response = await client.get(`/api/stories/${locationId}`);
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', error.response?.status, error.response?.data || error.message);
    }
    console.log('\n---\n');

    // Test 2: Unlock a story
    try {
      console.log('Testing POST /api/stories/unlock');
      const response = await client.post('/api/stories/unlock', {
        storyId: 'story123' // Replace with an actual story ID
      });
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', error.response?.status, error.response?.data || error.message);
    }
    console.log('\n---\n');

    // Test 3: Collect items
    try {
      console.log('Testing POST /api/collectibles/collect');
      const response = await client.post('/api/collectibles/collect', {
        itemId: 'item123' // Replace with an actual item ID
      });
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', error.response?.status, error.response?.data || error.message);
    }
    console.log('\n---\n');

    // Test 4: Get all legends
    try {
      console.log('Testing GET /api/legends');
      const response = await client.get('/api/legends');
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', error.response?.status, error.response?.data || error.message);
    }
    console.log('\n---\n');

    // Test 5: Trade legends
    try {
      console.log('Testing POST /api/legends/trade');
      const response = await client.post('/api/legends/trade', {
        legendId: 'legend123', // Replace with an actual legend ID
        targetUserId: 'user456' // Replace with an actual user ID
      });
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', error.response?.status, error.response?.data || error.message);
    }
    
    console.log('\n=== API Testing Complete ===');
  } catch (error) {
    console.error('Error during API testing:', error);
  }
}

// Run the tests when script is executed directly
async function main() {
  try {
    // Generate a custom token
    console.log('Generating Firebase tokens for testing...');
    const customToken = await generateToken();
    
    // Exchange for ID token
    const idToken = await exchangeCustomTokenForIdToken(customToken);
    
    // Run the tests
    await testAllEndpoints(idToken);
    
  } catch (error) {
    console.error('Test failed:', error);
    process.exit(1);
  }
}

if (require.main === module) {
  main().then(() => process.exit(0));
}

module.exports = { testAllEndpoints };