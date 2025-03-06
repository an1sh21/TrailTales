const express = require('express');
const router = express.Router();
const { db } = require('../config/firebase');
const { verifyToken } = require('../middleware/auth');

// Get all legends
router.get('/', verifyToken, async (req, res) => {
  try {
    const legendsRef = await db.collection('legends').get();
    
    const legends = [];
    legendsRef.forEach(doc => {
      legends.push({
        id: doc.id,
        ...doc.data()
      });
    });

    res.json(legends);
  } catch (error) {
    console.error('Error fetching legends:', error);
    res.status(500).json({ error: 'Failed to fetch legends' });
  }
});

// Trade legends between users
router.post('/trade', verifyToken, async (req, res) => {
  try {
    const { legendId, targetUserId } = req.body;
    const userId = req.user.uid;

    await db.runTransaction(async (transaction) => {
      const userRef = db.collection('users').doc(userId);
      const targetUserRef = db.collection('users').doc(targetUserId);
      
      const [user, targetUser] = await Promise.all([
        transaction.get(userRef),
        transaction.get(targetUserRef)
      ]);

      const userLegends = user.data().legends || [];
      const targetUserLegends = targetUser.data().legends || [];

      // Remove legend from user
      const updatedUserLegends = userLegends.filter(legend => legend.id !== legendId);
      // Add legend to target user
      const legendToTrade = userLegends.find(legend => legend.id === legendId);
      targetUserLegends.push(legendToTrade);

      transaction.update(userRef, { legends: updatedUserLegends });
      transaction.update(targetUserRef, { legends: targetUserLegends });
    });

    res.json({ message: 'Legend traded successfully' });
  } catch (error) {
    console.error('Error trading legend:', error);
    res.status(500).json({ error: 'Failed to trade legend' });
  }
});

module.exports = router; 