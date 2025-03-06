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
      const user = await transaction.get(userRef);
      
      const collection = user.data().collection || [];
      collection.push({
        itemId,
        collectedAt: admin.firestore.FieldValue.serverTimestamp(),
        type: 'collectible'
      });
      
      transaction.update(userRef, { collection });
    });

    res.json({ message: 'Item collected successfully' });
  } catch (error) {
    console.error('Error collecting item:', error);
    res.status(500).json({ error: 'Failed to collect item' });
  }
});

module.exports = router; 