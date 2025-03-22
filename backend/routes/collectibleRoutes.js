const express = require('express');
const router = express.Router();
const { getAvailableCollectibles, collectItem, getUserCollectibles } = require('../controllers/collectibleController');

router.get('/available', getAvailableCollectibles); // Get available collectibles at a location
router.post('/collect', collectItem); // Collect an AR item
router.get('/user', getUserCollectibles); // Get all collected items for a user

module.exports = router;