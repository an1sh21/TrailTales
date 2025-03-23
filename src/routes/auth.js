const express = require('express');
const router = express.Router();
const { admin } = require('../config/firebase');
const { body } = require('express-validator');
const { validate } = require('../middleware/validator');
const { verifyToken } = require('../middleware/auth');

// Test endpoint
router.get('/test', (req, res) => {
  res.json({ message: 'Auth routes working!' });
});

// Verify token validity
router.get('/verify', verifyToken, (req, res) => {
  res.json({ 
    authenticated: true, 
    user: {
      uid: req.user.uid,
      email: req.user.email
    } 
  });
});

// Add more auth-related endpoints here

module.exports = router; 