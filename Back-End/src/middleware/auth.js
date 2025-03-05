const { admin } = require('../config/firebase');
const { ERROR_MESSAGES } = require('../config/constants');

const verifyToken = async (req, res, next) => {
  try {
    // Get the token from the Authorization header
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ 
        error: 'Unauthorized - No token provided' 
      });
    }

    // Extract the token
    const token = authHeader.split('Bearer ')[1];

    // Verify the token
    const decodedToken = await admin.auth().verifyIdToken(token);
    
    // Add the user information to the request object
    req.user = {
      uid: decodedToken.uid,
      email: decodedToken.email
    };

    next();
  } catch (error) {
    console.error('Auth Error:', error);
    return res.status(401).json({ 
      error: 'Unauthorized - Invalid token' 
    });
  }
};

module.exports = { verifyToken }; 