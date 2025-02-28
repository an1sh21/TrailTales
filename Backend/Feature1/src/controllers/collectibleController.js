const { db } = require('../config/firebase');
const { COLLECTION_NAMES, COLLECTIBLE_TYPES } = require('../config/constants');
const { getDistance } = require('geofire-common');

const collectibleController = {
  async getNearbyCollectibles(req, res) {
    try {
      const { latitude, longitude, radius = 1000 } = req.query;
      const lat = parseFloat(latitude);
      const lng = parseFloat(longitude);

      const collectiblesRef = db.collection(COLLECTION_NAMES.COLLECTIBLES);
      const snapshot = await collectiblesRef.get();

      const nearbyCollectibles = [];
      snapshot.forEach(doc => {
        const collectible = doc.data();
        const distance = getDistance(
          [lat, lng],
          [collectible.location.latitude, collectible.location.longitude]
        ) * 1000; // Convert to meters

        if (distance <= radius) {
          nearbyCollectibles.push({
            id: doc.id,
            ...collectible,
            distance
          });
        }
      });

      res.json({ collectibles: nearbyCollectibles });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  },

  async collectItem(req, res) {
    try {
      const { collectibleId } = req.params;
      const userId = req.user.uid;

      const collectibleRef = db.collection(COLLECTION_NAMES.COLLECTIBLES).doc(collectibleId);
      const userRef = db.collection(COLLECTION_NAMES.USERS).doc(userId);

      await db.runTransaction(async (transaction) => {
        const collectible = await transaction.get(collectibleRef);
        const user = await transaction.get(userRef);

        if (!collectible.exists || !user.exists) {
          throw new Error('Invalid collectible or user');
        }

        const collectibleData = collectible.data();
        const userData = user.data();

        // Update user inventory based on collectible type
        switch (collectibleData.type) {
          case COLLECTIBLE_TYPES.COIN:
            userData.coins += collectibleData.value;
            break;
          case COLLECTIBLE_TYPES.TOKEN:
            userData.tokens += collectibleData.value;
            break;
          case COLLECTIBLE_TYPES.STORY:
          case COLLECTIBLE_TYPES.LEGEND:
            userData.inventory.collectibles.push(collectibleId);
            break;
        }

        transaction.update(userRef, userData);
        // Optionally delete or mark collectible as collected
        transaction.delete(collectibleRef);
      });

      res.json({ success: true, message: 'Item collected successfully' });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }
};

module.exports = collectibleController; 