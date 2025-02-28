const QuestModel = require('./questModel');
const TokenModel = require('../tokens/tokenModel');

const QuestService = {
  async createQuest(questData) {
    try {
      const questId = await QuestModel.createQuest(questData);
      return { success: true, questId };
    } catch (error) {
      throw new Error('Failed to create quest: ' + error.message);
    }
  },

  async startQuest(questId, userId) {
    try {
      const quest = await QuestModel.getQuestById(questId);
      if (!quest) {
        throw new Error('Quest not found');
      }
      
      // Create user progress tracking in Firestore
      await db.collection('userQuests').add({
        userId,
        questId,
        progress: 0,
        startedAt: admin.firestore.FieldValue.serverTimestamp(),
        completedLocations: [],
        status: 'IN_PROGRESS'
      });

      return { success: true, quest };
    } catch (error) {
      throw new Error('Failed to start quest: ' + error.message);
    }
  },

  async checkQuestProgress(questId, userId, location) {
    try {
      const userQuest = await db.collection('userQuests')
        .where('userId', '==', userId)
        .where('questId', '==', questId)
        .limit(1)
        .get();

      if (userQuest.empty) {
        throw new Error('Quest not started');
      }

      // Verify if user is at the correct location
      // Implement location verification logic here

      return { success: true, canCollectToken: true };
    } catch (error) {
      throw new Error('Failed to check quest progress: ' + error.message);
    }
  }
};

module.exports = QuestService; 