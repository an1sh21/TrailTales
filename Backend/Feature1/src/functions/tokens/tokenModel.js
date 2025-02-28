const { db } = require('../utils/firebase');

const tokensCollection = db.collection('tokens');

const TokenModel = {
  // Token structure in Firestore
  /*
    {
      id: string,
      name: string,
      description: string,
      story: string,
      questId: string,
      locationId: string,
      coordinates: {
        latitude: number,
        longitude: number
      },
      arModel: string, // URL to AR model
      isCollected: boolean,
      collectedBy: [string], // array of user IDs
      createdAt: timestamp
    }
  */

  async createToken(tokenData) {
    const token = await tokensCollection.add({
      ...tokenData,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      isCollected: false,
      collectedBy: []
    });
    return token.id;
  },

  async collectToken(tokenId, userId) {
    return await tokensCollection.doc(tokenId).update({
      collectedBy: admin.firestore.FieldValue.arrayUnion(userId)
    });
  }
};

module.exports = TokenModel; 