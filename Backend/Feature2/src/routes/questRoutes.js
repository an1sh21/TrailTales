const express = require('express');
const router = express.Router();
const questController = require('../controllers/questController');
const auth = require('../middleware/auth');

router.get('/quests', questController.getAllQuests);
router.post('/quests/start', auth, questController.startQuest);
router.post('/quests/collect-token', auth, questController.collectToken);

module.exports = router; 