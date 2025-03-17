const admin = require("firebase-admin");
const path = require("path");
const fs = require("fs");

// Get the absolute path to the service account key
const serviceAccountPath = process.env.GOOGLE_APPLICATION_CREDENTIALS || 
  path.join(__dirname, '..', '..', 'serviceAccountKey.json');

// Check if the service account file exists
if (!fs.existsSync(serviceAccountPath)) {
  console.error("Service account file not found:", serviceAccountPath);
  process.exit(1);
}

// Load the service account file
const serviceAccount = JSON.parse(fs.readFileSync(serviceAccountPath, 'utf8'));

// Initialize Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

console.log("Firebase Admin SDK initialized successfully.");
const db = admin.firestore();

module.exports = { admin, db };

