const { db } = require('../config/firebase');
const admin = require('firebase-admin');

// Helper function to calculate distance between coordinates
const calculateDistance = (lat1, lon1, lat2, lon2) => {
  // Simple implementation of distance calculation
  const R = 6371; // Radius of the earth in km
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180; 
  const a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
    Math.sin(dLon/2) * Math.sin(dLon/2); 
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
  const d = R * c; // Distance in km
  return d * 1000; // Convert to meters
};

exports.getAllQuests = async (req, res) => {
  try {
    const questsSnapshot = await db.collection('quests')
      .where('isActive', '==', true)
      .get();
    
    const quests = [];
    questsSnapshot.forEach(doc => {
      quests.push({
        id: doc.id,
        ...doc.data()
      });
    });
    
    res.json(quests);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.getQuestById = async (req, res) => {
  try {
    const { questId } = req.params;

    const questDoc = await db.collection('quests').doc(questId).get();

    if (!questDoc.exists) {
      return res.status(404).json({ message: 'Quest not found' });
    }

    const quest = questDoc.data();

    res.json({
      id: questDoc.id,
      title: quest.title,
      description: quest.description,
      difficulty: quest.difficulty,
      tokenLocations: quest.tokenLocations || [],
      path: quest.path || [],
      siteMap: quest.siteMap || null
    });

  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.startQuest = async (req, res) => {
  try {
    const { questId } = req.body;
    const userId = req.user.uid;

    // Check if quest exists and get its details
    const questDoc = await db.collection('quests').doc(questId).get();
    if (!questDoc.exists) {
      return res.status(404).json({ message: 'Quest not found' });
    }
    const quest = questDoc.data();

    // Check if quest is already in progress
    const existingProgress = await db.collection('userProgress')
      .where('userId', '==', userId)
      .where('questId', '==', questId)
      .where('status', '==', 'in_progress')
      .get();

    if (!existingProgress.empty) {
      return res.status(400).json({ message: 'Quest already in progress' });
    }

    // Extract token IDs from tokenLocations
    const lockedTokens = Array.isArray(quest.tokenLocations) 
      ? quest.tokenLocations
        .filter(token => token && typeof token.id === 'string')
        .map(token => token.id)
      : [];

    // Create new quest progress with locked tokens
    const newProgress = await db.collection('userProgress').add({
      userId,
      questId,
      collectedTokens: [],
      completedLocations: [],
      currentStep: 0,
      lockedTokens,
      startedAt: admin.firestore.FieldValue.serverTimestamp(),
      status: 'in_progress'
    });

    // Return quest details with first step
    res.status(201).json({
      id: newProgress.id,
      userId,
      questId,
      status: 'in_progress',
      questDetails: {
        title: quest.title,
        description: quest.description,
        difficulty: quest.difficulty,
        totalSteps: quest.tokenLocations ? quest.tokenLocations.length : 0,
        currentStep: 0,
        firstStep: quest.tokenLocations && quest.tokenLocations[0] ? {
          location: quest.tokenLocations[0].location,
          description: quest.tokenLocations[0].description,
          waypoint: quest.tokenLocations[0].location.coordinates
        } : null,
        path: quest.path || [],
        lockedTokens
      }
    });
  } catch (error) {
    console.error('Error starting quest:', error);
    res.status(500).json({ message: error.message });
  }
};

exports.collectToken = async (req, res) => {
  try {
    const { questId, tokenId, userLocation } = req.body;
    const userId = req.user.uid;

    // Get quest progress
    const progressRef = db.collection('userProgress')
      .where('userId', '==', userId)
      .where('questId', '==', questId)
      .where('status', '==', 'in_progress');

    const progressDoc = await progressRef.get();
    if (progressDoc.empty) {
      return res.status(404).json({ message: 'Quest progress not found' });
    }

    const progress = progressDoc.docs[0].data();

    // Check if token is locked
    if (progress.lockedTokens.includes(tokenId)) {
      return res.status(403).json({ message: 'This token is currently locked' });
    }

    const tokenDoc = await db.collection('storyTokens').doc(tokenId).get();
    if (!tokenDoc.exists) {
      return res.status(404).json({ message: 'Token not found' });
    }
    const token = tokenDoc.data();

    // Check if user is at the correct location
    const distance = calculateDistance(
      userLocation.latitude,
      userLocation.longitude,
      token.location.coordinates.latitude,
      token.location.coordinates.longitude
    );

    if (distance > token.location.radius) {
      return res.status(400).json({ message: 'You are too far from the token location' });
    }

    // Update progress
    await db.collection('userProgress').doc(progressDoc.docs[0].id).update({
      collectedTokens: admin.firestore.FieldValue.arrayUnion(tokenId),
      completedLocations: admin.firestore.FieldValue.arrayUnion(token.location),
      currentStep: progress.currentStep + 1,
      lockedTokens: admin.firestore.FieldValue.arrayRemove(tokenId)
    });

    res.json({ 
      message: 'Token collected successfully',
      nextStep: progress.currentStep + 1
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}; 