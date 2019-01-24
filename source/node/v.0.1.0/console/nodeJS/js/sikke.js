var sikke = require('sikke');
var stdio = require('stdio');
const Ora = require('ora');
var colors = require('colors');
var commands = require('./commands')
var prettyjson = require('prettyjson');
const cliSpinners = require('cli-spinners');

var sikkeClient = new sikke.Client({
  host: '127.0.0.1', // host: '185.195.254.26',
  port: 9090,
  user: 'parifixusdxxt',
  pass: 'vmz5a51va623*a1v15a',
  timeout: 1000 * 60 * 3
});

var options = {
  keysColor: 'green',
  dashColor: 'magenta',

};

const spinner = new Ora({
  text: 'Processing your command. Please wait...'.magenta,
  spinner: process.argv[2]
});
spinner.color = 'yellow';


var ops = stdio.getopt({});
if (ops.args == null) {
  console.log('Any command found.'.red);
} else {
  var method = ops.args[0];
  var params = ops.args.slice(1);

  var isFunctionValid = 0;
  for (var protoFn in commands) {
    if (method == protoFn) {
      isFunctionValid = 1;
    }
  }
  if (isFunctionValid) {
    if (params.length == 0) {
      params = "";
    }
    spinner.start();
    sikkeClient.cmd(method, ...params, function (err, data) {
      if (!err) {
        spinner.succeed("Your transaction has been successfully completed.\n".green);
        switch (method) {
          case "help":
            for (var item of data) {
              console.log("  " + item.yellow);
            }
            break;
          case "getTransactions":
          case "createWalletAndSave":
          case "createWallet":
          case "getHistories":
          case "send":
            console.log(prettyjson.render(data, options));
            break;
          default:
            console.table(data);
            break;
        }
      } else {
        spinner.fail("Your operation was not completed successfully. Error: ".red + err);
      }
    })
  } else {
    console.log('You have entered an unknown command. Please see the help menu for help.'.red);
  }
}