const express = require('express');
const router = express.Router();

router.post('/auth/token', (req, res) => {
    console.log('Received request:', req.body);
    res.json({ message: "Token route is working!" });
});

module.exports = router;
