const { body, validationResult } = require('express-validator');

// Validation rules for updating user profile
exports.validateUpdateProfile = [
  body('displayName')
    .optional()
    .isLength({ min: 2, max: 30 })
    .withMessage('Display name must be between 2 and 30 characters'),
  body('preferences.darkMode')
    .optional()
    .isBoolean()
    .withMessage('Dark mode must be a boolean value'),
  body('preferences.notificationsEnabled')
    .optional()
    .isBoolean()
    .withMessage('Notifications enabled must be a boolean value'),
  body('preferences.musicVolume')
    .optional()
    .isFloat({ min: 0, max: 1 })
    .withMessage('Music volume must be between 0 and 1')
];

// Validation rules for password reset request
exports.validatePasswordResetRequest = [
  body('email')
    .isEmail()
    .withMessage('Please provide a valid email address')
];

// Validation rules for password reset
exports.validatePasswordReset = [
  body('password')
    .isLength({ min: 6 })
    .withMessage('Password must be at least 6 characters long')
    .matches(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,}$/)
    .withMessage('Password must include one lowercase character, one uppercase character, a number, and a special character')
];

// Check validation results and return errors if any
exports.checkValidationResult = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
}; 