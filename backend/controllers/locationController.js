const { db } = require('../firebaseConfig');
const { calculateDistance } = require('../utils/geolocationUtils');

exports.checkProximity = async (req, res) => {
    const { lat, lng, userID } = req.query;

    try {
        const locationsSnapshot = await db.collection('locations').get();
        const nearbyLocations = [];

        locationsSnapshot.forEach(doc => {
            const location = doc.data();
            const distance = calculateDistance(lat, lng, location.latitude, location.longitude);

            if (distance <= location.radiusMeters) {
                nearbyLocations.push(location);

                db.collection('users').doc(userID).collection('unlockedLocations')
                  .doc(location.siteID)
                  .set({ unlockedAt: new Date() }, { merge: true });
            }
        });

        res.json(nearbyLocations);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

exports.getAllLocations = async (req, res) => {
    try {
        const locationsSnapshot = await db.collection('locations').get();
        const locations = locationsSnapshot.docs.map(doc => doc.data());

        res.json(locations);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};