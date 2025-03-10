const crypto = require('crypto');
const User = require('../models/User');
const admin = require('../config/firebase-config');
const sendEmail = require('../utils/emailService');

// @desc    Request password reset
// @route   POST /api/auth/forgot-password
// @access  Public
exports.forgotPassword = async (req, res, next) => {
  try {
    const { email } = req.body;

    // Find user by email
    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'There is no user with that email'
      });
    }

    // Generate reset token
    const resetToken = user.getResetPasswordToken();

    // Save the updated user with reset token
    await user.save({ validateBeforeSave: false });

    // Create reset URL
    const resetUrl = `${req.protocol}://${req.get('host')}/api/auth/reset-password/${resetToken}`;

    // Create email HTML content
    const html = `
      <h3>Password Reset Request</h3>
      <p>You have requested to reset your password. Please click the link below to reset your password:</p>
      <a href="${resetUrl}" target="_blank">Reset Password</a>
      <p>If you did not request this reset, please ignore this email and your password will remain unchanged.</p>
      <p>This link is valid for 10 minutes.</p>
    `;

    try {
      // Send email
      await sendEmail({
        email: user.email,
        subject: 'Trail Tales Password Reset',
        html
      });

      res.status(200).json({
        success: true,
        message: 'Password reset email sent'
      });
    } catch (err) {
      // If email sending fails, clear the reset tokens and save
      user.resetPasswordToken = undefined;
      user.resetPasswordExpire = undefined;

      await user.save({ validateBeforeSave: false });

      return res.status(500).json({
        success: false,
        message: 'Email could not be sent'
      });
    }
  } catch (error) {
    next(error);
  }
};

// @desc    Reset password
// @route   PUT /api/auth/reset-password/:resetToken
// @access  Public
exports.resetPassword = async (req, res, next) => {
  try {
    // Get hashed token
    const resetPasswordToken = crypto
      .createHash('sha256')
      .update(req.params.resetToken)
      .digest('hex');

    // Find user by reset token and check if token is still valid
    const user = await User.findOne({
      resetPasswordToken,
      resetPasswordExpire: { $gt: Date.now() }
    });

    if (!user) {
      return res.status(400).json({
        success: false,
        message: 'Invalid or expired token'
      });
    }

    // Update password in Firebase
    try {
      await admin.auth().updateUser(user.firebaseId, {
        password: req.body.password
      });
    } catch (firebaseError) {
      return res.status(400).json({
        success: false,
        message: firebaseError.message || 'Could not update password in Firebase'
      });
    }

    // Clear reset token fields
    user.resetPasswordToken = undefined;
    user.resetPasswordExpire = undefined;

    // Save the updated user
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Password reset successful'
    });
  } catch (error) {
    next(error);
  }
};

// @desc    Change password
// @route   PUT /api/auth/change-password
// @access  Private
exports.changePassword = async (req, res, next) => {
  try {
    const { currentPassword, newPassword } = req.body;

    if (!currentPassword || !newPassword) {
      return res.status(400).json({
        success: false,
        message: 'Please provide current and new password'
      });
    }

    try {
      // Update password in Firebase
      await admin.auth().updateUser(req.user.firebaseId, {
        password: newPassword
      });

      res.status(200).json({
        success: true,
        message: 'Password changed successfully'
      });
    } catch (firebaseError) {
      return res.status(400).json({
        success: false,
        message: firebaseError.message || 'Could not update password'
      });
    }
  } catch (error) {
    next(error);
  }
}; 