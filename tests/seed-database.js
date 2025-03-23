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
    title: "Mission 01 : Airforce base",
    content: "Machines that were forged in battle brewed in glory. This museum showcases the Aircrafts and Artefacts that are related to the SRI-LANKAN CIVIL WAR.",
    locationId: "location 01",
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  },
];

// Sample data for collectibles
const collectibles = [
  {
    name: "US Coin",
    description: "A common coin that is scattered across multiple sites in the island.",
    type: "coin",
    locationId: "location 01",
    rarity: "common"
  },
  {
    name: "Air relic of war",
    description: "A token of bravery and glory of the Sri Lankan airforce.",
    type: "token",
    locationId: "location 01",
    rarity: "rare"
  }
];

// ========== SAMPLE DATA FROM FEATURE 2 ==========

// Sample data for quests
const quests = [
  {
    title: "Mission 01: Air force base",
    description: "Explore the Ratmalana Air-Force base museum and discover the history of the SRILANKAN CIVIL WAR form the pov of the skies.",
    difficulty: "easy",
    isActive: true,
    tokenLocations: [
      {
        id: "token 01",
        type: "token",
        location: {
          coordinates: { latitude: 12.345, longitude: 67.890 },
          radius: 50,
          siteId: "site 01"
        },
        description: "A token of bravery and glory of the Sri Lankan airforce.",
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
    siteMap: "/public/rewards/easy.jpg"
  },
  {
    title: "Mission 01: Air force base",
    description: "Explore the Ratmalana Air-Force base museum and discover the history of the SRILANKAN CIVIL WAR form the pov of the skies.",
    difficulty: "easy",
    isActive: true,
    tokenLocations: [
      {
        id: "token 02",
        type: "coin",
        location: {
          coordinates: { latitude: 12.355, longitude: 67.880 },
          radius: 30,
          siteId: "site 01"
        },
        description: "A common coin that is scattered across multiple sites in the island.",
        rarity: "uncommon",
        arMarker: {
          type: "image",
          data: "US coin"
        }
      }
    ],
    path: [
      { latitude: 12.355, longitude: 67.880 },
      { latitude: 12.357, longitude: 67.882 }
    ],
    siteMap: "/public/rewards/easy.jpg"
  }
];

// Sample data for challenges
const challenges = [
  {
    questId: "quest 01", // Will be updated after quest creation
    title: "Find the artefact",
    description: "Find the artifact inside a hanger of the airforce base.",
    points: 50,
    type: "photo"
  },
  {
    questId: "quest 01", // Will be updated after quest creation
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
    uid: "KUgT8Ju89cUAWP103htXUltrXcx1",
    email: "hixsisvix@gmail.com",
    username: "CyberWolf",
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
    userId: "tKUgT8Ju89cUAWP103htXUltrXcx1",
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
    userId: "KUgT8Ju89cUAWP103htXUltrXcx1",
    gold: 100,
    diamonds: 5,
    updatedAt: admin.firestore.FieldValue.serverTimestamp()
  }
];

// Sample user collections data
const userCollections = [
  {
    userId: "KUgT8Ju89cUAWP103htXUltrXcx1",
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