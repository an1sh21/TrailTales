const { db } = require('../config/firebase');
const admin = require('firebase-admin');

async function addSampleData() {
  try {
    // 1. Add more sample quests
    const quests = [
      {
        title: "Easy Temple Quest",
        description: "A simple quest to collect temple tokens",
        difficulty: "easy",
        isActive: true,
        tokenLocations: [
          {
            id: "easy_token1",
            type: "token",
            location: {
              coordinates: { latitude: 12.345, longitude: 67.890 },
              radius: 50,
              siteId: "easy_site1"
            },
            description: "Easy Temple Token",
            rarity: "common",
            arMarker: {
              type: "image",
              data: "easy_marker_data"
            }
          }
        ],
        path: [
          { latitude: 12.345, longitude: 67.890 }
        ]
      },
      {
        title: "Hard Temple Quest",
        description: "A challenging quest for experienced explorers",
        difficulty: "hard",
        isActive: true,
        tokenLocations: [
          {
            id: "hard_token1",
            type: "token",
            location: {
              coordinates: { latitude: 12.346, longitude: 67.891 },
              radius: 50,
              siteId: "hard_site1"
            },
            description: "Hard Temple Token",
            rarity: "legendary",
            arMarker: {
              type: "image",
              data: "hard_marker_data"
            }
          }
        ],
        path: [
          { latitude: 12.346, longitude: 67.891 }
        ]
      }
    ];

    for (const quest of quests) {
      const questRef = await db.collection('quests').add(quest);
      console.log(`Created quest: ${quest.title} with ID: ${questRef.id}`);
    }

    // 2. Add sample user progress
    const sampleUserId = "test_user_123"; // Replace with actual test user ID
    const progressData = {
      userId: sampleUserId,
      questId: "quest_id_here", // Replace with actual quest ID
      status: "in_progress",
      collectedTokens: [],
      completedLocations: [],
      currentStep: 0,
      lockedTokens: ["token1", "token2"],
      startedAt: admin.firestore.FieldValue.serverTimestamp(),
      siteProgress: {
        site1: { collected: 0, total: 2 }
      }
    };

    const progressRef = await db.collection('userProgress').add(progressData);
    console.log('Created user progress with ID:', progressRef.id);

    // 3. Add sample user rewards
    const rewardsData = {
      gold: 0,
      completedQuests: []
    };

    await db.collection('userRewards').doc(sampleUserId).set(rewardsData);
    console.log('Created user rewards for user:', sampleUserId);

    // 4. Add sample user collection
    const collectionData = {
      tokens: []
    };

    await db.collection('userCollections').doc(sampleUserId).set(collectionData);
    console.log('Created user collection for user:', sampleUserId);

    console.log('Sample data added successfully!');
  } catch (error) {
    console.error('Error adding sample data:', error);
  }
}

// Run the sample data addition
addSampleData(); 