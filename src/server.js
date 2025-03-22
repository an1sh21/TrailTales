const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const path = require('path');
require('dotenv').config();

const { errorHandler } = require('./middleware/errorHandler');

// Routes import
const authRoutes = require('./routes/auth');
const storiesRoutes = require('./routes/stories');
const collectiblesRoutes = require('./routes/collectibles');
const questsRoutes = require('./routes/quests');
const challengesRoutes = require('./routes/challenges');
const usersRoutes = require('./routes/users');

const app = express();

// Debug logging
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);
  next();
});

// Basic test endpoint
app.get('/', (req, res) => {
  res.json({ message: 'Trail Tales API is running!' });
});

// Middleware
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(helmet({
  contentSecurityPolicy: false,
  crossOriginEmbedderPolicy: false
}));
app.use(express.json());

// Serve static files from the public directory
app.use('/public', express.static(path.join(__dirname, '..', 'public')));

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/stories', storiesRoutes);
app.use('/api/collectibles', collectiblesRoutes);
app.use('/api/quests', questsRoutes);
app.use('/api', challengesRoutes);
app.use('/api/users', usersRoutes);

// Error handling
app.use(errorHandler);

// Get port from environment or default to 9000
const PORT = process.env.PORT || 9000;

// Start the server
app.listen(PORT, () => {
  console.log(`Trail Tales Server running on http://localhost:${PORT}`);
}).on('error', (error) => {
  console.error('Server failed to start:', error.message);
  process.exit(1);
}); 