const admin = require('firebase-admin');
require('dotenv').config();
const path = require('path');
const fs = require('fs');

// Get the full path to the service account file
const serviceAccountPath = path.resolve(__dirname, '..', './serviceAccountKey.json');
console.log('Looking for service account at:', serviceAccountPath);

let serviceAccount;
try {
  if (fs.existsSync(serviceAccountPath)) {
    console.log('Service account file found!');
    // Read the file directly instead of requiring it
    const rawData = fs.readFileSync(serviceAccountPath, 'utf8');
    serviceAccount = JSON.parse(rawData);
  } else {
    console.error('Service account file not found at the specified path');
    console.log('Current directory:', __dirname);
    console.log('Files in parent directory:');
    const files = fs.readdirSync(path.resolve(__dirname, '..'));
    console.log(files);
    process.exit(1);
  }
} catch (error) {
  console.error('Error loading service account:', error);
  process.exit(1);
}

// Initialize the app with the service account
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

console.log('Firebase Admin SDK initialized successfully');
const db = admin.firestore();

// Sample data for stories
const stories = [
  {
    title: "The Legend of Trail Peak",
    content: "Long ago, travelers would climb this mountain to seek wisdom...",
    locationId: "location1",
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  },
  {
    title: "The Hidden Waterfall",
    content: "Locals say that this waterfall has magical properties...",
    locationId: "location2",
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  }
];

// Sample data for collectibles
const collectibles = [
  {
    name: "Ancient Coin",
    description: "A rare coin from the medieval period",
    type: "coin",
    locationId: "location1",
    rarity: "common"
  },
  {
    name: "Golden Token",
    description: "A mysterious token with unknown origins",
    type: "token",
    locationId: "location2",
    rarity: "rare"
  }
];

// Sample user
const users = [
  {
    uid: "testuser123",
    email: "test@example.com",
    username: "TestUser",
    collection: [],
    coins: 0,
    tokens: 0,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  }
];

// Function to add documents to a collection
async function addDocuments(collectionName, documents) {
  console.log(`Adding documents to ${collectionName}...`);
  
  for (const doc of documents) {
    try {
      const docRef = await db.collection(collectionName).add(doc);
      console.log(`Added document ${docRef.id} to ${collectionName}`);
    } catch (error) {
      console.error(`Error adding document to ${collectionName}:`, error);
    }
  }
}

// Main function to seed the database
async function seedDatabase() {
  try {
    // Add stories
    await addDocuments('stories', stories);
    
    // Add collectibles
    await addDocuments('collectibles', collectibles);
    
    // Add users
    await addDocuments('users', users);
    
    console.log('Database seeding completed!');
    process.exit(0);
  } catch (error) {
    console.error('Error seeding database:', error);
    process.exit(1);
  }
}

// Run the seeding function
seedDatabase(); 