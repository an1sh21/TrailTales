const axios = require('axios');
require('dotenv').config();
const { generateToken } = require('./firebase-token-generator');

/**
 * Exchanges a Firebase custom token for an ID token
 * @param {string} customToken - The custom token to exchange
 * @returns {Promise<string>} - The ID token
 */
async function exchangeCustomTokenForIdToken(customToken) {
  try {
    // You need to get your Firebase API key from the Firebase console
    // Project settings > General > Web API Key
    const apiKey = process.env.FIREBASE_API_KEY;
    
    if (!apiKey) {
      throw new Error('FIREBASE_API_KEY environment variable is not set. Add it to your .env file.');
    }
    
    const response = await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=${apiKey}`,
      {
        token: customToken,
        returnSecureToken: true
      }
    );
    
    const idToken = response.data.idToken;
    console.log('ID token generated successfully:');
    console.log(idToken);
    console.log('\nUse this token in your Authorization header:');
    console.log('Authorization: Bearer ' + idToken);
    
    return idToken;
  } catch (error) {
    console.error('Error exchanging token:', error.response?.data || error);
    throw error;
  }
}

// Run the token exchange when script is run directly
async function main() {
  try {
    // Generate a custom token first
    const customToken = await generateToken();
    
    // Exchange it for an ID token
    await exchangeCustomTokenForIdToken(customToken);
  } catch (error) {
    console.error('Token exchange failed:', error);
    process.exit(1);
  }
}

if (require.main === module) {
  main().then(() => process.exit(0));
}

module.exports = { exchangeCustomTokenForIdToken }; 