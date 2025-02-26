const express = require('express');
const router = express.Router();
const { db } = require('../config/firebase');
const { authenticateUser } = require('../middleware/auth');
const { validateTrade, validate } = require('../middleware/validator');

router.get('/', authenticateUser, async (req, res) => {
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
    res.status(500).json({ error: error.message });
  }
});

router.post('/trade', authenticateUser, validateTrade, validate, async (req, res) => {
  try {
    const { toUserId, legendId } = req.body;
    const fromUserId = req.user.uid;
    
    await db.runTransaction(async (transaction) => {
      const fromUserRef = db.collection('users').doc(fromUserId);
      const toUserRef = db.collection('users').doc(toUserId);
      const legendRef = db.collection('legends').doc(legendId);
      
      const [fromUser, toUser, legend] = await Promise.all([
        transaction.get(fromUserRef),
        transaction.get(toUserRef),
        transaction.get(legendRef)
      ]);
      
      if (!legend.exists) {
        throw new Error('Legend not found');
      }
      
      const fromUserData = fromUser.data();
      if (!fromUserData.legends.includes(legendId)) {
        throw new Error('You do not own this legend');
      }
      
      transaction.update(fromUserRef, {
        legends: fromUserData.legends.filter(id => id !== legendId)
      });
      
      const toUserData = toUser.data();
      transaction.update(toUserRef, {
        legends: [...toUserData.legends, legendId]
      });
      
      transaction.update(legendRef, {
        currentOwner: toUserId
      });
      
      // Record transaction
      transaction.create(db.collection('transactions').doc(), {
        fromUserId,
        toUserId,
        legendId,
        timestamp: admin.firestore.FieldValue.serverTimestamp()
      });
    });
    
    res.json({ message: 'Trade completed successfully' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router; 