const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { firebaseAuth } = require('../middleware/auth');
const { 
  validatePasswordResetRequest, 
  validatePasswordReset,
  checkValidationResult 
} = require('../utils/validators');

// Public routes (no authentication required)
router.post('/forgot-password', validatePasswordResetRequest, checkValidationResult, authController.forgotPassword);
router.put('/reset-password/:resetToken', validatePasswordReset, checkValidationResult, authController.resetPassword);

// Protected routes (require authentication)
router.put('/change-password', firebaseAuth, validatePasswordReset, checkValidationResult, authController.changePassword);

module.exports = router; 