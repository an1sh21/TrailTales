const { admin } = require('../config/firebase');
const { ERROR_MESSAGES } = require('../config/constants');

/**
 * Middleware to verify Firebase authentication token
 */
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
    
    // Check if we're using a test token from .env
    const testToken = process.env.TEST_AUTH_TOKEN;
    const testUserId = process.env.TEST_USER_ID;
    
    if (process.env.NODE_ENV === 'development' && token === testToken && testUserId) {
      console.log('Using test authentication for user:', testUserId);
      req.user = { uid: testUserId };
      return next();
    }

    // Verify the token with Firebase
    const decodedToken = await admin.auth().verifyIdToken(token);
    
    // Add the user information to the request object
    req.user = decodedToken;

    next();
  } catch (error) {
    console.error('Auth Error:', error);
    return res.status(401).json({ 
      error: 'Unauthorized - Invalid token' 
    });
  }
};

// Alias for backward compatibility with Feature2 code
const authenticateToken = verifyToken;

module.exports = { 
  verifyToken,
  authenticateToken
}; 