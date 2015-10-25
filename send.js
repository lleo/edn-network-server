#!/usr/bin/env node

var net = require('net')
  , nomnom = require('nomnom')

var opt = nomnom.script('send.js').options({
  port: {
    abbr: 'p'
  , flag: false
  , default: 10000
  , help: "connect port; default 10000"
  }
, positions: {
    abbr: 'P'
  , flag: false
}
}).parse()

var PORT = opt.port
  , POS  = opt.positions.split(',').map(function(v,i,a){ return parseInt(v,10) })
  , MSG  = opt[0] || ""

console.log("PORT = %d", PORT)
console.log("POS  = %j", POS )
console.log("MSG  = %j", MSG )

console.log("typeof MSG =", typeof MSG)

console.log(opt)

if (typeof MSG == 'string') {
  exit(1)
}

//process.exit(0)

//var MSG = "{ \"cmd\"    : \"append\","
//        + "  \"metric\" : \"cpu_usage\","
//        + "  \"node\"   : { \"host\" : \"localhost\", \"cpu\" : 0 },"
//        + "  \"data\"   : 100 }"

//MSG = JSON.stringify( JSON.parse(MSG) )
MSG = JSON.stringify( MSG )

console.log("SENDING: >"+ MSG + "\n\n<")
var sk = net.connect({port: PORT}, function () {
  sk.write(MSG+"\n\n")
})

sk.on('data', function (data) {
  console.log("REPLY: >" + data.toString() + "<")
  sk.end()
})
