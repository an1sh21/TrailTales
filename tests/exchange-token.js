const axios = require('axios');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

// Get the Firebase API key from environment
const FIREBASE_API_KEY = process.env.FIREBASE_API_KEY;

if (!FIREBASE_API_KEY) {
  console.error('❌ FIREBASE_API_KEY is not set in .env file');
  console.log('Please add your Firebase API key to the .env file:');
  console.log('FIREBASE_API_KEY=your-api-key');
  process.exit(1);
}

// Get the custom token from command line or .env
let customToken = process.argv[2];

if (!customToken) {
  // Try to get from .env file
  customToken = process.env.TEST_AUTH_TOKEN;
  
  if (!customToken) {
    console.error('❌ No custom token provided');
    console.log('Please provide a custom token as a command line argument or set TEST_AUTH_TOKEN in .env file');
    console.log('Usage: node exchange-token.js <custom-token>');
    process.exit(1);
  }
}

// Exchange the custom token for an ID token
async function exchangeToken(customToken) {
  const url = `https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=${FIREBASE_API_KEY}`;
  
  try {
    const response = await axios.post(url, {
      token: customToken,
      returnSecureToken: true
    });
    
    const idToken = response.data.idToken;
    const refreshToken = response.data.refreshToken;
    const expiresIn = response.data.expiresIn;
    
    console.log('✅ Successfully exchanged custom token for ID token');
    console.log('----------------------------------------');
    console.log('ID Token:', idToken);
    console.log('Refresh Token:', refreshToken);
    console.log('Expires In:', expiresIn, 'seconds');
    console.log('----------------------------------------');
    
    // Update the .env file with the ID token
    const envPath = path.resolve(__dirname, '..', '.env');
    let envContent = '';
    
    if (fs.existsSync(envPath)) {
      envContent = fs.readFileSync(envPath, 'utf8');
      
      // Replace or add TEST_AUTH_TOKEN
      if (envContent.includes('TEST_AUTH_TOKEN=')) {
        envContent = envContent.replace(/TEST_AUTH_TOKEN=.*/, `TEST_AUTH_TOKEN=${idToken}`);
      } else {
        envContent += `\nTEST_AUTH_TOKEN=${idToken}\n`;
      }
      
      fs.writeFileSync(envPath, envContent);
      console.log('✅ Updated TEST_AUTH_TOKEN in .env file with the new ID token');
    } else {
      fs.writeFileSync(envPath, `TEST_AUTH_TOKEN=${idToken}\n`);
      console.log('✅ Created .env file with TEST_AUTH_TOKEN');
    }
    
    return idToken;
  } catch (error) {
    console.error('❌ Error exchanging token:', error.response?.data || error.message);
    process.exit(1);
  }
}

// Run the token exchange
exchangeToken(customToken); 