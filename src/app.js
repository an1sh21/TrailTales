require('dotenv').config();  // Load environment variables from .env file
const express = require('express');
const questRoutes = require('./routes/questRoutes');
const challengeRoutes = require('./routes/challengeRoutes');
const path = require('path');

const app = express();

// Middleware to parse incoming JSON requests
app.use(express.json());

// Serve static files from the public directory
app.use(express.static(path.join(__dirname, '../public')));

// Routes
app.use('/api', questRoutes);
app.use('/api', challengeRoutes);

// Error handling middleware for catching server errors
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ message: 'Something went wrong!' });
});

// Port configuration
const PORT = process.env.PORT || 3001;

// Start the server
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
