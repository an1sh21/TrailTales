const express = require('express');
const router = express.Router();
const { updateProgress, getUserProgress, syncOfflineProgress } = require('../controllers/progressController');

router.post('/update', updateProgress); // Track completed sites
router.get('/user', getUserProgress); // Get all completed sites for a user
router.post('/sync', syncOfflineProgress); // Sync offline progress

module.exports = router;