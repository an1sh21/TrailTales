const express = require('express');
const router = express.Router();
const challengeController = require('../controllers/challengeController');
const { authenticateToken } = require('../middleware/auth');  // ✅ Import correctly

router.get('/quests/:questId/challenges', authenticateToken, challengeController.getQuestChallenges); // ✅ Use the function

module.exports = router;
