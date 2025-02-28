const { db } = require('../config/firebase');
const { COLLECTION_NAMES } = require('../config/constants');

const legendController = {
  async tradeLegend(req, res) {
    try {
      const { legendId, targetUserId } = req.params;
      const sourceUserId = req.user.uid;

      const legendRef = db.collection(COLLECTION_NAMES.LEGENDS).doc(legendId);
      const sourceUserRef = db.collection(COLLECTION_NAMES.USERS).doc(sourceUserId);
      const targetUserRef = db.collection(COLLECTION_NAMES.USERS).doc(targetUserId);

      await db.runTransaction(async (transaction) => {
        const [legend, sourceUser, targetUser] = await Promise.all([
          transaction.get(legendRef),
          transaction.get(sourceUserRef),
          transaction.get(targetUserRef)
        ]);

        if (!legend.exists || !sourceUser.exists || !targetUser.exists) {
          throw new Error('Invalid legend or user');
        }

        const legendData = legend.data();
        if (!legendData.tradeable) {
          throw new Error('This legend is not tradeable');
        }

        // Update legend ownership
        transaction.update(legendRef, { currentOwner: targetUserId });

        // Update user inventories
        const sourceUserData = sourceUser.data();
        const targetUserData = targetUser.data();

        sourceUserData.inventory.legends = sourceUserData.inventory.legends.filter(
          id => id !== legendId
        );
        targetUserData.inventory.legends.push(legendId);

        transaction.update(sourceUserRef, sourceUserData);
        transaction.update(targetUserRef, targetUserData);
      });

      res.json({ success: true, message: 'Legend traded successfully' });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }
};

module.exports = legendController; 