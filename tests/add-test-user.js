const admin = require('firebase-admin');
const path = require('path');
const fs = require('fs');
require('dotenv').config();

// Initialize Firebase Admin
const serviceAccountPath = path.resolve(__dirname, '..', './serviceAccountKey.json');

try {
  if (fs.existsSync(serviceAccountPath)) {
    console.log('✅ Service account file found at:', serviceAccountPath);
    const serviceAccount = JSON.parse(fs.readFileSync(serviceAccountPath, 'utf8'));
    
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      databaseURL: process.env.FIREBASE_DATABASE_URL
    });
  } else {
    console.error('❌ Service account file not found at:', serviceAccountPath);
    process.exit(1);
  }
} catch (error) {
  console.error('❌ Error initializing Firebase Admin:', error);
  process.exit(1);
}

// Get FireStore DB reference
const db = admin.firestore();

// Create a test user
async function createTestUser() {
  const userId = process.argv[2] || process.env.TEST_USER_ID || 'testuser123';
  
  try {
    // Add the user to the 'users' collection
    await db.collection('users').doc(userId).set({
      username: 'Test User',
      email: 'test@example.com',
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      displayName: 'Test Explorer',
      experience: 100,
      level: 1
    });
    console.log(`✅ Added user document with ID: ${userId}`);
    
    // Make sure user exists in userProgress
    await db.collection('userProgress').doc(userId).set({
      completedQuests: [],
      completedChallenges: [],
      completedStories: [],
      lastActivity: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log(`✅ Added userProgress document for user: ${userId}`);
    
    // Make sure user exists in userRewards
    await db.collection('userRewards').doc(userId).set({
      gold: 100,
      diamonds: 5,
      lastUpdated: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log(`✅ Added userRewards document for user: ${userId}`);
    
    // Make sure user exists in userCollections
    await db.collection('userCollections').doc(userId).set({
      tokens: [],
      collectibles: [],
      lastUpdated: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log(`✅ Added userCollections document for user: ${userId}`);
    
    console.log('✅ Test user setup completed successfully!');
    
    // Update the .env file with the test user ID
    const envPath = path.resolve(__dirname, '..', '.env');
    let envContent = '';
    
    if (fs.existsSync(envPath)) {
      envContent = fs.readFileSync(envPath, 'utf8');
      
      if (!envContent.includes('TEST_USER_ID=')) {
        fs.appendFileSync(envPath, `\nTEST_USER_ID=${userId}\n`);
        console.log(`✅ Added TEST_USER_ID=${userId} to .env file`);
      } else {
        console.log('ℹ️ TEST_USER_ID already exists in .env file');
      }
    }
    
  } catch (error) {
    console.error('❌ Error creating test user:', error);
    process.exit(1);
  }
}

// Run the function
createTestUser(); 