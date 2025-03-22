const { db } = require('../firebaseConfig');

// Get available collectibles at a site
exports.getAvailableCollectibles = async (req, res) => {
    const { siteID, userID } = req.query;

    try {
        const collectiblesSnapshot = await db.collection('collectibles').where('siteID', '==', siteID).get();
        const collectedSnapshot = await db.collection('users').doc(userID).collection('collectedItems').get();
        
        let collectedItems = collectedSnapshot.docs.map(doc => doc.id);
        let available = { story_tokens: [], legends: [], coins: [] };

        collectiblesSnapshot.forEach(doc => {
            let item = doc.data();
            if (!collectedItems.includes(doc.id)) { // Only return items the user has not collected
                if (item.type === "story_token") available.story_tokens.push({ id: doc.id, ...item });
                else if (item.type === "legend") available.legends.push({ id: doc.id, ...item });
                else if (item.type === "coin") available.coins.push({ id: doc.id, ...item });
            }
        });

        res.json(available);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// User collects an AR item
exports.collectItem = async (req, res) => {
    const { userID, collectibleID } = req.body;

    try {
        const collectibleRef = db.collection('collectibles').doc(collectibleID);
        const collectible = await collectibleRef.get();

        if (!collectible.exists) {
            return res.status(404).json({ error: "Collectible not found" });
        }

        await db.collection('users').doc(userID).collection('collectedItems').doc(collectibleID)
            .set({ collectedAt: new Date() });

        res.json({ message: "Collectible added to user inventory", collectible: { id: collectibleID, ...collectible.data() } });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// Get user's collected items
exports.getUserCollectibles = async (req, res) => {
    const { userID } = req.query;

    try {
        const collectedSnapshot = await db.collection('users').doc(userID).collection('collectedItems').get();
        let collectedItems = collectedSnapshot.docs.map(doc => ({
            id: doc.id,
            collectedAt: doc.data().collectedAt
        }));

        res.json({ collectedItems });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};