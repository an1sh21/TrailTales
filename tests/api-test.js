const axios = require('axios');
require('dotenv').config();

// Configuration
const API_URL = process.env.API_URL || 'http://localhost:9000';
const AUTH_TOKEN = process.env.TEST_AUTH_TOKEN; // Add a test token to your .env file

// Helper function for making API requests
async function apiRequest(method, endpoint, data = null, token = null) {
  try {
    const headers = {
      'Content-Type': 'application/json',
    };
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const config = {
      method,
      url: `${API_URL}${endpoint}`,
      headers,
      ...(data && { data }),
    };
    
    const response = await axios(config);
    return { success: true, data: response.data, status: response.status };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message,
    };
  }
}

// Test basic API connectivity
async function testBasicConnectivity() {
  console.log('🔍 Testing API connectivity...');
  const result = await apiRequest('GET', '/');
  
  if (result.success && result.data.message === 'Trail Tales API is running!') {
    console.log('✅ API is running properly');
    return true;
  } else {
    console.error('❌ API connectivity test failed:', result.message || 'Unknown error');
    return false;
  }
}

// Test stories endpoints
async function testStoriesEndpoints(token) {
  console.log('\n🔍 Testing stories endpoints...');
  
  // Test getting nearby stories
  console.log('Testing nearby stories endpoint...');
  const nearbyStoriesResult = await apiRequest(
    'GET', 
    '/api/stories/nearby?lat=55.8642&lng=-4.2518&radius=10', 
    null,
    token
  );
  
  if (nearbyStoriesResult.success) {
    const stories = nearbyStoriesResult.data.stories || [];
    console.log(`✅ Successfully retrieved ${stories.length} nearby stories`);
    
    // Test getting story details
    if (stories.length > 0) {
      const storyId = stories[0].id;
      console.log(`Testing story details endpoint for story ID: ${storyId}...`);
      
      const storyDetailsResult = await apiRequest('GET', `/api/stories/${storyId}`, null, token);
      
      if (storyDetailsResult.success) {
        console.log('✅ Successfully retrieved story details');
      } else {
        console.error('❌ Failed to retrieve story details:', storyDetailsResult.message);
      }
    } else {
      console.log('⚠️ No stories available to test details endpoint');
    }
  } else {
    console.error('❌ Failed to retrieve nearby stories:', nearbyStoriesResult.message);
  }
}

// Test quests endpoints
async function testQuestsEndpoints(token) {
  console.log('\n🔍 Testing quests endpoints...');
  
  // Test getting all quests
  console.log('Testing all quests endpoint...');
  const questsResult = await apiRequest('GET', '/api/quests', null, token);
  
  if (questsResult.success) {
    const quests = questsResult.data.quests || [];
    console.log(`✅ Successfully retrieved ${quests.length} quests`);
    
    // Test getting quest details
    if (quests.length > 0) {
      const questId = quests[0].id;
      console.log(`Testing quest details endpoint for quest ID: ${questId}...`);
      
      const questDetailsResult = await apiRequest('GET', `/api/quests/${questId}`, null, token);
      
      if (questDetailsResult.success) {
        console.log('✅ Successfully retrieved quest details');
        
        // Test starting a quest (POST)
        console.log('Testing start quest endpoint (POST)...');
        const startQuestResult = await apiRequest(
          'POST',
          `/api/quests/${questId}/start`,
          {},
          token
        );
        
        if (startQuestResult.success) {
          console.log(`✅ Successfully started quest: ${startQuestResult.data.message}`);
        } else {
          console.error('❌ Failed to start quest:', startQuestResult.message);
        }
      } else {
        console.error('❌ Failed to retrieve quest details:', questDetailsResult.message);
      }
    } else {
      console.log('⚠️ No quests available to test details endpoint');
    }
  } else {
    console.error('❌ Failed to retrieve quests:', questsResult.message);
  }
}

