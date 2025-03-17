const http = require('http');

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/plain' });
  res.end('Simple server is working!\n');
});

const PORT = 8000;
server.listen(PORT, () => {
  console.log(`Simple server running at http://localhost:${PORT}/`);
}); 