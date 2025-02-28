const express = require('express');
const router = express.Router();
const { authenticateUser } = require('../middleware/auth');
const legendController = require('../controllers/legendController');

router.post('/trade/:legendId/:targetUserId', authenticateUser, legendController.tradeLegend);

module.exports = router; 