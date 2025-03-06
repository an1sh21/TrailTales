const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
require('dotenv').config();

const storiesRouter = require('./routes/stories');
const collectiblesRouter = require('./routes/collectibles');
const legendsRouter = require('./routes/legends');

const app = express();

// Middleware
app.use(helmet());
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/stories', storiesRouter);
app.use('/api/collectibles', collectiblesRouter);
app.use('/api/legends', legendsRouter);

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Something went wrong!' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
}); 