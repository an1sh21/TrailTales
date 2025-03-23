const express = require('express');
const router = express.Router();
const { db, admin } = require('../config/firebase');
const { verifyToken } = require('../middleware/auth');
const questController = require('../controllers/questController');
const { getUserStats } = require('../controllers/userStatsController');

// Test route without authentication
router.get('/test', (req, res) => {
  res.json({ message: 'Quest routes working' });
});

// Get user statistics with authentication
router.get('/stats', verifyToken, getUserStats);

// Get all quests
router.get('/', verifyToken, async (req, res) => {
  try {
    const questsRef = db.collection('quests');
    const snapshot = await questsRef.get();
    
    if (snapshot.empty) {
      return res.json({ quests: [] });
    }
    
    const quests = [];
    snapshot.forEach(doc => {
      quests.push({
        id: doc.id,
        ...doc.data()
      });
    });
    
    res.json({ quests });
  } catch (error) {
    console.error('Error fetching quests:', error);
    res.status(500).json({ error: 'Failed to fetch quests' });
  }
});

// Get quest by ID
router.get('/:questId', verifyToken, async (req, res) => {
  try {
    const questRef = db.collection('quests').doc(req.params.questId);
    const doc = await questRef.get();
    
    if (!doc.exists) {
      return res.status(404).json({ error: 'Quest not found' });
    }
    
    res.json({
      id: doc.id,
      ...doc.data()
    });
  } catch (error) {
    console.error('Error fetching quest:', error);
    res.status(500).json({ error: 'Failed to fetch quest' });
  }
});

// Start a quest
router.post('/:questId/start', verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    const questId = req.params.questId;
    
    // Check if quest exists
    const questRef = db.collection('quests').doc(questId);
    const questDoc = await questRef.get();
    
    if (!questDoc.exists) {
      return res.status(404).json({ error: 'Quest not found' });
    }
    
    // Get or create user progress document
    const userProgressRef = db.collection('userProgress').doc(userId);
    const userProgressDoc = await userProgressRef.get();
    
    if (!userProgressDoc.exists) {
      // Create new progress document
      await userProgressRef.set({
        activeQuests: [questId],
        completedQuests: [],
        completedChallenges: [],
        lastActivity: admin.firestore.FieldValue.serverTimestamp()
      });
    } else {
      // Update existing document
      const userData = userProgressDoc.data();
      const activeQuests = userData.activeQuests || [];
      
      // Check if quest is already active
      if (activeQuests.includes(questId)) {
        return res.json({ message: 'Quest already started' });
      }
      
      // Add quest to active quests
      await userProgressRef.update({
        activeQuests: [...activeQuests, questId],
        lastActivity: admin.firestore.FieldValue.serverTimestamp()
      });
    }
    
    res.json({ message: 'Quest started successfully' });
  } catch (error) {
    console.error('Error starting quest:', error);
    res.status(500).json({ error: 'Failed to start quest' });
  }
});

// Quest-related routes
router.post('/start', verifyToken, questController.startQuest);
router.post('/collect-token', verifyToken, questController.collectToken);

module.exports = router; 