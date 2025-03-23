const { db } = require('../config/firebase');

exports.getQuestChallenges = async (req, res) => {
  try {
    const { questId } = req.params;
    const challengesSnapshot = await db.collection('challenges')
      .where('questId', '==', questId)
      .get();
    
    const challenges = [];
    challengesSnapshot.forEach(doc => {
      challenges.push({
        id: doc.id,
        ...doc.data()
      });
    });
    
    res.json(challenges);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}; 