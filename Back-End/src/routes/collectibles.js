const express = require('express');
const router = express.Router();
const { db } = require('../config/firebase');
const { authenticateUser } = require('../middleware/auth');
const { validateCollectible, validate } = require('../middleware/validator');

router.post('/collect', authenticateUser, validateCollectible, validate, async (req, res) => {
  try {
    const { collectibleId, locationType } = req.body;
    const userId = req.user.uid;
    
    await db.runTransaction(async (transaction) => {
      const userRef = db.collection('users').doc(userId);
      const collectibleRef = db.collection('collectibles').doc(collectibleId);
      
      const [user, collectible] = await Promise.all([
        transaction.get(userRef),
        transaction.get(collectibleRef)
      ]);
      
      if (!collectible.exists) {
        throw new Error('Collectible not found');
      }
      
      const userData = user.data();
      const collection = userData.collection || [];
      
      collection.push({
        collectibleId,
        collectedAt: admin.firestore.FieldValue.serverTimestamp(),
        type: locationType
      });
      
      if (locationType === 'coin') {
        transaction.update(userRef, { 
          collection,
          coins: userData.coins + 1
        });
      } else if (locationType === 'token') {
        transaction.update(userRef, { 
          collection,
          tokens: userData.tokens + 1
        });
      } else {
        transaction.update(userRef, { collection });
      }
    });
    
    res.json({ message: 'Collectible acquired successfully' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router; 