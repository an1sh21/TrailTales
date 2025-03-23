const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middleware/auth');
const { getUserStats } = require('../controllers/userStatsController');
const { db } = require('../config/firebase');
const admin = require('firebase-admin');

// Test route without authentication
router.get('/test', (req, res) => {
  res.json({ message: 'User routes working' });
});

// Get user profile
router.get('/profile', verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    const userDoc = await db.collection('users').doc(userId).get();
    
    if (!userDoc.exists) {
      return res.status(404).json({ message: 'User profile not found' });
    }
    
    const userData = userDoc.data();
    
    // Remove sensitive information if present
    const { password, ...profileData } = userData;
    
    res.json({
      id: userId,
      ...profileData
    });
  } catch (error) {
    console.error('Error fetching user profile:', error);
    res.status(500).json({ message: 'Failed to fetch user profile' });
  }
});

// Update user profile
router.put('/profile', verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    const userRef = db.collection('users').doc(userId);
    const userDoc = await userRef.get();
    
    if (!userDoc.exists) {
      return res.status(404).json({ message: 'User profile not found' });
    }
    
    // Only allow updating certain fields
    const allowedFields = ['displayName', 'username', 'avatar'];
    const updates = {};
    
    for (const field of allowedFields) {
      if (req.body[field] !== undefined) {
        updates[field] = req.body[field];
      }
    }
    
    if (Object.keys(updates).length === 0) {
      return res.status(400).json({ message: 'No valid fields to update' });
    }
    
    // Add timestamp
    updates.updatedAt = admin.firestore.FieldValue.serverTimestamp();
    
    await userRef.update(updates);
    
    res.json({ 
      message: 'Profile updated successfully',
      updates
    });
  } catch (error) {
    console.error('Error updating user profile:', error);
    res.status(500).json({ message: 'Failed to update user profile' });
  }
});

// Get user statistics with authentication
router.get('/stats', verifyToken, getUserStats);

// Get user rewards
router.get('/rewards', verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    const userRewardsRef = await db.collection('userRewards').doc(userId).get();
    
    if (!userRewardsRef.exists) {
      return res.json({
        gold: 0,
        diamonds: 0
      });
    }
    
    res.json(userRewardsRef.data());
  } catch (error) {
    console.error('Error fetching user rewards:', error);
    res.status(500).json({ message: 'Failed to fetch user rewards' });
  }
});

// Get user collections
router.get('/collections', verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    const userCollectionsRef = await db.collection('userCollections').doc(userId).get();
    
    if (!userCollectionsRef.exists) {
      return res.json({
        tokens: []
      });
    }
    
    res.json(userCollectionsRef.data());
  } catch (error) {
    console.error('Error fetching user collections:', error);
    res.status(500).json({ message: 'Failed to fetch user collections' });
  }
});

module.exports = router; 