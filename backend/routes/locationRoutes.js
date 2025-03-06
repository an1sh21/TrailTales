const express = require('express');
const router = express.Router();
const { checkProximity, getAllLocations } = require('../controllers/locationController');

router.get('/check', checkProximity);
router.get('/all', getAllLocations);

module.exports = router;