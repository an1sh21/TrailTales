const axios = require('axios');
require('dotenv').config();

async function quickTest() {
  const token = process.env.TEST_AUTH_TOKEN;
  const baseUrl = 'http://localhost:9000';
  
  try {
    // Test basic connectivity
    console.log('Testing base endpoint...');
    const baseResponse = await axios.get(baseUrl);
    console.log('Base endpoint response:', baseResponse.data);
    
    // Test collectibles endpoint (GET)
    console.log('\nTesting collectibles endpoint (GET)...');
    const collectiblesResponse = await axios.get(`${baseUrl}/api/collectibles`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    console.log(`Collectibles endpoint response: Found ${collectiblesResponse.data.collectibles.length} collectibles`);
    
    if (collectiblesResponse.data.collectibles.length > 0) {
      const collectibleId = collectiblesResponse.data.collectibles[0].id;
      
      // Test collect endpoint (POST)
      console.log('\nTesting collectibles/collect endpoint (POST)...');
      const collectResponse = await axios.post(
        `${baseUrl}/api/collectibles/collect`,
        { itemId: collectibleId },
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      console.log('Collect endpoint response:', collectResponse.data);
      
      // Test user's collection (GET)
      console.log('\nTesting collectibles/user endpoint (GET)...');
      const userCollectionResponse = await axios.get(`${baseUrl}/api/collectibles/user`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      console.log(`User collection response: Found ${userCollectionResponse.data.collectibles.length} collectibles`);
    }
    
    // Test quests endpoint (GET)
    console.log('\nTesting quests endpoint (GET)...');
    const questsResponse = await axios.get(`${baseUrl}/api/quests`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    console.log(`Quests endpoint response: Found ${questsResponse.data.quests.length} quests`);
    
    if (questsResponse.data.quests.length > 0) {
      const questId = questsResponse.data.quests[0].id;
      
      // Test quest details endpoint (GET)
      console.log('\nTesting quest details endpoint (GET)...');
      const questDetailsResponse = await axios.get(`${baseUrl}/api/quests/${questId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      console.log('Quest details endpoint response:', questDetailsResponse.data.id ? 'Success' : 'Failed');
      
      // Test start quest endpoint (POST)
      console.log('\nTesting start quest endpoint (POST)...');
      const startQuestResponse = await axios.post(
        `${baseUrl}/api/quests/${questId}/start`,
        {},
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      console.log('Start quest endpoint response:', startQuestResponse.data);
    }
    
    // Test user profile endpoint (GET)
    console.log('\nTesting user profile endpoint (GET)...');
    const profileResponse = await axios.get(`${baseUrl}/api/users/profile`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    console.log('Profile endpoint response:', profileResponse.data.id ? 'Success' : 'Failed');
    
    // Test update profile endpoint (PUT)
    console.log('\nTesting update profile endpoint (PUT)...');
    const updateProfileResponse = await axios.put(
      `${baseUrl}/api/users/profile`,
      {
        displayName: `Test Explorer ${new Date().toISOString().slice(0,10)}`
      },
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );
    console.log('Update profile endpoint response:', updateProfileResponse.data);
    
    // Test user stats endpoint (GET)
    console.log('\nTesting user stats endpoint (GET)...');
    const statsResponse = await axios.get(`${baseUrl}/api/users/stats`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    console.log('Stats endpoint response:', statsResponse.data);
    
    console.log('\nAll tests passed!');
  } catch (error) {
    console.error('Error during test:', error.message);
    if (error.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
  }
}

quickTest(); 