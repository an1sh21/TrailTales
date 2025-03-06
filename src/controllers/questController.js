const { db } = require('../config/firebase');
const admin = require('firebase-admin');

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

exports.startQuest = async (req, res) => {
  try {
    const { questId } = req.body;
    const userId = req.user.uid;

    const existingProgress = await db.collection('userProgress')
      .where('userId', '==', userId)
      .where('questId', '==', questId)
      .where('status', '==', 'in_progress')
      .get();

    if (!existingProgress.empty) {
      return res.status(400).json({ message: 'Quest already in progress' });
    }

    const newProgress = await db.collection('userProgress').add({
      userId,
      questId,
      collectedTokens: [],
      completedLocations: [],
      startedAt: admin.firestore.FieldValue.serverTimestamp(),
      status: 'in_progress'
    });

    res.status(201).json({
      id: newProgress.id,
      userId,
      questId,
      status: 'in_progress'
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.collectToken = async (req, res) => {
  try {
    const { questId, tokenId, userLocation } = req.body;
    const userId = req.user.uid;

    const tokenDoc = await db.collection('storyTokens').doc(tokenId).get();
    if (!tokenDoc.exists) {
      return res.status(404).json({ message: 'Token not found' });
    }
    const token = tokenDoc.data();

    const distance = calculateDistance(
      userLocation.latitude,
      userLocation.longitude,
      token.location.coordinates.latitude,
      token.location.coordinates.longitude
    );

    if (distance > token.location.radius) {
      return res.status(400).json({ message: 'You are too far from the token location' });
    }

    const progressRef = db.collection('userProgress')
      .where('userId', '==', userId)
      .where('questId', '==', questId)
      .where('status', '==', 'in_progress');

    const progressDoc = await progressRef.get();
    if (progressDoc.empty) {
      return res.status(404).json({ message: 'Quest progress not found' });
    }

    await db.collection('userProgress').doc(progressDoc.docs[0].id).update({
      collectedTokens: admin.firestore.FieldValue.arrayUnion(tokenId),
      completedLocations: admin.firestore.FieldValue.arrayUnion(token.location)
    });

    res.json({ message: 'Token collected successfully' });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

function calculateDistance(lat1, lon1, lat2, lon2) {
  const R = 6371e3; // Earth's radius in meters
  const φ1 = lat1 * Math.PI/180;
  const φ2 = lat2 * Math.PI/180;
  const Δφ = (lat2-lat1) * Math.PI/180;
  const Δλ = (lon2-lon1) * Math.PI/180;

  const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
          Math.cos(φ1) * Math.cos(φ2) *
          Math.sin(Δλ/2) * Math.sin(Δλ/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

  return R * c; // Distance in meters
}