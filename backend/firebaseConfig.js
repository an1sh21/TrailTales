const admin = require('firebase-admin');
const serviceAccount = require('./trailtales-5ce34-firebase-adminsdk-fbsvc-a7fb7ddd09.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

module.exports = { admin, b };