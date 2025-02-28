const admin = require('firebase-admin');

admin.initializeApp({
  credential: admin.credential.applicationDefault()
});

const db = admin.firestore();
const auth = admin.auth();

module.exports = { db, auth }; 