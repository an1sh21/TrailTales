const express = require('express');
const router = express.Router();
const { authenticateToken } = require('../middleware/auth');
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

module.exports = router; 