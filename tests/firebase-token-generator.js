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

// Create a test user ID or use from command line
const userId = process.argv[2] || 'test-user-' + Date.now();

// Generate a custom token
async function generateToken() {
  try {
    const customToken = await admin.auth().createCustomToken(userId);
    console.log('✅ Successfully generated custom token');
    console.log('----------------------------------------');
    console.log('User ID:', userId);
    console.log('Custom Token:', customToken);
    console.log('----------------------------------------');
    console.log('This token can be exchanged for an ID token using the exchange-token.js script');
    
    // Write to .env file if TEST_AUTH_TOKEN is not already there
    const envPath = path.resolve(__dirname, '..', '.env');
    let envContent = '';
    
    if (fs.existsSync(envPath)) {
      envContent = fs.readFileSync(envPath, 'utf8');
    }
    
    if (!envContent.includes('TEST_AUTH_TOKEN=')) {
      fs.appendFileSync(envPath, `\nTEST_AUTH_TOKEN=${customToken}\n`);
      console.log('✅ Added TEST_AUTH_TOKEN to .env file');
    } else {
      console.log('ℹ️ TEST_AUTH_TOKEN already exists in .env file. You may want to update it manually.');
    }
    
    return customToken;
  } catch (error) {
    console.error('❌ Error generating token:', error);
    process.exit(1);
  }
}

// Run the token generator
generateToken(); 