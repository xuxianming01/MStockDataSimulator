var fs = require('fs')
    , http = require('http')
    , socketio = require('socket.io');

 
var server = http.createServer(function(req, res) {
    res.writeHead(200, { 'Content-type': 'text/html'});
    res.end(fs.readFileSync(__dirname + '/index.html'));
}).listen(8080, function() {
    console.log('Listening at: http://localhost:8080');
});


var clients = {};

socketio.listen(server).on('connection', function (socket) {
    console.log("Connection " + socket.id + " accepted.");
    clients[socket.id] = socket;
    socket.on('disconnect', function(){
    	delete clients[socket.id];
    });
    socket.on('error', function (exc) {
        console.log("ignoring exception: " + exc);
   });
});

var thrift = require('thrift');
       
var UserStorage = require('./gen-nodejs/SendStockDataTool.js'),
    ttypes = require('./gen-nodejs/SendStockDataTool_types');
       
var server = thrift.createServer(UserStorage, {
  sendStockData: function(stockdata, success) {
    console.log("server stored:");
    for(sc in clients)
    {
       console.log("clinet:");
       console.log("clinet:"+clients[sc].id);
       clients[sc].volatile.emit('message', stockdata);
    }
    success();
  },
});
server.listen(9799);

process.on('uncaughtException', function(err){
     console.log("error:"+err);
});




 
