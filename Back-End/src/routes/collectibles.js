const express = require('express');
const router = express.Router();
const { db, admin } = require('../config/firebase');
const { verifyToken } = require('../middleware/auth');

// Collect items
router.post('/collect', verifyToken, async (req, res) => {
  try {
    const { itemId } = req.body;
    const userId = req.user.uid;

    await db.runTransaction(async (transaction) => {
      const userRef = db.collection('users').doc(userId);
      const userDoc = await transaction.get(userRef);
      
      let userData = userDoc.exists ? userDoc.data() : { collection: [] };
      const collection = userData.collection || [];

      // Check if item is already collected
      const isAlreadyCollected = collection.some(item => item.itemId === itemId && item.type === 'collectible');
      if (isAlreadyCollected) {
        return res.json({ message: 'Item already collected' });
      }

      collection.push({
        itemId,
        collectedAt: admin.firestore.FieldValue.serverTimestamp(),
        type: 'collectible'
      });
      
      if (!userDoc.exists) {
        transaction.set(userRef, { collection });
      } else {
        transaction.update(userRef, { collection });
      }
    });

    res.json({ message: 'Item collected successfully' });
  } catch (error) {
    console.error('Error collecting item:', error);
    res.status(500).json({ error: 'Failed to collect item' });
  }
});

module.exports = router; 