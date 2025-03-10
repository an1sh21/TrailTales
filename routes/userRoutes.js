const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const { firebaseAuth } = require('../middleware/auth');
const { validateUpdateProfile, checkValidationResult } = require('../utils/validators');

// Protected routes (require authentication)
router.use(firebaseAuth);

// Get current user profile
router.get('/me', userController.getCurrentUser);

// Update user profile
router.put('/profile', validateUpdateProfile, checkValidationResult, userController.updateProfile);

// Delete user account
router.delete('/account', userController.deleteAccount);

// Link Google account
router.post('/link-google', userController.linkGoogleAccount);

module.exports = router; 