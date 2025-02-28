const functions = require('firebase-functions');
const QuestService = require('./quests/questService');
const TokenModel = require('./tokens/tokenModel');

exports.createQuest = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
  }

  try {
    return await QuestService.createQuest({
      ...data,
      createdBy: context.auth.uid
    });
  } catch (error) {
    throw new functions.https.HttpsError('internal', error.message);
  }
});

exports.startQuest = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
  }

  try {
    return await QuestService.startQuest(data.questId, context.auth.uid);
  } catch (error) {
    throw new functions.https.HttpsError('internal', error.message);
  }
});

exports.collectToken = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
  }

  try {
    const { questId, tokenId, location } = data;
    
    // Verify quest progress and location
    const progress = await QuestService.checkQuestProgress(questId, context.auth.uid, location);
    
    if (progress.canCollectToken) {
      await TokenModel.collectToken(tokenId, context.auth.uid);
      return { success: true, message: 'Token collected successfully' };
    }
    
    return { success: false, message: 'Cannot collect token at this time' };
  } catch (error) {
    throw new functions.https.HttpsError('internal', error.message);
  }
}); 