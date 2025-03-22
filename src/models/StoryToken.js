const mongoose = require('mongoose');

const storyTokenSchema = new mongoose.Schema({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  arModel: {
    type: String,  // URL to AR model
    required: true,
  },
  collectibleCard: {
    image: String,
    rarity: String,
    description: String
  },
  location: {
    coordinates: {
      latitude: Number,
      longitude: Number,
    },
    radius: Number  // Detection radius in meters
  }
});

module.exports = mongoose.model('StoryToken', storyTokenSchema); 