// Test collectibles endpoints
async function testCollectiblesEndpoints(token) {
  console.log('\n🔍 Testing collectibles endpoints...');
  
  // Test getting all collectibles
  console.log('Testing all collectibles endpoint...');
  const collectiblesResult = await apiRequest('GET', '/api/collectibles', null, token);
  
  if (collectiblesResult.success) {
    const collectibles = collectiblesResult.data.collectibles || [];
    console.log(`✅ Successfully retrieved ${collectibles.length} collectibles`);
    
    // Test collecting an item (POST)
    if (collectibles.length > 0) {
      console.log('Testing collectible collection (POST) endpoint...');
      const collectibleId = collectibles[0].id;
      console.log('Attempting to collect item with ID:', collectibleId);
      
      const collectResult = await apiRequest(
        'POST',
        '/api/collectibles/collect',
        { itemId: collectibleId },
        token
      );
      
      if (collectResult.success) {
        console.log(`✅ Successfully collected item: ${collectResult.data.message}`);
      } else {
        console.error('❌ Failed to collect item:', collectResult.message);
        console.error('Status:', collectResult.status);
        console.error('Error data:', JSON.stringify(collectResult.data, null, 2));
      }
      
      // Test fetching user's collection
      console.log('Testing user collection endpoint...');
      const userCollectionResult = await apiRequest('GET', '/api/collectibles/user', null, token);
      
      if (userCollectionResult.success) {
        const userCollectibles = userCollectionResult.data.collectibles || [];
        console.log(`✅ User collection has ${userCollectibles.length} collectibles`);
        if (userCollectibles.length > 0) {
          console.log('User collection:', JSON.stringify(userCollectibles, null, 2));
        }
      } else {
        console.error('❌ Failed to retrieve user collection:', userCollectionResult.message);
      }
    } else {
      console.log('⚠️ No collectibles available to test collection endpoint');
    }
  } else {
    console.error('❌ Failed to retrieve collectibles:', collectiblesResult.message);
  }
}

// Test user endpoints
async function testUserEndpoints(token) {
  console.log('\n🔍 Testing user endpoints...');
  
  // Print token info for debugging
  console.log('DEBUG - Token info:');
  try {
    const tokenPayload = JSON.parse(Buffer.from(token.split('.')[1], 'base64').toString());
    console.log('User ID from token:', tokenPayload.user_id || tokenPayload.sub || 'not found');
    console.log('Token expiration:', new Date(tokenPayload.exp * 1000).toISOString());
  } catch (error) {
    console.log('Error decoding token:', error.message);
  }
  
  // Test getting user profile
  console.log('Testing user profile endpoint...');
  const profileResult = await apiRequest('GET', '/api/users/profile', null, token);
  
  if (profileResult.success) {
    console.log('✅ Successfully retrieved user profile');
    console.log('Profile data:', JSON.stringify(profileResult.data, null, 2));
    
    // Test updating user profile (PUT)
    console.log('Testing update profile endpoint (PUT)...');
    const updateResult = await apiRequest(
      'PUT',
      '/api/users/profile',
      {
        displayName: `Test Explorer ${new Date().toISOString().slice(0,10)}`
      },
      token
    );
    
    if (updateResult.success) {
      console.log('✅ Successfully updated user profile');
    } else {
      console.error('❌ Failed to update user profile:', updateResult.message);
    }
  } else {
    console.error('❌ Failed to retrieve user profile:', profileResult.message);
    console.error('Status:', profileResult.status);
    console.error('Error data:', JSON.stringify(profileResult.data, null, 2));
  }
  
  // Test getting user stats
  console.log('Testing user stats endpoint...');
  const statsResult = await apiRequest('GET', '/api/users/stats', null, token);
  
  if (statsResult.success) {
    console.log('✅ Successfully retrieved user stats');
    console.log('Stats data:', JSON.stringify(statsResult.data, null, 2));
  } else {
    console.error('❌ Failed to retrieve user stats:', statsResult.message);
  }
}

// Main test function
async function runTests() {
  console.log('🚀 Starting Trail Tales API Tests');
  console.log('================================\n');
  
  const apiRunning = await testBasicConnectivity();
  
  if (!apiRunning) {
    console.error('Cannot continue tests as the API is not responding.');
    process.exit(1);
  }
  
  if (!AUTH_TOKEN) {
    console.warn('⚠️ No authentication token provided in .env file. Skipping authenticated tests.');
    process.exit(0);
  }
  
  // Run all the tests
  await testStoriesEndpoints(AUTH_TOKEN);
  await testQuestsEndpoints(AUTH_TOKEN);
  await testCollectiblesEndpoints(AUTH_TOKEN);
  await testUserEndpoints(AUTH_TOKEN);
  
  console.log('\n================================');
  console.log('✅ All tests complete');
}

// Run the tests
runTests().catch(error => {
  console.error('Test execution failed:', error);
  process.exit(1);
}); 