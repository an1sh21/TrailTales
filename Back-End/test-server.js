const http = require('http');

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/plain' });
  res.end('Server is working!\n');
});

server.listen(3000, () => {
  console.log('Test server running at http://localhost:3000/');
}); 