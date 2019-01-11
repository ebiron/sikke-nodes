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
  text: 'Processing your command. Please wait...',
  spinner: process.argv[2]
});
spinner.color = 'yellow';

/*
setTimeout(() => {
  spinner.color = 'yellow';
  spinner.text = 'Loading rainbows';
}, 10000);*/


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
        if (method == "help") {
          console.log(prettyjson.render(data, options));
          for (var item of data) {
            console.log("  " + item.yellow);    

          }
          //  console.log(data);
        } else {
          console.log(prettyjson.render(data, options));
        }
      } else {
        spinner.fail("Your operation was not completed successfully. Error: " + err.red);
        console.log("Your operation was not completed successfully. Error: " + err.red);
      }
    })
  } else {
    console.log('You have entered an unknown command. Please see the help menu for help.'.red);
  }
}




/*
 * sikkeClient.cmd('repairTx', function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('createAccount', "parifix2", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('createAccountAndSave', "alias_name:sikke10",
 * "limit_hourly:100", "limit_daily:500", "limit_max_amount:1500", function(err,
 * data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('mergeBalance', function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('syncTx', function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('syncWallet',
 * "address:SKK1Lxh42NcQvsNEvspiALD2tenSCxdkRUisg", "limit_hourly:9000",
 * "limit_daily:90000", "limit_max_amount:200000", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('getTransactions', "seq:11", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('listAccounts', function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('getBalance', "SKK1P8fQ23UDua311wjc53zxsjvVQ4s6JcFnx",
 * function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('send', "from:SKK1KuAwe4kR1SfdoXDiQStVtRPvdTVaKy2ry",
 * "to:SKK1QGJeuVYm7zcs3JGjFZ6asgXsKrkUMDAbC", "amount:1", "desc:aqqq",
 * function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('importWallet',
 * "7rMnVkKaqAj7uDoGthqYUSGjHYYkFDw3FEeMT2CYCnqX", function(err, data) {
 * console.log(data) })
 */
/*
 * sikkeClient.cmd('makeDefault', "SKK1KuAwe4kR1SfdoXDiQStVtRPvdTVaKy2ry",
 * function(err, data) { console.log(data) })
 */
/*
 * sikkeClient.cmd('help', ,function(err, data) { console.log(data) })
 */


/*
if (method === 'test') {
  sikkeClient.cmd(method, function(err, data) {
    console.log(data)
  })
}
*/
