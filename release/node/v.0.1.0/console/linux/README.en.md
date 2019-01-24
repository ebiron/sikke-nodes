![enter image description here](https://wallet.sikke.com.tr/static/images/sikke_client_logo.jpg)
# Welcome to Sikke Client



**Sikke Platform** is an ecosystem in which transfer and other transactions related to Sikke (SKK) crypto coin and other crypto currencies created in the coin platform are made; smart contracts are created and approved; and future transactions can be made.


##  Get Started

To use the coin client system on your personal computer, 2 applications must be downloaded. One of these is the <strong>Sikke Node Server</strong> and the other is <strong>Sikke Node Client</strong> which is used to connect to this server.
<strong>Sikke Node Server</strong> is a jar console based jar file. When the jar file is run, a server stands up and starts listening to requests to be sent to http://localhost:9090/  

<strong>Sikke Node Client</strong> is a console application designed to send requests to this server. 

<strong>Sikke Node Server</strong> consists of a <em><strong>single jar file of all operating systems</strong></em>, while the <strong>Sikke Node Client</strong> has 3 instances that you can download according to your operating system. 

<br>How to use the Sikke Node is described in detail in the documentation below.

To run the Sikke Node Server, make sure you have the latest version of Java installed on your personal computer.  
If the latest version of Java is not installed on your system, you can  download from https://www.java.com/en/download/
and install the latest version of Java.
To see if the server is up and running; You can request from the browser and Postman at <a  href="http://localhost:9090/serverStatus">http://localhost:9090/serverStatus</a>

You can  [clone](https://github.com/sikke-official/sikke-nodes.git)  Sikke Node from Sikke Github Repository:

[https://github.com/sikke-official/sikke-nodes.git](https://github.com/sikke-official/sikke-nodes.git)

## How to run Sikke Node Server(Windows, Linux, MacOS)

<p>After installing the current version of Java on your personal computer, you should run the <strong>Sikke Node Server</strong> as follows.

   You can run Sikke Node Server as follows;
 

 

 1. Open new terminal(console)
 2. Go to the directory where the jar file is located
 	> cd sikke-nodes\release\node\v.0.1.0\server
 
 
 3.    Run the following command  
	

     java -jar sikke.jar
     Sikke Node Server started at port: 9090


![enter image description here](https://github.com/sikke-official/sikke-nodes/blob/master/docs/images/sikke_console_java_jar.PNG)

  ## How to run Sikke Node Console

<p>In order to request <strong>Sikke Node Server</strong> from <strong>Sikke Node Console</strong> , you must download Sikke Node Console which is suitable for your operating system.</p>

You can run Sikke Node Console as follows;
  
  

 1. Open new terminal(console)
  2. Go to the directory where the console folder is located and run
   ***for Windows***
		> cd sikke-nodes\release\node\v.0.1.0\console\windows
		>`sikke register email:test.mail@gmail.com password:123456`
		
	 ***for Linux and MacOS***
		> cd sikke-nodes\release\node\v.0.1.0\console\linux
		>`./sikke register email:test.mail@gmail.com password:123456`

	


![enter image description here](https://github.com/sikke-official/sikke-nodes/blob/master/docs/images/sikke_console_client.PNG)

## Introduction

[JSON-RPC](https://en.wikipedia.org/wiki/JSON-RPC)  is a remote procedure call protocol encoded in JSON. You can use this API to access data in  [Sikke](https://www.sikke.com.tr/)  Client. The JSON-RPC API server runs on:

-   `http://localhost:9090/`  when running Sikke Node locally.

All API calls are POST requests made to Sikke API.

All requests follow the standard JSON-RPC format and include 4 variables in the data object:

|Data Object | Example |
|--|--|
| id |  e.g. "1"|
|jsonrpc |e.g. "2.0"  |
|metthod |  e.g. "getBalances"|
|params | e.g. ["1"] |

## Sikke Node Commands

  

> `| means OR  ,  & means AND`
> `[]  means Optional , () means Mandatory`, 
   ## register  

>   _**register**_ function accepts two required parameters. One of these is the email and the other is the password. If you are not registered in the system, you can register and perform wallet operations.

     register (email:Value), (password:Value)

|Parameter|Definition  |Obligation
|--|--|--|
|email   | New user email | required
|password   | New user password | required

> Example Usage(console):
> 

> 

       register email:test@gmail.com password:your_password

## login  

>  _**login**_ function accepts two required parameters to login operation, one user username(email) and one user password. You must be logged in to make wallet transactions at the Sikke Node.

     login (email:Value), (password:Value)

|Parameter|Definition  |Obligation
|--|--|--|
|email   | New user email | required
|password   | New user password | required

> Example Usage(console):

    >login email:test@gmail.com password:your_password

   

## logout  

> _**logout**_ function allows the user to log out of the system. The user leaving the system must be logged in to perform the operation again.

    logout

   ## makeDefault  

>   The ***makeDefault*** function receives the **Sikke wallet address** starting with **SKK** as a mandatory parameter.
>    The wallet address is marked by default.

     makeDefault (address)

|Parameter|Definition  |Obligation
|--|--|--|
|address   | Sikke wallet address | required

> Example Usage(console):

    >makeDefault SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA


   
   

## importWallet
   >    ***importWallet*** function receives the **Sikke private address** . 
   >     By importing the private key address of the existing  **Sikke** wallet into the coin client system, a new wallet is created in this system.

     importWallet (privateKey)

|Parameter|Definition  |Obligation
|--|--|--|
|privateKey   | SIKKE Private Key Address | required

    

> Example Usage(console):

    >importWallet 8o4taasJhgRaXJb5T1opıpnhzKHFfFGPSZ2HcWEznnvybz

   

## listWallets
   >    _***listWallets***_ function does not receive any parameters.  
This command lists the wallets and wallet balances information in the Sikke System of the logged-in user.

> Example Usage(console):
> 

    >listWallets

 

## getHistories
   >  _**getHistories**_ function runs in 2 different modes. First of all, no parameters are accepted. If no parameter is entered, the user's last 100 transaction records will be returned. In the other mode, only 1 parameter must be entered. The records are returned according to the selected parameter type.

> First Mode:

     getHistories
> Second Mode:

     getHistories [address:Value] [hash:Value] [seq:Value] [block:Value] 

|Parameter|Definition  |Obligation
|--|--|--|
|address/hash/seq/block : Value| Single Parameter | required

>Example Usages(console):
    
**First Mode:**


    >getHistories

**Second Mode:**

 
 > ***address:Value***

    

    >getHistories address:SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA

> ***hash:Value***

    >getHistories hash:8bce4ekkdl557678b9eb41f934654646595fd7b427032f2f48d9ddfgggfb7ea326791kk8dj  

> ***seq:Value***

    >getHistories seq:10001
> ***block:Value***

    >getHistories block:10000


## getTransactions

 
> _**getTransactions**_  function returns transactions on the  **Sikke Network**.  **_To run this command, the user does not need to login to the  Sikke Node_**. This command works with the paging logic and brings up a maximum of  _**100 records**_ each time. You can change other values (pages) by changing the _**`skip`**_ value.
>  
> This command is called without entering any parameters; The default  _**`skip`**_ value is 0 (zero) (first page) and the default  _**`limit`**_ value is set to 100, bringing the records in the  **Sikke Network**. This command can accept multiple optional parameters. You can retrieve records by filtering this command using optional parameters.
> 
     getTransactions [skip:Value] [limit:Value] [sort:Value] [seq_gt:Value] [wallet:Value] [wallets:Values] [asset:Value] [type:Value] [subtype:Value] [status:Value] [public_key:Value] [from_date:Value] [to_date:Value] [user_id:Value] [seq:Value] [group:Value]

|Parameter|Definition  |Obligation
|--|--|--|
|skip| Page number. Default value is 0(zero). 0 means first page | optional
|limit| Number of records on each page. Default and maximum value is 100. | optional
|sort|Sorting value. It is used to sort the records in the order of transaction. Its value can be **`asc`** and **`desc`**. Default value is **`desc`**|optional
|seq_gt| It is used to bring the **greater than** the given sequence value.| optional
|wallet| Sikke wallet address | optional
|wallets| Multiple Sikke wallet addresses separated by commas. Example:SKK1GeGCy24yc8K82i5EzZnwSWSi5cv45Gvdx,SKK1oa5Lgg5zU1x2hFebbMnsDsXp99xsJtZm,SKK1EFMvq6ftrzNqXHAroBEBtVA8Vr9SbG7MA | optional
|asset| Asset type | optional
|type| Transaction type | optional
|subtype| Transaction subtype | optional
|status| Transaction status | optional
|public_key| Sikke wallet public key | optional
|from_date| It is used to fetch records belonging to the transaction date larger than the given date. The date format must be yyyyMMdd(20180101) | optional
|to_date| It is used to fetch records belonging to the transaction date smaller than the given date. The date format must be yyyyMMdd(20180201) | optional
|user_id| It is used to retrieve the transactions of the given user_id. | optional
|seq| It is used to retrieve transactions with a given sequence number. | optional
|group| It is used to retrieve transactions with a given group number. | optional
   

> Example Usage(console):
> 

    >createWallet alias_name:your_wallet_name
    >createWallet alias_name:your_wallet_name limit_hourly:100 limit_daily:1000 limit_max_amount:2000
    >createWallet alias_name:your_wallet_name limit_hourly:100 limit_daily:1000 limit_max_amount:2000 callback_url:your_callback_url


   ## createWallet
   >   _**createWallet**_ function receives the optional wallet aliasName parameter.  
The command creates a **Sikke** wallet starting with the **SKK** prefix in the Sikke Node local database.

     createWallet alias_name:Value limit_hourly:Value limit_daily:Value limit_max_amount:Value callback_url:Value

|Parameter|Definition  |Obligation
|--|--|--|
|alias_name| Sikke Wallet Alias Name (**Alias name must be written without spaces.**) | optional
|limit_hourly| Hourly Transfer Limit | optional
|limit_daily|Daily Transfer Limit|optional
|limit_max_amount| Max Transfer Limit | optional
|callback_url| Callback URL | optional
   

> Example Usage(console):
> 

    >createWallet alias_name:your_wallet_name
    >createWallet alias_name:your_wallet_name limit_hourly:100 limit_daily:1000 limit_max_amount:2000
    >createWallet alias_name:your_wallet_name limit_hourly:100 limit_daily:1000 limit_max_amount:2000 callback_url:your_callback_url

 

## createWalletAndSave

> **createWalletAndSave**_ function receives the optional parameters. The command creates **Sikke** wallet in **Sikke Node**. The created wallet is synchronized to the Sikke Network. When you create your **Sikke** wallet, you can see this wallet on all Sikke Systems (**Sikke Web Wallet**, **Sikke Web Wallet**, **Sikke Node**). With the
> created wallet, transaction can be made in all Sikke Systems.

     createWalletAndSave alias_name:Value limit_hourly:Value limit_daily:Value limit_max_amount:Value callback_url:Value

|Parameter|Definition  |Obligation
|--|--|--|
|alias_name| Sikke Wallet Alias Name (**Alias name must be written without spaces.**) | optional
|limit_hourly| Hourly Transfer Limit | optional
|limit_daily|Daily Transfer Limit|optional
|limit_max_amount| Max Transfer Limit | optional
|callback_url| Callback URL | optional



> Example Usages(console):

    >createWalletAndSave  alias_name:my_wallet_name
    >createWalletAndSave  alias_name:my_wallet_name limit_hourly:100 limit_daily:1000 limit_max_amount:2000
    >createWalletAndSave  alias_name:my_wallet_name limit_hourly:100 limit_daily:1000 limit_max_amount:2000 callback_url:your_callback_url



   
   ## mergeBalances
   >    _**mergeBalances**_  function has two modes.

In the first, this method does not accept any parameters. If no parameters are entered, all balances in all wallets except the default wallet are sent to the default wallet.

In the other mode, only the asset type, only the wallet address, or both can be entered. If only asset type is entered, the balances of all types of wallets other than the default wallet are sent to the default wallet. If only the wallet address is entered, all the balances of all the wallets except those entered wallets are sent to this wallet. If both are entered, the specified wallet is sent the specified type of balances of all other wallets.

> First Mode:

    mergeBalances

> Second Mode:

    mergeBalances [address] [asset]

   

|Parameter|Definition  |Obligation
|--|--|--|
|address| Sikke Wallet Address | optional
|asset| Asset Type | optional

> Example Usages(console):
> 
**First Mode:**

    >mergeBalances
    
**Second Mode:**

     >mergeBalances asset:SKK
     >mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
     >mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x asset:SKK

   ## getBalances
   
   >    _**getBalances**_ function accepts two optional parameters, one SIKKE wallet address and one asset. The command lists the balance information of the wallets found in the coin client according to the specified criteria.

     getBalances [sikkeWalletAddress] [asset]

|Parameter|Definition  |Obligation
|--|--|--|
|sikkeWalletAddress| Sikke Private Key Address | optional
|asset| asset (SKK,XTG,UPC,OKO,..) | optional


> Example Usages(console):

    >getBalances
    >getBalances SKK 
    >getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
    >getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x SKK
    >getBalances SKK SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x


 ## send
   >   **send** function takes two required parameters, one is the receiving address and the other is amount.  
This command sends the specified amount of coins to the selected recipient wallet address.  
If the sender wallet address is not entered, the default wallet in the system is set as sender wallet.  
If the asset is not selected, then the default asset is determined as SKK asset type.  
The transfer specified as “hidden” appears as a hidden transaction on the network.

     send [from:Value] (to:Value) [asset:Value] (amount:Value) [desc:Value] [hidden:Value]

|Parameter|Definition  |Obligation
|--|--|--|
|from| Sender Wallet Address | optional
|to| Receiver Wallet Address | required
|asset| Asset(SKK,XTG,UPC,OKO,...) | optional
|amount| Amount | required
|desc| Description| optional
|hidden| Hidden Transaction | optional


> Example Usages(console):

    >send to:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
    >send from:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
    >send from:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc to:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x amount:100 asset:UPC
    >send from:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc to:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x amount:100 asset:UPC desc:your_transaction_description hidden:1

   


   ## syncWallet
   >    


    

> ***syncWallet*** has two modes. 
> 
>In the first of these modes, if any parameters are not entered, 
The wallets of the user who entered the Sikke Node are transferred to the Sikke Network. The wallets of this user on the Sikke Node are notified to the coin network.
***This synchronization is repeated each time the user enters the Sikke Node.***

>In the second of these modes, a wallet created with createWallet is synchronized with the Sikke System.

> After that, the wallet that was previously only available in the local database becomes visible on the entire SIKKE System (**Sikke Web Wallet**, **Sikke Mobile Wallet**, **Sikke Client**).

#### First Mod:

     syncWallet

> Example Usage(console):

    syncWallet

#### Second Mod:

    syncWallet [address:Value] (alias_name:Value) (limit_daily:Value) (limit_hourly:Value) (limit_max_amount:Value)

|Parameter|Definition  |Obligation
|--|--|--|
|address| Sikke Private Key Address | required
|alias_name| Alias Name | optional
|limit_daily| Daily Transfer Limit | optional
|limit_hourly| Hourly Transfer Limit | optional
|limit_max_amount| Max Transfer Limit | optional

    

> `Example Usages(console):`

    >syncWallet address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x 
    >syncWallet address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x limit_daily:100 limit_daily:1000
    >syncWallet address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x alias_name:My_Wallet


 ## importWallets
   >    _**importWallets**_ function imports the wallets in the file by reading the "wallets.skk" file in the directory where the jar file is located. If the file cannot be found, it returns a file not found error.

    importWallets



> Example Usage(console):

    >importWallets


 ##  exportWallets
   >    _**exportWallets**_ function writes all the wallets of the user to the directory where the jar file is located, creating a file called "wallets.skk".

    exportWallets

> Example Usage(console):

    >exportWallets


## help
   >    ***help*** function shows Help Menu. 
   >     With the help command you can quickly see how the commands work.

 















