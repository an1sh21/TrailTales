const axios = require('axios');
require('dotenv').config();
const { exchangeCustomTokenForIdToken } = require('./exchange-token');
const { generateToken } = require('./firebase-token-generator');

// Configuration
const API_BASE_URL = 'http://localhost:3000';

// Test data
const TEST_LOCATION_ID = 'location1';  // Update with a valid location ID from your database
const TEST_STORY_ID = 'story1';        // Update with a valid story ID from your database
const TEST_ITEM_ID = 'coin1';          // Update with a valid collectible ID from your database

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
      const response = await client.get(`/api/stories/${TEST_LOCATION_ID}`);
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });
    }
    console.log('\n---\n');

    // Test 2: Unlock a story
    try {
      console.log('Testing POST /api/stories/unlock');
      const response = await client.post('/api/stories/unlock', {
        storyId: TEST_STORY_ID
      });
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });
    }
    console.log('\n---\n');

    // Test 3: Collect items
    try {
      console.log('Testing POST /api/collectibles/collect');
      const response = await client.post('/api/collectibles/collect', {
        itemId: TEST_ITEM_ID
      });
      console.log('✅ Success! Status:', response.status);
      console.log('Response data:', JSON.stringify(response.data, null, 2));
    } catch (error) {
      console.error('❌ Error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });
    }
    console.log('\n---\n');
    
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