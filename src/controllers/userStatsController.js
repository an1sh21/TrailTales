const { db } = require('../config/firebase');

// Get user stats
exports.getUserStats = async (req, res) => {
  try {
    const userId = req.user.uid;
    
    // Get user document
    const userDoc = await db.collection('users').doc(userId).get();
    
    if (!userDoc.exists) {
      return res.status(200).json({
        quests: {
          completed: 0,
          inProgress: 0
        },
        collection: {
          stories: 0,
          collectibles: 0
        }
      });
    }
    
    const userData = userDoc.data();
    
    // Get user progress for quests
    const userProgressSnapshot = await db.collection('userProgress')
      .where('userId', '==', userId)
      .get();
    
    let completedQuests = 0;
    let inProgressQuests = 0;
    
    userProgressSnapshot.forEach(doc => {
      const progress = doc.data();
      if (progress.status === 'completed') {
        completedQuests++;
      } else if (progress.status === 'in_progress') {
        inProgressQuests++;
      }
    });
    
    // Count collection items
    const collection = userData.collection || [];
    const storiesCount = collection.filter(item => item.type === 'story').length;
    const collectiblesCount = collection.filter(item => item.type === 'collectible').length;
    
    res.json({
      quests: {
        completed: completedQuests,
        inProgress: inProgressQuests
      },
      collection: {
        stories: storiesCount,
        collectibles: collectiblesCount
      }
    });
  } catch (error) {
    console.error('Error getting user stats:', error);
    res.status(500).json({ message: 'Failed to get user statistics' });
  }
}; 