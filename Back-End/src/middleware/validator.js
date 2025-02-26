const { body, validationResult } = require('express-validator');

const validateCollectible = [
  body('collectibleId').notEmpty().isString(),
  body('locationType').notEmpty().isString(),
];

const validateTrade = [
  body('toUserId').notEmpty().isString(),
  body('legendId').notEmpty().isString(),
];

const validate = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }
  next();
};

module.exports = {
  validateCollectible,
  validateTrade,
  validate
}; 