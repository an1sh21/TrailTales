require('dotenv').config();
const express = require('express');
const questRoutes = require('./routes/questRoutes');
const challengeRoutes = require('./routes/challengeRoutes');
const userRoutes = require('./routes/userRoutes');
const authRoutes = require('./routes/authRoutes');  // ✅ Add this

const app = express();

app.use(express.json());

// Routes
app.use('/api', questRoutes);
app.use('/api', challengeRoutes);
app.use('/api/user', userRoutes);
app.use('/api', authRoutes);  // ✅ Add this

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ message: 'Something went wrong!' });
});

const PORT = process.env.PORT || 3001;

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
