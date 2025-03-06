const mongoose = require('mongoose');

const userProgressSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  questId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Quest',
    required: true
  },
  collectedTokens: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'StoryToken'
  }],
  completedLocations: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Location'
  }],
  startedAt: {
    type: Date,
    default: Date.now
  },
  completedAt: {
    type: Date
  },
  status: {
    type: String,
    enum: ['in_progress', 'completed', 'abandoned'],
    default: 'in_progress'
  }
});

module.exports = mongoose.model('UserProgress', userProgressSchema); 