const User = require('../models/User');
const admin = require('../config/firebase-config');
const sendEmail = require('../utils/emailService');

// @desc    Get current user profile
// @route   GET /api/users/me
// @access  Private
exports.getCurrentUser = async (req, res, next) => {
  try {
    const user = await User.findById(req.user.id).select('-resetPasswordToken -resetPasswordExpire');
    
    res.status(200).json({
      success: true,
      data: user
    });
  } catch (error) {
    next(error);
  }
};

// @desc    Update user profile
// @route   PUT /api/users/profile
// @access  Private
exports.updateProfile = async (req, res, next) => {
  try {
    const { displayName, preferences } = req.body;
    
    // Prepare update object
    const updateData = {};
    
    // Only add fields that are provided
    if (displayName) updateData.displayName = displayName;
    if (preferences) {
      updateData.preferences = {};
      if (preferences.darkMode !== undefined) updateData.preferences.darkMode = preferences.darkMode;
      if (preferences.notificationsEnabled !== undefined) updateData.preferences.notificationsEnabled = preferences.notificationsEnabled;
      if (preferences.musicVolume !== undefined) updateData.preferences.musicVolume = preferences.musicVolume;
    }
    
    // Update user in MongoDB
    const user = await User.findByIdAndUpdate(
      req.user.id,
      { $set: updateData },
      { new: true, runValidators: true }
    ).select('-resetPasswordToken -resetPasswordExpire');
    
    // Update displayName in Firebase if provided
    if (displayName) {
      await admin.auth().updateUser(req.user.firebaseId, {
        displayName: displayName
      });
    }
    
    res.status(200).json({
      success: true,
      data: user,
      message: 'Profile updated successfully'
    });
  } catch (error) {
    next(error);
  }
};

// @desc    Delete user account
// @route   DELETE /api/users/account
// @access  Private
exports.deleteAccount = async (req, res, next) => {
  try {
    // Delete user in Firebase
    await admin.auth().deleteUser(req.user.firebaseId);
    
    // Delete user in MongoDB
    await User.findByIdAndDelete(req.user.id);
    
    res.status(200).json({
      success: true,
      message: 'Account deleted successfully'
    });
  } catch (error) {
    next(error);
  }
};

// @desc    Link Google account
// @route   POST /api/users/link-google
// @access  Private
exports.linkGoogleAccount = async (req, res, next) => {
  try {
    const { idToken } = req.body;
    
    if (!idToken) {
      return res.status(400).json({
        success: false,
        message: 'Google ID token is required'
      });
    }
    
    // Link accounts in Firebase
    const credential = admin.auth.GoogleAuthProvider.credential(idToken);
    await admin.auth().updateUser(req.user.firebaseId, {
      providerToLink: credential
    });
    
    // Update user in MongoDB
    const user = await User.findByIdAndUpdate(
      req.user.id,
      { $set: { 'accountLinked.google': true } },
      { new: true }
    ).select('-resetPasswordToken -resetPasswordExpire');
    
    res.status(200).json({
      success: true,
      data: user,
      message: 'Google account linked successfully'
    });
  } catch (error) {
    next(error);
  }
}; 