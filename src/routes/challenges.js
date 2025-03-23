const express = require('express');
const router = express.Router();
const challengeController = require('../controllers/challengeController');
const { verifyToken } = require('../middleware/auth');

router.get('/quests/:questId/challenges', verifyToken, challengeController.getQuestChallenges);

module.exports = router; 