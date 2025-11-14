const express = require('express');
const path = require('path');
const app = express();

// Serve static files from dist
app.use(express.static(path.join(__dirname, 'dist/erp-frontend/browser')));

// SPA fallback - serve index.html for all non-file requests
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist/erp-frontend/browser/index.html'));
});

const PORT = 4200;
app.listen(PORT, 'localhost', () => {
  console.log(`Server running at http://localhost:${PORT}`);
});
