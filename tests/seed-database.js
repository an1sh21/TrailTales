const admin = require('firebase-admin');
const path = require('path');
const fs = require('fs');
require('dotenv').config();

// Get the full path to the service account file
const serviceAccountPath = path.resolve(__dirname, '..', './serviceAccountKey.json');
console.log('Looking for service account at:', serviceAccountPath);

let serviceAccount;
try {
  if (fs.existsSync(serviceAccountPath)) {
    console.log('Service account file found!');
    // Read the file directly
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

// ========== SAMPLE DATA FROM FEATURE 1 ==========

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

// ========== SAMPLE DATA FROM FEATURE 2 ==========

// Sample data for quests
const quests = [
  {
    title: "Ancient Temple Exploration",
    description: "Explore the ancient temple and collect sacred tokens",
    difficulty: "medium",
    isActive: true,
    tokenLocations: [
      {
        id: "token1",
        type: "token",
        location: {
          coordinates: { latitude: 12.345, longitude: 67.890 },
          radius: 50,
          siteId: "site1"
        },
        description: "Sacred Temple Token",
        rarity: "rare",
        arMarker: {
          type: "image",
          data: "sample_marker_data"
        }
      }
    ],
    path: [
      { latitude: 12.345, longitude: 67.890 },
      { latitude: 12.346, longitude: 67.891 }
    ],
    siteMap: "/public/rewards/medium.jpg"
  },
  {
    title: "Mountain Trail Challenge",
    description: "Hike the mountain trail and discover hidden treasures",
    difficulty: "hard",
    isActive: true,
    tokenLocations: [
      {
        id: "token2",
        type: "coin",
        location: {
          coordinates: { latitude: 12.355, longitude: 67.880 },
          radius: 30,
          siteId: "site2"
        },
        description: "Mountain Coin",
        rarity: "uncommon",
        arMarker: {
          type: "image",
          data: "mountain_marker"
        }
      }
    ],
    path: [
      { latitude: 12.355, longitude: 67.880 },
      { latitude: 12.357, longitude: 67.882 }
    ],
    siteMap: "/public/rewards/hard.jpg"
  }
];

// Sample data for challenges
const challenges = [
  {
    questId: "quest1", // Will be updated after quest creation
    title: "Photo Challenge",
    description: "Take a photo at the marked location",
    points: 50,
    type: "photo"
  },
  {
    questId: "quest1", // Will be updated after quest creation
    title: "Quiz Challenge",
    description: "Answer questions about the temple history",
    points: 100,
    type: "quiz",
    questions: [
      {
        question: "When was the temple built?",
        options: ["1200 AD", "1500 AD", "1800 AD", "2000 AD"],
        correctAnswer: 0
      }
    ]
  }
];

// Sample user with combined properties
const users = [
  {
    uid: "testuser123",
    email: "test@example.com",
    username: "TestUser",
    collection: [],
    coins: 0,
    tokens: 0,
    quests: [],
    challenges: [],
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  }
];

// Sample user progress data
const userProgress = [
  {
    userId: "testuser123",
    totalCoins: 0,
    completedQuests: [],
    activeQuests: [],
    collectedTokens: [],
    updatedAt: admin.firestore.FieldValue.serverTimestamp()
  }
];

// Sample user rewards data
const userRewards = [
  {
    userId: "testuser123",
    gold: 100,
    diamonds: 5,
    updatedAt: admin.firestore.FieldValue.serverTimestamp()
  }
];

// Sample user collections data
const userCollections = [
  {
    userId: "testuser123",
    tokens: [],
    updatedAt: admin.firestore.FieldValue.serverTimestamp()
  }
];

// Function to add documents to a collection
async function addDocuments(collectionName, documents) {
  console.log(`Adding documents to ${collectionName}...`);
  
  const addedDocs = [];
  for (const doc of documents) {
    try {
      const docRef = await db.collection(collectionName).add(doc);
      console.log(`Added document ${docRef.id} to ${collectionName}`);
      addedDocs.push({ id: docRef.id, ...doc });
    } catch (error) {
      console.error(`Error adding document to ${collectionName}:`, error);
    }
  }
  return addedDocs;
}

// Function to set documents with specific IDs
async function setDocuments(collectionName, documents, idField) {
  console.log(`Setting documents in ${collectionName}...`);
  
  for (const doc of documents) {
    try {
      const id = doc[idField];
      // Remove the id field from the document
      const docData = { ...doc };
      delete docData[idField];
      
      await db.collection(collectionName).doc(id).set(docData);
      console.log(`Set document ${id} in ${collectionName}`);
    } catch (error) {
      console.error(`Error setting document in ${collectionName}:`, error);
    }
  }
}

// Main function to seed the database
async function seedDatabase() {
  try {
    // Add stories
    const addedStories = await addDocuments('stories', stories);
    
    // Add collectibles
    const addedCollectibles = await addDocuments('collectibles', collectibles);
    
    // Add quests
    const addedQuests = await addDocuments('quests', quests);
    
    // Update challenge questIds with actual quest IDs
    for (let challenge of challenges) {
      challenge.questId = addedQuests[0].id;
    }
    
    // Add challenges
    const addedChallenges = await addDocuments('challenges', challenges);
    
    // Add users
    const addedUsers = await addDocuments('users', users);
    
    // Set user progress, rewards, and collections with user ID
    await setDocuments('userProgress', userProgress, 'userId');
    await setDocuments('userRewards', userRewards, 'userId');
    await setDocuments('userCollections', userCollections, 'userId');
    
    console.log('Database seeding completed!');
    process.exit(0);
  } catch (error) {
    console.error('Error seeding database:', error);
    process.exit(1);
  }
}

// Run the seeding function
seedDatabase(); 