const { auth } = require('../config/firebase');
const { ERROR_MESSAGES } = require('../config/constants');

const authenticateUser = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ error: ERROR_MESSAGES.UNAUTHORIZED });
    }

    const token = authHeader.split(' ')[1];
    const decodedToken = await auth.verifyIdToken(token);
    req.user = decodedToken;
    next();
  } catch (error) {
    res.status(401).json({ error: ERROR_MESSAGES.UNAUTHORIZED });
  }
};

module.exports = { authenticateUser }; 