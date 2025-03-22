const express = require('express');
const cors = require('cors');

const locationRoutes = require('./routes/locationRoutes');

const app = express();
app.use(cors());
app.use(express.json());

app.use('/api/location', locationRoutes);

const collectibleRoutes = require('./routes/collectibleRoutes');
app.use('/api/collectibles', collectibleRoutes);

const progressRoutes = require('./routes/progressRoutes');
app.use('/api/progress', progressRoutes);

module.exports = app;