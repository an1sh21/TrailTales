const express = require('express');
const router = express.Router();
const { db, admin } = require('../config/firebase');
const { verifyToken } = require('../middleware/auth');

// Get stories for a location
router.get('/:locationId', verifyToken, async (req, res) => {
  try {
    const { locationId } = req.params;
    const storiesRef = await db.collection('stories')
      .where('locationId', '==', locationId)
      .get();
    
    const stories = [];
    storiesRef.forEach(doc => {
      stories.push({
        id: doc.id,
        ...doc.data()
      });
    });

    res.json(stories);
  } catch (error) {
    console.error('Error fetching stories:', error);
    res.status(500).json({ error: 'Failed to fetch stories' });
  }
});

// Unlock a story
router.post('/unlock', verifyToken, async (req, res) => {
  try {
    const { storyId } = req.body;
    const userId = req.user.uid;

    await db.runTransaction(async (transaction) => {
      const userRef = db.collection('users').doc(userId);
      const user = await transaction.get(userRef);
      
      const collection = user.data().collection || [];
      collection.push({
        storyId,
        unlockedAt: admin.firestore.FieldValue.serverTimestamp(),
        type: 'story'
      });
      
      transaction.update(userRef, { collection });
    });

    res.json({ message: 'Story unlocked successfully' });
  } catch (error) {
    console.error('Error unlocking story:', error);
    res.status(500).json({ error: 'Failed to unlock story' });
  }
});

module.exports = router; 