const { ERROR_MESSAGES } = require('../config/constants');

/**
 * Global error handling middleware
 */
const errorHandler = (err, req, res, next) => {
  console.error('Error:', err.stack);
  const statusCode = err.statusCode || 500;
  const message = err.message || ERROR_MESSAGES.SERVER_ERROR;
  
  res.status(statusCode).json({ error: message });
};

module.exports = { errorHandler }; 