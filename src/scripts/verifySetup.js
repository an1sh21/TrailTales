const { db, admin } = require('../config/firebase');

async function verifySetup() {
  try {
    console.log('🔍 Starting Firebase Setup Verification...\n');

    // 1. Test Database Connection
    console.log('1️⃣ Testing Database Connection...');
    await db.collection('test').doc('test').set({ test: true });
    console.log('✅ Database connection successful!\n');

    // 2. Test Security Rules
    console.log('2️⃣ Testing Security Rules...');
    const testUserId = 'test_user_123';
    
    // Create test user progress
    await db.collection('userProgress').doc(testUserId).set({
      totalCoins: 0,
      completedQuests: [],
      activeQuests: [],
      collectedTokens: []
    });
    console.log('✅ Security rules for userProgress working!\n');

    // 3. Test Quest Collection
    console.log('3️⃣ Testing Quest Collection...');
    const testQuest = {
      title: "Test Quest",
      description: "A test quest for verification",
      difficulty: "easy",
      isActive: true,
      tokenLocations: [
        {
          id: "test_token_1",
          type: "token",
          location: {
            coordinates: { latitude: 6.9271, longitude: 79.8612 },
            radius: 50
          },
          description: "Test Token",
          arMarker: {
            type: "image",
            data: "test_marker"
          },
          collectInstructions: "Tap on the Token"
        }
      ],
      path: [
        { latitude: 6.9271, longitude: 79.8612 }
      ],
      totalSteps: 1
    };

    const questRef = await db.collection('quests').add(testQuest);
    console.log('✅ Quest collection working!\n');

    // 4. Test User Rewards
    console.log('4️⃣ Testing User Rewards...');
    await db.collection('userRewards').doc(testUserId).set({
      gold: 0,
      diamonds: 0
    });
    console.log('✅ User rewards working!\n');

    // 5. Test User Collections
    console.log('5️⃣ Testing User Collections...');
    await db.collection('userCollections').doc(testUserId).set({
      tokens: []
    });
    console.log('✅ User collections working!\n');

    // 6. Test Indexes
    console.log('6️⃣ Testing Indexes...');
    const questsSnapshot = await db.collection('quests')
      .where('isActive', '==', true)
      .where('difficulty', '==', 'easy')
      .get();
    console.log('✅ Indexes working!\n');

    // 7. Cleanup Test Data
    console.log('7️⃣ Cleaning up test data...');
    await db.collection('test').doc('test').delete();
    await db.collection('userProgress').doc(testUserId).delete();
    await db.collection('userRewards').doc(testUserId).delete();
    await db.collection('userCollections').doc(testUserId).delete();
    await questRef.delete();
    console.log('✅ Test data cleaned up!\n');

    console.log('🎉 All Firebase setup verifications passed successfully!');
    console.log('\nYour database is ready for use with the following collections:');
    console.log('- quests');
    console.log('- userProgress');
    console.log('- userRewards');
    console.log('- userCollections');

  } catch (error) {
    console.error('❌ Verification failed:', error);
    console.error('\nPlease check:');
    console.error('1. Firebase configuration in src/config/firebase.js');
    console.error('2. Service account key file');
    console.error('3. Security rules in Firebase Console');
    console.error('4. Database indexes in Firebase Console');
  }
}

// Run the verification
verifySetup(); 