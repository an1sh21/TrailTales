const express = require('express');
const router = express.Router();
const { authenticateUser } = require('../middleware/auth');
const collectibleController = require('../controllers/collectibleController');

router.get('/nearby', authenticateUser, collectibleController.getNearbyCollectibles);
router.post('/collect/:collectibleId', authenticateUser, collectibleController.collectItem);

module.exports = router; 