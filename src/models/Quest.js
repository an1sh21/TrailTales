// Data structure for Quests
const questStructure = {
  title: String,
  description: String,
  difficulty: String, // 'easy', 'medium', 'hard'
  isActive: Boolean,
  tokenLocations: [
    {
      id: String,
      type: String, // 'token' or 'coin'
      location: {
        coordinates: {
          latitude: Number,
          longitude: Number
        },
        radius: Number
      },
      description: String,
      arMarker: {
        type: String,
        data: String
      },
      collectInstructions: String // 'Tap on the Coin' or 'Tap on the Token'
    }
  ],
  path: [
    {
      latitude: Number,
      longitude: Number
    }
  ],
  totalSteps: Number,
  rewards: {
    easy: {
      gold: 100
    },
    medium: {
      gold: 250
    },
    hard: {
      gold: 500,
      includesDiamond: true
    }
  }
};

module.exports = questStructure;