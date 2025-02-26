const express = require('express');
const router = express.Router();
const { db } = require('../config/firebase');
const { authenticateUser } = require('../middleware/auth');

router.get('/:locationId', authenticateUser, async (req, res) => {
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
    res.status(500).json({ error: error.message });
  }
});

router.post('/unlock', authenticateUser, async (req, res) => {
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
    res.status(500).json({ error: error.message });
  }
});

module.exports = router; 