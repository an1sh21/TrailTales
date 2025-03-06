// Data structure for Quests
const questStructure = {
  title: String,
  description: String,
  difficulty: String, // 'easy', 'medium', 'hard'
  theme: String,
  locations: [
    {

      name: String,
      coordinates: {
        latitude: Number,
        longitude: Number
      },
      storyTokenId: String
    }
  ],
  isActive: Boolean,
  createdAt: Date
};

module.exports = questStructure