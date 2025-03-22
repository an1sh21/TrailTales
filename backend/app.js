const express = require('express');
const cors = require('cors');

const locationRoutes = require('./routes/locationRoutes');

const app = express();
app.use(cors());
app.use(express.json());

app.use('/api/location', locationRoutes);

module.exports = app;