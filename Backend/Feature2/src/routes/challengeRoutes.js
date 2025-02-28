const express = require('express');
const router = express.Router();
const challengeController = require('../controllers/challengeController');
const auth = require('../middleware/auth');

router.get('/quests/:questId/challenges', auth, challengeController.getQuestChallenges);

module.exports = router; 