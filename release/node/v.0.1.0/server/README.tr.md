![enter image description here](https://wallet.sikke.com.tr/static/images/sikke_client_logo.jpg)
# Sikke Node



**Sikke Platformu**; Sikke elektronik token (SKK) ve sikke platformunda oluşturulan diğer kripto birimleri ile ilgili transfer ve diğer işlemlerin yapıldığı, akıllı sözleşmelerin oluşturulup onaylandığı ve ileri tarihli işlemlerin yapılabildiği bir ekosistemdir.


## Başlarken
**Sikke Node**'u kullanmak için kendi kişisel bilgisayarınızda kullanmak için iki tane uygulama indirilmelidir. Bunlardan ilki **Sikke Node Server** sunucu uygulaması, diğeri ise bu sunucu ya bağlanıp ve işlem yapmak için kullanılacak **Sikke Node Client**  uygulamasıdır.

**Sikke Node Server** sunucusu **Java** tabanlı jar haline getirilmiş bir uygulamadır. Bu sunucu, kişisel bilgisayarınızda lokalde çalışmaya başladığında *http://localhost:9090/* adresine gelecek istekleri dinleyip uygun sonuçları üretip bunları istemciye gönderir. **Sikke Node Server** sunucusu ***tüm işletim sistemleri*** için kullanılabilecek çalıştırılabilir jar dosyasıdır. 

**Sikke Node Client** client uygulaması ise; **Sikke Node Server** sunucusuna istek atmayı sağlamak amacıyla **NodeJS** tabanlı bir istemci konsol uygulamasıdır. **Sikke Node Client** uygulaması işletim sistemine göre 3 tane çalıştırılabilir uyguladır. İşletim sisteminize uygun olan **Sikke Node Client** ile çalışılmalıdır.

Sikke Node'un nasıl kullanılacağı detaylı olarak aşağıda anlatılmıştır.

**Sikke Node** kendi kişisel bilgisayarınızda çalışır. **Sikke Node Server**'ı çalıştırmak için kişisel bilgisayarınız Java nın güncel versiyonunun kurulu olduğundan emin olunuz. Eğer Java'nın güncel versiyonu kişisel bilgisayarınızda yok ise; [Java'nın resmi sitesinden](https://www.java.com/en/download/) güncel versiyonu indirip kurmalısınız.

**Sikke Node Server**'ın ayakta ve çalışıyor olduğunu anlamak için tarayıcıdan veya Postman den http://localhost:9090/serverStatus adresine istek atmanız yeterlidir.

You can  [clone](https://github.com/sikke-official/sikke-nodes.git)  Sikke Node from Sikke Github Repository:

[https://github.com/sikke-official/sikke-nodes.git](https://github.com/sikke-official/sikke-nodes.git)

## Sikke Node Sunucusu Nasıl Çalıştırılır? (Windows, Linux, MacOS)

Java'nın güncel versiyonunu kişisel bilgisayarınıza kurduktan sonra Sikke Node Server'ı çalıştırabilirsiniz.

Sikke Node Sunucu'sunu aşağıdaki gibi çalıştırabilirsiniz;
 

 

 1. Yeni bir terminal(konsol) açınız.
 2. jar dosyasının bulunduğu dizine gidiniz.
 	> cd sikke-nodes\release\node\v.0.1.0\server
 
 
 3.    Aşağıdaki komut çalıştırınız  
	

     java -jar sikke.jar
     Sikke Node Server started at port: 9090



   

![enter image description here](https://github.com/sikke-official/sikke-nodes/blob/master/docs/images/sikke_console_java_jar.PNG)  

## Sikke Node Konsol Nasıl Çalıştırılır? 

Sikke Node Client'ten Sikke Node Server' a istek atmak için Sikke Node Client'ın işletim sisteminize uygun uygulamasının kullanılması gereklidir. Sunucuya istek atmak için aşağıdaki örneği inceleyiniz.
 

Sikke Node Konsolunu aşağıdaki gibi çalıştırabilirsiniz;
  
  

 1. Yeni bir terminal(konsol) açınız
  2. Console dosyasının olduğu dizine gidiniz ve aşağıdaki gibi çalıştırınız
   ***for Windows***
		> cd sikke-nodes\release\node\v.0.1.0\console\windows
		>`sikke register email:test.mail@gmail.com password:123456`
		
	 ***for Linux and MacOS***
		> cd sikke-nodes\release\node\v.0.1.0\console\linux
		>`./sikke register email:test.mail@gmail.com password:123456`

	

![enter image description here](https://github.com/sikke-official/sikke-nodes/blob/master/docs/images/sikke_console_client.PNG)

## Introduction

[JSON-RPC](https://en.wikipedia.org/wiki/JSON-RPC)   JSON'da kodlanmış uzaktan prosedür çağırma protokolüdür. **Sikke Node**'ta işlem yapmak için **Sikke Node Server**'a JSON-RPC formatında istek atmanız yeterlidir. JSON-RPC sunucu aşağıdaki gibi çalışır.

-   `http://localhost:9090/`  lokalden istek atmak için kullanılacak olan adrestir.

**Sikke Node Server**'a yapılacak olan tüm istekler **POST** olarak yapılır.
**Sikke Node Server** sunucusuna yapılacak olan isteklerde request'in body kısmına 4 tane parametre geçirilmesi zorunludur.

|Veri Objesi | Örnek |
|--|--|
| id |  e.g. "1"|
|jsonrpc |e.g. "2.0"  |
|method |  e.g. "getBalances"|
|params | e.g. ["1"] |

## Sikke Node Komutları
  

> `| means OR  ,  & means AND`
> `[]  zorunlu , () means Mandatory`, 
   ## register  

>   _**register**_  komutu **Sikke Wallet Sistemi** ne kayıtlı değilseniz kayıt olabileceğiniz komuttur ve iki tane zorunlu alan kabul eder. Bunlardan ilki sistemde işlem yapmak isteyen kişinin **email** adresi diğeri ise bu kullanıcının **şifre**sidir. Eğer **Sikke Node**'ta işlem yapmak istiyorsanız öncelikle sisteme kayıt olmalısınız, eğer sisteme kayıtlı değilseniz, sisteme kayıt olup cüzdan işlemlerinizi gerçekleştirebilirsiniz.

     register (email:Value), (password:Value)

|Parametre|Açıklama  |Zorunluluk
|--|--|--|
|email   | Yeni Kullanıcının Email Adresi | zorunlu
|password   | Yeni Kullanıcının Şifresi | zorunlu

> Örnek Kullanım(konsol):
> 

    >register email:test@gmail.com password:your_password

## login  

>  _**login**_ komutu **Sikke Wallet Sistemi'**nde zaten kayıtlı olan kullanıcının sisteme giriş yapmasını sağlayan komuttur ve iki tane zorunlu alan kabul eder. Bu zorunlu alanlardan ilki **email** diğeri ise **şifre**dir. **Sikke Node**'ta cüzdan işlemleri yapabilmek için sisteme giriş yapmalısınız.


     login (email:Value), (password:Value)

|Parametre|Açıklama  |Zorunluluk
|--|--|--|
|email   | Giriş Yapacak Kullanıcının Email Adresi | zorunlu
|password   | Giriş Yapacak Kullanıcının Şifresi Adresi | zorunlu

> Örnek Kullanım(konsol):

    >login email:test@gmail.com password:your_password

   

## logout  

> _**logout**_ komutu **Sikke Node**'a giriş yapmış kullanıcının sistemden çıkış yapmasınız sağlayan komuttur. Sistemden çıkış yapmış kullanıcı sistemde işlem yapmak isterse tekrar sisteme giriş yapmalıdır.

    >logout

   ## makeDefault  

>   The ***makeDefault*** komutu seçmiş olduğunuz bir cüzdanı varsayılan olarak işaretlemenizi sağlayan komuttur. Komut **SKK** ön eki ile başlayan **Sikke Cüzdan Adresi**'ni zorunlu parametre olarak kabul eder.

     makeDefault (address)

|Parametre|Açıklama  |Zorunluluk
|--|--|--|
|address   | SIKKE Cüzdan Adresi | zorunlu

> Örnek Kullanım(konsol):

    >makeDefault SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA


   
   

## importWallet
   >    ***importWallet***  komutu **Sikke Node**'ta bulunmayan cüzdanınızı **Sikke Node**'a dahil etmenizi sağlayan komuttur. Bu komut varolan cüzdanınızın **Sikke Cüzdan Private Key**'ini zorunlu parametre olarak kabul edip aynı cüzdanı **Sikke Node** içerisinde oluşturur.
  
     importWallet (privateKey)

|Parametre|Açıklama  |Zorunluluk
|--|--|--|
|privateKey   | Sikke Cüzdan Private Key | zorunlu

    

> Örnek Kullanım(konsol):

    >importWallet 8o4taasJhgRaXJb5T1opıpnhzKHFfFGPSZ2HcWEznnvybz

   

## listWallets
   > ***listWallets*** komutu Sikke Node'a giriş yapan kullanıcının **Sikke Node** sisteminde var olan cüzdanlarını ve bu cüzdanların bakiye bilgilerini listeler.

> Örnek Kullanım(konsol):
> 

    >listWallets

 

## getHistories
   >  _**getHistories**_  komutu iki modta çalışır. Bunların ilkinde hiçbir parametre girmezseniz sisteme giriş yapan kullanıcının tüm cüzdanlarının transaction kayıtlarının son 100 tanesini listeler. Diğer modta ise sadece bir parametre geçirilerek, geçirilen parametre tipine göre filtreleme yapılarak transaction listesi döner.



**I. Mod:**

    >getHistories
**II. Mod:**

    >getHistories ADDRESS|HASH|SEQ|BLOCK : Value 

|Parametre|Açıklama  |Zorunluluk
|--|--|--|
|ADDRESS/HASH/SEQ/BLOCK : Value| Tek Bir Parametre Girilmeli | zorunlu

>      Örnek Kullanımlar(konsol):
> 

**I. Mod:**


    >getHistories

**II. Mod:**

 
 > ***ADDRESS:Value***

    

    >getHistories ADDRESS:SKK1QBepiMsdrBBX6cy9E7wKtjwZAkWvFmJmA

> ***HASH:Value***

    >getHistories HASH:8bce4ekkdl557678b9eb41f934654646595fd7b427032f2f48d9ddfgggfb7ea326791kk8dj  

> ***SEQ:Value***

    >getHistories SEQ:10001
> ***BLOCK:Value***

    >getHistories BLOCK:10000



   ## createWallet
   >   _**createWallet**_ komutu giriş yapan kullanıcı için **Sikke Node** sisteminde **SKK** ön ekiyle başlayan yeni bir Sikke cüzdanı lokalde oluşturur.
Bu komut cüzdan için bir takma ad(*aliasName*) parametresini isteğe bağlı olarak kabul eder.


     createWallet aliasName

|Parametre|Açıklama  |Zorunluluk
|--|--|--|
|aliasName   | Sikke Cüzdanı Takma Adı | isteğe bağlı

    

> Örnek Kullanımlar(konsol):
> 

    > createWallet 
    > createWallet your_sikke_wallet_alias_name

 

## createWalletAndSave

_**createWalletAndSave**_  komutu giriş yapan kullanıcı için **Sikke** sisteminde **SKK** ön ekiyle başlayan yeni bir Sikke cüzdanı oluşturur. Bu oluşturulan cüzdan tüm Sikke sisteminde (**Sikke Web Wallet, Sikke Mobile Wallet, Sikke Node**) görünür. Bu komut sadece isteğe bağlı parametreler kabul eder.Yaratılan cüzdan ile tüm Sikke sisteminde işlem yapabilirsiniz.

     createWalletAndSave ALIAS_NAME|LIMIT_HOURLY|LIMIT_DAILY|LIMIT_MAX_AMOUNT|DEFAULT : Value

|Parametre|Açıklama|Zorunluluk
|--|--|--|
|ALIAS_NAME| Sikke Cüzdanı Takma Adı | isteğe bağlı
|LIMIT_HOURLY| Sikke Cüzdanı Saatlik Transfer Limiti | isteğe bağlı
|LIMIT_DAILY|Sikke Cüzdanı Günlük Transfer Limiti|isteğe bağlı
|LIMIT_MAX_AMOUNT| Sikke Cüzdanı Maximum Transfer Limiti | isteğe bağlı
|DEFAULT| Sikke Cüzdanı Default Olarak İşaretleme | isteğe bağlı


> Örnek Kullanımlar(konsol):

    > createWalletAndSave  ALIAS_NAME:my_wallet_name
    > createWalletAndSave  DEFAULT:1
    > createWalletAndSave  ALIAS_NAME:my_wallet_name LIMIT_HOURLY:100 LIMIT_DAILY:1000 LIMIT_MAX_AMOUNT:2000
    > createWalletAndSave  ALIAS_NAME:my_wallet_name LIMIT_HOURLY:100 LIMIT_DAILY:1000 LIMIT_MAX_AMOUNT:2000 DEFAULT:1



   
   ## mergeBalance
   >    _**mergeBalances**_  komutu iki moda sahiptir. İlkinde hiç bir parametre girmezseniz giriş yapmış olan kullanıcının tüm cüzdanlarının tüm varlık tiplerindeki bakiyelerini daha önceden varsayılan olarak işaretlediği cüzdan üzerinde birleştirir. İkinci modta ise sadece **varlık tipi** veya sadece **Sikke cüzdan adresi** veya **her ikisi birden** girilebilir.
   

 - Sadece **varlık tipi** girilirse; giriş yapmış olan kullanıcının tüm cüzdanlarının seçilen varlık tipindeki bakiyeleri *varsayılan olarak işaretlediği cüzdan üzerinde birleştirilir.*
   
 - Sadece **Sikke cüzdan adresi** girilirse; giriş yapmış olan kullanıcının tüm cüzdanlarının tüm varlık tipindeki bakiyeleri *seçtiği cüzdan üzerinde birleştirilir.*
 - Hem **varlık tipi** hem de **Sikke cüzdan adresi** girilirse; giriş yapmış olan kullanıcının *tüm cüzdanlarının seçilen varlık tipindeki bakiyeleri seçilen cüzdan üzerinde birleştirilir.* 



**I. Mod:**

      > mergeBalances
   

**II. Mod:**
 

     > mergeBalances [address],[asset]

   

|Parameter|Açıklama  |Zorunluluk
|--|--|--|
|address| Sikke Cüzdan Adresi | isteğe bağlı
|asset| Varlık Tipi | isteğe bağlı

> Örnek Kullanımlar(konsol):

**I. Mode:**

    > mergeBalances
    
**II. Mode:**

     > mergeBalances asset:SKK
     > mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
     > mergeBalances address:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x asset:SKK

   ## getBalances
   >    _**getBalances**_ komutu iki tane isteğe bağlı parametre kabul eder. Bunlardan ilki **Sikke cüzdan adresi** diğeri **Varlık tipi**dir. Bu komut giriş yapmış olan kullanıcının girilen kriter/kriterlere göre cüzdanlarının bakiyelerini döner.

     > getBalances sikkeWalletAddress |& asset

|Parameter|Açıklama  |Zorunluluk
|--|--|--|
|sikkeWalletAddress| Sikke Cüzdan Private Key | isteğe bağlı
|Varlık Tipi| asset (SKK,XTG,UPC,OKO,..) | isteğe bağlı


> Örnek Kullanımlar(konsol):

    > getBalances
    > getBalances SKK 
    > getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x
    > getBalances SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x SKK
    > getBalances SKK SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x


 ## send
   >   **send** komutu girilen **Sikke cüzdan adresi** ne girilen **miktar** kadar bakiye göndermek için kullanılır. İki tane zorunlu parametre kabul eder. Bunlardan biri **Sikke cüzdan adresi** diğeri ise gönderilecek **miktar**dır.
Eğer gönderen cüzdan adresi girilmezse, varsayılan olarak işaretlenen cüzdan gönderici cüzdan olarak belirlenir..
Eğer varlık tipi girilmezse, varsayılan varlık tipi SKK varlık tipi olarak belirlenir.
Eğer göndermeişlemi gizli(hidden) olarak belirlenirse, bu gönderme işlemi ağda gizli olarak gönderilir.


     send FROM|TO|ASSET|AMOUNT|DESC|HIDDEN : Value

|Parametre|Açıklama  |Zorunluluk
|--|--|--|
|FROM| Gönderici Cüzdan Adresi | isteğe bağlı
|TO| Alıcı Cüzdan Adresi | zorunlu
|ASSET| Varlık Tipi(SKK,XTG,UPC,OKO,...) | isteğe bağlı
|AMOUNT| Gönderilecek Miktar| zorunlu
|DESC| Açıklama| isteğe bağlı
|HIDDEN| Gizli Gönderme | isteğe bağlı


> Örnek Kullanımlar(konsol):

    >send TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
    >send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100
    >send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100 ASSET:UPC
    >send FROM:SKK23h6BPxWhJS11j5UJ1Mdf6Z2UkGG4P5nfc TO:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x AMOUNT:100 ASSET:UPC DESC:My_Transfer HIDDEN:1

   


   ## syncWallet
   >    


    

> ***syncWallet*** has two modes. 
> 
>In the first of these modes, if any parameters are not entered, the system user's **SIKKE CLIENT** is synchronized with all the wallets **SIKKE API** that are in the local database. 
>In the second of these modes, a wallet address and information created with createWallet is synchronized with the SIKKE API.
After that, the wallet that was previously only available in the local database becomes visible on the entire SIKKE system (**SIKKE Web Wallet**, **SIKKE Mobile Wallet**, **SIKKE Client**).
> 
> 


#### First Mod:

     syncWallet

> Example Usage(console):

    >syncWallet

#### Second Mod:

    syncWallet ADDRESS|ALIAS_NAME|LIMIT_DAILY|LIMIT_HOURLY|LIMIT_MAX_AMOUNT:Value

|Parameter|Definition  |Obligation
|--|--|--|
|ADDRESS| SIKKE Private Key Address | mandatory
|ALIAS_NAME| Alias Name | optional
|LIMIT_DAILY| Daily Transfer Limit | optional
|LIMIT_HOURLY| Hourly Transfer Limit | optional
|LIMIT_MAX_AMOUNT| Max Transfer Limit | optional

    

> `Example Usages(console):`
> 
    >syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x 
    >syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x LIMIT_DAILY:100 LIMIT_DAILY:1000
    >syncWallet ADDRESS:SKK17h6BPxWhJS54j5UJ1Mdf6Z2UkGG4P5n4x ALIAS_NAME:My_Wallet


 ## importWallets
   >    _**importWallets**_ komutu **Sikke Node Server** içerisindeki *jar dosyasıyla aynı dizindeki bulunan* "wallets.skk" adlı dosyanın içindeki cüzdanları **Sikke Node** içerisine aktarmayı sağlayan komuttur. Bu işlem için de kullanıcının sisteme girş yapmış oloması gerekir.İçe aktarılan cüzdanlar giriş yapan kullanıcı ile ilişkilendirilir. Dosyada bilgisi bulunan cüzdanlar eğer **Sikke Node** içerisinde mevcutsa bilgisi güncellenir, mevcut değilse **Sikke Node** sistemine kaydedilir. Eğer bu dosya jar ile aynı dizinde bulunamazsa "dosya bulunamadı." hatasını döner.

     > importWallets


> Örnek Kullanım(konsol):

    > importWallets

 ##  exportWallets
   >    _**exportWallets**_  komutu giriş yapmış olan kullanıcının  **Sikke Node** içerinde bulunan tüm cüzdanlarını **Sikke Node Server** içerisindeki jar dosyasının bulunduğu dizine "wallets.skk" adlı dosyaya dışa aktarır. 

     > exportWallets

> Örnek Kullanım(konsol):

    > exportWallets


## help
   >    ***help***  komutu, **Sikke Node** içindeki komutların nasıl çalıştığını konsol ekranından hızlı bir şekilde görmek için Help Menüsü döner.

 








