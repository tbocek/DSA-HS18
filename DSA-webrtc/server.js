const express = require('express');
const WebSocket = require('ws');

// App setup
var app = express();

var server = app.listen(4000, function () {
    console.log('listening for requests on port 4000.');
});

// Static files
app.use(express.static('.'));

const wss = new WebSocket.Server({ server });

wss.on('connection', function(ws) {
    ws.on('message', function(message) {
        // Broadcast any received message to all clients
        console.log('received: %s', message);
        wss.broadcast(message);
    });
});

wss.broadcast = function(data) {
    this.clients.forEach(function(client) {
        if(client.readyState === WebSocket.OPEN) {
            client.send(data);
        }
    });
};

console.log('Server running.');