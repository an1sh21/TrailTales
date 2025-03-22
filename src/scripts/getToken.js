const admin = require('firebase-admin');
const serviceAccount = require('../config/serviceAccountKey.json');

// Initialize Firebase Admin
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Create a custom token for a test user
const uid = 'test-user-123'; // This can be any unique identifier
const email = 'test@example.com';

admin.auth().createCustomToken(uid)
  .then((token) => {
    console.log('Custom Token:', token);
    
    // Also create a user if it doesn't exist
    return admin.auth().getUserByEmail(email)
      .catch(() => {
        return admin.auth().createUser({
          email: email,
          password: 'password123',
          uid: uid
        });
      });
  })
  .then((userRecord) => {
    console.log('User Record:', userRecord);
  })
  .catch((error) => {
    console.error('Error:', error);
  }); 