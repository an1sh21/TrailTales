const { db } = require('../config/firebase');
const admin = require('firebase-admin');

async function initializeDatabase() {
  try {
    // 1. Create a sample quest
    const questData = {
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
      ]
    };

    const questRef = await db.collection('quests').add(questData);
    console.log('Sample quest created with ID:', questRef.id);

    // 2. Create indexes
    // Note: Indexes need to be created in Firebase Console
    console.log('Please create the following indexes in Firebase Console:');
    console.log('Collection: userProgress');
    console.log('Fields: userId (Ascending), questId (Ascending), status (Ascending)');

    // 3. Set up security rules
    console.log('Setting up security rules in Firebase Console:');
    console.log(`
    rules_version = '2';
    service cloud.firestore {
      match /databases/{database}/documents {
        function isAuthenticated() {
          return request.auth != null;
        }
        function isOwner(userId) {
          return request.auth.uid == userId;
        }

        // Quests are readable by all authenticated users, but only writable by admin
        match /quests/{questId} {
          allow read: if isAuthenticated();
          allow write: if false;
        }

        // User progress can only be read/written by the owner
        match /userProgress/{userId} {
          allow read, write: if isAuthenticated() && isOwner(userId);
        }

        // User rewards can only be read/written by the owner
        match /userRewards/{userId} {
          allow read, write: if isAuthenticated() && isOwner(userId);
        }

        // User collections can only be read/written by the owner
        match /userCollections/{userId} {
          allow read, write: if isAuthenticated() && isOwner(userId);
        }
      }
    }
    `);

    console.log('Database initialization completed successfully!');
  } catch (error) {
    console.error('Error initializing database:', error);
  }
}

// Run the initialization
initializeDatabase(); 