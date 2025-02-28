const { db } = require('../utils/firebase');

const questsCollection = db.collection('quests');

const QuestModel = {
  // Quest structure in Firestore
  /*
    {
      id: string,
      title: string,
      description: string,
      difficulty: string,
      theme: string,
      locations: [{
        id: string,
        name: string,
        coordinates: {
          latitude: number,
          longitude: number
        },
        tokenId: string,
        order: number
      }],
      rewards: [{
        type: string,
        value: any
      }],
      createdBy: string,
      createdAt: timestamp,
      isActive: boolean
    }
  */

  async createQuest(questData) {
    const quest = await questsCollection.add({
      ...questData,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      isActive: true
    });
    return quest.id;
  },

  async getQuestById(questId) {
    const quest = await questsCollection.doc(questId).get();
    return quest.exists ? { id: quest.id, ...quest.data() } : null;
  },

  async getQuestsByTheme(theme) {
    const quests = await questsCollection
      .where('theme', '==', theme)
      .where('isActive', '==', true)
      .get();
    return quests.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  }
};

module.exports = QuestModel; 