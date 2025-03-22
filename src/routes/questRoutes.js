const express = require('express');
const router = express.Router();
const { authenticateToken } = require('../middleware/auth');
const questController = require('../controllers/questController');
const { getUserStats } = require('../controllers/userStatsController');

// Debug middleware to check the request
const debugMiddleware = (req, res, next) => {
  console.log('Debug middleware called');
  next();
};

// Test route without authentication
router.get('/test', (req, res) => {
  res.json({ message: 'Test route working' });
});

// Get user statistics with authentication
router.get('/stats', 
  (req, res, next) => {
    console.log('Before auth middleware');
    next();
  },
  authenticateToken,
  (req, res, next) => {
    console.log('After auth middleware');
    next();
  },
  getUserStats
);

// Add quest-related routes
router.get('/quests', authenticateToken, questController.getAllQuests);
router.post('/quests/start', authenticateToken, questController.startQuest);
router.post('/quests/collect-token', authenticateToken, questController.collectToken);
router.get('/quests/:questId/tokens', authenticateToken, questController.getSiteTokens);
router.post('/quests/collect-ar-token', authenticateToken, questController.collectTokenViaAR);

// New route to get a quest by ID
router.get('/quests/:questId', authenticateToken, questController.getQuestById);

module.exports = router;
