const express = require('express');
const router = express.Router();
const { db } = require('../config/firebase');
const { admin } = require('../config/firebase');

router.post('/register', async (req, res) => {
  try {
    const { uid, email } = req.body;
    
    await db.collection('users').doc(uid).set({
      email,
      collection: [],
      legends: [],
      coins: 0,
      tokens: 0,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    res.status(201).json({ message: 'User registered successfully' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router; 