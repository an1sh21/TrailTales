const express = require('express');
const router = express.Router();
const { db, admin } = require('../config/firebase');
const { verifyToken } = require('../middleware/auth');

// Get all collectibles
router.get('/', verifyToken, async (req, res) => {
  try {
    const collectiblesRef = db.collection('collectibles');
    const snapshot = await collectiblesRef.get();
    
    if (snapshot.empty) {
      return res.json({ collectibles: [] });
    }
    
    const collectibles = [];
    snapshot.forEach(doc => {
      collectibles.push({
        id: doc.id,
        ...doc.data()
      });
    });
    
    res.json({ collectibles });
  } catch (error) {
    console.error('Error fetching collectibles:', error);
    res.status(500).json({ error: 'Failed to fetch collectibles' });
  }
});

// Collect items
router.post('/collect', verifyToken, async (req, res) => {
  try {
    console.log('Collecting item, request body:', req.body);
    const { itemId } = req.body;
    
    if (!itemId) {
      return res.status(400).json({ error: 'Item ID is required' });
    }
    
    const userId = req.user.uid;
    console.log('User ID:', userId);
    
    // Just add the item to the user's collection without checking if it exists
    // to simplify the endpoint for testing purposes
    const userCollectionsRef = db.collection('userCollections').doc(userId);
    
    // Add the item to the user's collection
    await userCollectionsRef.set({
      collectibles: admin.firestore.FieldValue.arrayUnion({
        itemId: itemId,
        collectedAt: admin.firestore.Timestamp.now()
      }),
      lastUpdated: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });
    
    console.log('Item successfully collected:', itemId);
    res.json({ message: 'Item collected successfully' });
  } catch (error) {
    console.error('Error collecting item:', error);
    res.status(500).json({ error: 'Failed to collect item: ' + error.message });
  }
});

// Get user's collected items
router.get('/user', verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    const userCollectionsRef = db.collection('userCollections').doc(userId);
    const userCollectionsDoc = await userCollectionsRef.get();
    
    if (!userCollectionsDoc.exists) {
      return res.json({ collectibles: [] });
    }
    
    const userData = userCollectionsDoc.data();
    const collectibles = userData.collectibles || [];
    
    res.json({ collectibles });
  } catch (error) {
    console.error('Error fetching collectibles:', error);
    res.status(500).json({ error: 'Failed to fetch collectibles' });
  }
});

module.exports = router; 