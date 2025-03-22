const { db } = require('../firebaseConfig');

// Track user progress when visiting a site
exports.updateProgress = async (req, res) => {
    const { userID, siteID } = req.body;

    try {
        await db.collection('users').doc(userID).collection('progress').doc(siteID)
            .set({ completed: true, completedAt: new Date() }, { merge: true });

        res.json({ message: "Progress updated", siteID, completedAt: new Date() });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// Get all completed sites for a user
exports.getUserProgress = async (req, res) => {
    const { userID } = req.query;

    try {
        const progressSnapshot = await db.collection('users').doc(userID).collection('progress').get();
        let progress = progressSnapshot.docs.map(doc => ({
            siteID: doc.id,
            completedAt: doc.data().completedAt
        }));

        res.json({ progress });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// Sync offline progress when user reconnects
exports.syncOfflineProgress = async (req, res) => {
    const { userID, progress, collectedItems } = req.body;

    try {
        let syncedProgressCount = 0;
        let syncedItemsCount = 0;

        // Sync completed sites
        for (let item of progress) {
            await db.collection('users').doc(userID).collection('progress').doc(item.siteID)
                .set({ completed: true, completedAt: item.completedAt }, { merge: true });
            syncedProgressCount++;
        }

        // Sync collected items
        for (let item of collectedItems) {
            await db.collection('users').doc(userID).collection('collectedItems').doc(item.collectibleID)
                .set({ collectedAt: item.collectedAt }, { merge: true });
            syncedItemsCount++;
        }

        res.json({
            message: "Offline progress synced",
            syncedItems: {
                progress: syncedProgressCount,
                collectedItems: syncedItemsCount
            }
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};