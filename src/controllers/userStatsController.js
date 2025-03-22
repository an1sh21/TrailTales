const { db, admin } = require('../config/firebase');

// Get user statistics
const getUserStats = async (req, res) => {
  console.log('getUserStats called');
  try {
    const userId = req.user.uid;

    // Get user progress
    const userProgressDoc = await db.collection('userProgress')
      .doc(userId)
      .get();

    // Get user rewards
    const userRewardsDoc = await db.collection('userRewards')
      .doc(userId)
      .get();

    // If documents don't exist, initialize them
    const stats = {
      totalCoins: 0,
      completedQuests: [],
      activeQuests: [],
      collectedTokens: [],
      ...userProgressDoc.exists ? userProgressDoc.data() : {},
      rewards: {
        gold: 0,
        diamonds: 0,
        ...userRewardsDoc.exists ? userRewardsDoc.data() : {}
      }
    };

    // Get active quests details
    const activeQuestsDetails = [];
    if (stats.activeQuests && stats.activeQuests.length > 0) {
      const activeQuestsSnapshot = await db.collection('quests')
        .where(admin.firestore.FieldPath.documentId(), 'in', stats.activeQuests)
        .get();
      
      activeQuestsSnapshot.forEach(doc => {
        const quest = doc.data();
        activeQuestsDetails.push({
          id: doc.id,
          title: quest.title,
          description: quest.description,
          difficulty: quest.difficulty,
          progress: {
            current: stats.questProgress?.[doc.id]?.currentStep || 0,
            total: quest.totalSteps
          }
        });
      });
    }

    // Get completed quests
    const completedQuestsDetails = stats.completedQuests.map(quest => ({
      id: quest.questId,
      completedAt: quest.completedAt,
      rewards: quest.rewards
    }));

    res.json({
      stats: {
        totalCoins: stats.totalCoins,
        completedQuests: completedQuestsDetails,
        activeQuests: activeQuestsDetails,
        collectedTokens: stats.collectedTokens,
        rewards: {
          gold: stats.rewards.gold,
          diamonds: stats.rewards.diamonds
        }
      }
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Update user statistics after completing a quest
const updateUserStats = async (userId, questData, rewards) => {
  try {
    const userProgressRef = db.collection('userProgress').doc(userId);
    const userRewardsRef = db.collection('userRewards').doc(userId);

    // Update progress
    await userProgressRef.set({
      totalCoins: admin.firestore.FieldValue.increment(rewards.gold),
      completedQuests: admin.firestore.FieldValue.arrayUnion({
        questId: questData.id,
        completedAt: admin.firestore.FieldValue.serverTimestamp(),
        rewards: rewards
      }),
      activeQuests: admin.firestore.FieldValue.arrayRemove(questData.id),
      lastUpdated: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });

    // Update rewards
    await userRewardsRef.set({
      gold: admin.firestore.FieldValue.increment(rewards.gold),
      diamonds: admin.firestore.FieldValue.increment(rewards.includesDiamond ? 1 : 0)
    }, { merge: true });

    return true;
  } catch (error) {
    console.error('Error updating user stats:', error);
    return false;
  }
};

// Add a quest to user's active quests
const addActiveQuest = async (userId, questId) => {
  try {
    const userProgressRef = db.collection('userProgress').doc(userId);
    
    await userProgressRef.set({
      activeQuests: admin.firestore.FieldValue.arrayUnion(questId),
      [`questProgress.${questId}`]: {
        currentStep: 0,
        startedAt: admin.firestore.FieldValue.serverTimestamp(),
        status: 'in_progress'
      }
    }, { merge: true });

    return true;
  } catch (error) {
    console.error('Error adding active quest:', error);
    return false;
  }
};

module.exports = {
  getUserStats,
  updateUserStats,
  addActiveQuest
}; 