title: GnuPG 基本觀念與指令介紹
date: 2022-05-17 00:25:27
tags:
    - geek
    - linux
---

GnuPG 應該是絕大多數 Linux user 或是開發者都用過的工具。說起來真不好意思，十多年前剛開始工作的時候就用過它，懵懵懂懂查了幾個常見指令就擺著不放。現在終於振作起來，稍微多花點時間，重新認識這個工具

<!-- more -->

看完文件之後覺得最後的結論很簡單，但是使用前要弄清楚的一些基本觀念四散在不同的文件，而且 gpg 的指令介面也不是很友善，使得一開始的門檻弄得有點高。我試著整理出我覺得比較容易理解的順序

# Gpg 使用層的基本邏輯

眾所皆知，Gpg 是用來做加密、簽章等等功能的工具，從使用的角度來看，我們可以這麼理解它的邏輯

* 抽象概念上，Gpg 是以「人的身份」為主體來互動
* 一個人可能有不同的身份，譬如說工作上的名字叫 Julian，在社群裡面互動又叫 Walkingice
* 你可以把這些身份跟你本人合併起來，也可以創造出一個全新的身份，達到匿名的效果
* 彼此簽章的時候，實際上是在做「這個人宣稱這個身份為他所有，我也同意這個說法」
* 每一個身份就是由一份 Primary Key pair 與數個 Uid 組合而成
* 每一個 Primary Key pair 底下可以掛許多個 subkey

畫成圖例就有點像這樣

<div style="max-width: 100%;" class="img-row">{% asset_img gpg.svg %}</div>

舉例來說，我身為一位中年大叔
* 我可以生出一組 Primary Key pair，用來當作我在社會活動的身份
    * 從工作開始認識我的朋友，習慣叫我 Julian
        * 所以我可以在這個 Primary Key pair 底下創造一個 uid 為 `Julian`
    * 從社群開始認識我的朋友，習慣叫我走冰
        * 所以我可以在這個 Primary Key pair 底下創造一個 uid 為 `Walkingice`
    * 兩邊的朋友都知道叫我 Julian 或是 Walkingice，不管用哪個稱謂都沒問題
    * 任何一邊的朋友，簽章認證我這份 Primary Key pair，是在認同我這個身份
        * 換句話說：「我同意這個 Primary Key pair 的所有人是 Julian，也就是 Walkingice」
* 但是我可能在大家不知道的地方，其實是個有變裝癖，並以此賺取斗內為生的肥宅，而這個身份我不想給任何親友知道
    * 所以我會生出另外一組 Primary Key pair，用來當作我在賺皮肉錢的身份
        * 在這個 Primary key 底下，我可以創造一個 uid 為 `FatTuber`
        * 簽章認證我這個身份的人，實際上是說：「我同意這個 Primary Key pair 的所有人是 FatTuber」

所以我們可以看到幾個特點

* 每個人可以創造出不同的身份，各個身份擁有的網絡是獨立的
* 每個身份要不要跟你的社會關係做連結，由你決定。
* 簽章認證並不表示「這個人真的是那個身份」，充其量只能說「這群人同意他所宣稱的身份」
    * 譬如說我也可以宣稱我是 `Tony Stark<tony@avenger.universe`，而且我身邊一堆人還真的簽章同意，但我很明顯不是
    * Gpg 運作的邏輯就像一般社會一樣，所以你要慎選你的朋友，也要仔細斟酌你朋友說的話

使用 Gpg 就像在真實社會裡面運作一樣，你要重視自己某個身份的 reputation，並且好好經營它，才能夠在這個分散式的架構裡面作到身份的認證。

# Gpg 裡面常見的名詞解釋

* Key pair
    * public key 跟 private key 的組合

* Primary Key pair / Master Key pair
    * 一份人際網絡關係就由一份 Primary Key pair 來管理
    * 大多數情況都只要一份 Primary Key pair 就好，你的個人聲望都會累積在此
    * Primary Key pair 的 private key 相當重要

* UID / User ID
    * UTF-8 字串，由一個名字與 e-mail 組合而成，像是 `FooBar (Some comment) <foobar@the.addr>`
    * 在任何指令需要透過 UID 指定或是搜尋的時候，不用全部打完，通常用一部分也可以配對成功
    * 每一組 Primary Key pair 底下可以有好幾個 UID
        * 你可能因為換工作而有不同的 mail address

* Subkey pair
    * 因為 Primary key pair 很重要，需要好好藏起來。於是產生其他的 key pair 並且掛在 primary 底下，所以稱為 subkey pair
    * 在一組 Primary key pair 之下，可以掛上許多不同的 subkey 去做各種簽章或加密的實際動作
    * Primary key pair 跟 subkey pair 的資訊安全強度相同 (如果產生的參數一樣)

* Key ID
    * 16 進位的字串，用來鑑別一個 Key
    * 又稱 long key id
    * 如果只抓最後一部分 16 個字元來看，這個字串就叫 **fingerprint**
    * gpg 取最後 8 個字元來當 fingerprint，又稱 short key id

* KeyGrip
    * 用在 gpg 內部的東西，20 bytes 的 SHA1 字串
    * 用處跟 fingerprint 有點像
    * 與 protocol 獨立，是用 key 的公開資訊算出

* Signing, Certification, Authentication, Encryption
    * Signing: 用 keypair 對某一份資料簽名，讓其他人能夠確認是否看到跟我一樣的資料
    * Certification: 對別人的一組 key 做 signing，就是 Certification / Certify
        * 只有 Primary private key 可以 certify 其他 key
    * Authentication: 認證，就像常見的 ssh login
    * Encryption: 用 key 對資料作加密

# 與查詢有關的指令

很多操作都需要用 id 去定位某個 key，所先介紹一下查詢的指令

* 列出 Primary key pair 的 key id
    * `$ gpg -K`
    * 如果想看其他人的 pubkey，就用小寫 k `$ gpg -k`

```
$ gpg -K
sec   ed25519 2022-05-12 [SC]
      A12DA4269CD15837F1D1DFF844269415E755ADA0
uid           [ultimate] Foobar (Primary Key for testing GPG) <foobar@testing.gpg>
uid           [ultimate] SecondUid (third) <second@outerspace>
ssb   rsa1056 2022-05-12 [S] [expires: 2022-08-20]
ssb   dsa2048 2022-05-12 [S]
ssb   rsa2016 2022-05-12 [E] [expires: 2023-05-12]

```

上面給的資訊少得可憐。既然我們經常要用到 subkey，那麼要用什麼指定 subkey？

* 列出 subkey 的 fingerprint
    * `$ gpg -K --keyid-format long`
    * `$ gpg -K --with-subkey-fingerprint` 這個也行，列出比較長的 id

```
$ gpg --keyid-format long
sec   ed25519/44269415E755ADA0 2022-05-12 [SC]
      A12DA4269CD15837F1D1DFF844269415E755ADA0
uid                 [ultimate] Foobar (Primary Key for testing GPG) <foobar@testing.gpg>
uid                 [ultimate] SecondUid (third) <second@outerspace>
ssb   rsa1056/2C4FAFA05BC34CC8 2022-05-12 [S] [expires: 2022-08-20]
ssb   dsa2048/96679AF60453EA0B 2022-05-12 [S]
ssb   rsa2016/8AD8E8D104841669 2022-05-12 [E] [expires: 2023-05-12]
```

上面的範例，2022/05/12 就會過期的 RSA key 的 fingerprint 就是 `2C4FAFA05BC34CC8`，後面會經常用到 subkey fingerprint

* 列出 keygrip
    * `$ gpg -K  --with-keygrip`
    * 應該是 Gpg 2.1 之後，private key 就會存在 `private-keys-v1.d/[KeyGrip].key`，所以要看 keygrip 才知道是對應那一把 private key

```
$ gpg --homedir=. -K  --with-keygrip
/home/walkingice/temp/gpg/pubring.kbx
-------------------------------------
sec   ed25519 2022-05-12 [SC]
      A12DA4269CD15837F1D1DFF844269415E755ADA0
      Keygrip = 9636425F6158C1C242C52C79AFF07DB2A76637C8
uid           [ultimate] Foobar (Primary Key for testing GPG) <foobar@testing.gpg>
uid           [ultimate] SecondUid (third) <second@outerspace>
ssb   rsa1056 2022-05-12 [S] [expires: 2022-08-20]
      Keygrip = 80ACB61CD079B38A4FBE6B9511BAD1FF90CD1D10
ssb   dsa2048 2022-05-12 [S]
      Keygrip = 0E3D334598E9A2A46450F52035A30348D85776A1
ssb   rsa2016 2022-05-12 [E] [expires: 2023-05-12]
      Keygrip = E7B8746A7A51D69486938458A9B3C68E105791C3
```

# 管理 Primary key 與 Subkey

網路上其他地方已經很多教學了，我這邊只大概列出常用到的指令當作備忘

* 新增 primary key pair
    * `$ gpg --gen-key`
    * `$ gpg --expert --full-gen-key` 用更專家的模式來產生 key

* 刪除 primary key pair
    * `$ gpg --delete-secret-keys [UID]` 先刪除 private key
    * `$ gpg --delete-keys [UID]` 接著刪除 public key

* 編輯 subkey
    * subkey 是掛在 primary key 底下，所以操作都是從「編輯 primary key」出發
    * `$ gpg --edit-key [UID]` - 編輯這個 UID 所關聯的 primary key
        * `gpg --expert --edit-key [UID]` - 用專家模式可以有更多操作選項
    * 新增
        * `gpg> addkey`
    * 刪除
        * `gpg> key [SUBKEY fingerprint]` - 特別指定要對這個 subkey 操作
        * `gpg> delkey`
    * 撤銷/Revoke key
        * `gpg> key [SUBKEY fingerprint]` - 特別指定要對這個 subkey 操作
        * `gpg> revkey` - revoke 之後記得要重新 export public key 告知其他人

* 匯出並刪除 primary private key
    * 匯出 primary private key 到檔案 `primary_private_key.gog` 並且拿到別的地方好好保存
    * `$ gpg -o primary_private_key.gpg --armor --export-secret-keys [KEY_ID]`
    * 備份完備，接著在 gpg 管轄範圍內刪除 primary private key
    * `$ rm private-keys-v1.d/[Primary KeyGrip]` - v2.1 之後直接砍掉檔案就行了
    * 確認一下，打 `$ gpg -K` 會看到 private key 前面的 `sec` 變成 `sec#`

* 匯出 subkey private key
    * 如果你想要備份 subkey 的 private key，匯出的方式會有點不同
    * `$ gpg -o sub_prikeys.gpg --armor --export-secret-subkey [KEY_ID]` - 匯出所有的 subkey private keys
    * `$ gpg -o sub_prikeys.gpg --armor --export-secret-subkey [SUBKEY_FINGERPRINT]!` - 只匯出某一把 subkey private key，注意那個驚嘆號

* 匯入 primary private key
    * 需要把 primary private key 找回來的時候，把之前匯出的檔案重新匯入即可
    * `$ gpg --import primary_private_key.gpg`

為什麼要麻煩地把 primary private key 藏起來？

這把 key 可能擁有你經年累月所累積的聲望，用這把 key 簽署過幾百人，也可能跟幾百人都交換過。如果哪天要出差去高風險的地方，電腦可能會遭竊或是被破壞，我們不會希望這把私鑰就這樣毀於一旦。利用 subkey 的機制，可以把 primary key 在大多數情況都藏起來，主要使用 subkey 來加密或是簽名。即使 subkey 遺失或是被竊盜，還能夠請出最權威的 primary 來宣稱某把 subkey 已經失效

[Debian 這篇 Subkeys](https://wiki.debian.org/Subkeys) 解釋了原因，並且列出哪些情況才會需要用到 primary private key

* 你要簽署認同別人的 key，或是要撤銷手上的某個 key
    * when you sign someone else's key or revoke an existing signature,
    * when you revoke or generate a revocation certificate for the complete key
* 你要編輯 UID
    * when you add a new UID or mark an existing UID as primary,
    * when you change the preferences (e.g., with setpref) on a UID,
* 你需要編輯 subkey
    * when you create a new subkey,
    * when you revoke an existing UID or subkey,
    * when you change the expiration date on your primary key or any of its subkey, or

所以建構 key 的流程會有這種方式

1. 產生一組 primary key pair
    * 因為不會直接拿它做檔案加密所以不用擔心效率，選定最難的加密演算法，長度用到最大
1. 產生幾組專門拿來加密或是簽名的 subkey
1. 匯出 primary private key 將其好好保存
1. 刪掉 gpg 裡面的 primary private key
1. 使用 subkey 做常見的工作
1. 需要用到 primary private key 的時候，才暫時將其匯入，用完再刪掉

# 跟別人交換 key

用 gpg 終究不會是自己一個人玩，總是要跟別人交流。所以雙方會交換 pubkey 放到自己的 gpg database 裡面

* 匯出自己的 pubkey
    * `$ gpg --output my-pubkey.gpg --export [UID]` - 匯出所有的 pubkeys
    * `$ --output my-single-pubkey.gpg --export [SUBKEY_FINGERPRINT]!` - 只匯出某個 subkey 的 pubkey

* 匯入別人的 pubkey
    * `$ gpg --import friend_pubkey.gpg`

單單把別人的 pubkey 放進自己的 database 還不夠，還需要用我們自己的人格去保證這把 pubkey 是不是真的由某個認識的人發出


* 確認匯入的 pubkey
    * `$ gpg -k` 確認一下「朋友」的 pubkey 已經放進去，假設「朋友」的名字是 `the_friend`
    * `$ gpg --edit-key the_friend` - 開始編輯「朋友」的 key
    * `gpg> fpr` - 確認一下「朋友」的 fingerprint 對不對
    * `gpg> uid friend_the_one` - 「朋友」有很多 UID，我想針對 `friend_the_one` 這個 UID 做簽名
    * `gpg> sign` - 進行簽名認證，或是用 `lsign` 只在 local 端簽名。確認這份 pubkey 就是由「朋友」所擁有
    * `gpg> save`

如果你需要親自跟每個 gpg user 見面，確認對方的身份之後才簽名認證，長久下來效率低落。可以利用透過其他人認證過的結果來簡化流程。譬如說有個叫 Kenji 的人做事總是不仔細，所以他認證過的人我都不願意相信。但是我完全無條件相信「朋友」所認證的任何人，所以我可以針對 Kenji 跟「朋友」設定不同的 trust 程度

* 修改 trust
    * `$ gpg --edit-key the_friend`
    * `gpg> trust`
    * `gpg> save`

但是時間久了，要怎麼知道我們所設定的 trust 程度呢？edit key 的時候就能看見

```
pub  rsa3072/58C7791869454109
     created: 2022-05-12  expires: 2024-05-11  usage: SC
     trust: ultimate      validity: ultimate
```

上方的例子，trsut 的程度就是 `ultimate`，「朋友」的話就是對的！👆

交換 public key 如果全部都要用 e-mail 之類的方法傳遞，那就太累人了。通常就會找一個 keyserver，大家把 public key 傳上去，或從那邊抓下某個人的 public key，確認無誤之後再 sign。又或著，你對自己的 key 做出什麼變更，也可以傳上 server。別人就能從這個 server 抓下你的更新

* 送出 public key 到 server
    * `$ gpg --send-keys [KEY_ID]` - 送出這一組的 public key
        * 但有時候會遇到 `gpg: keyserver send failed: Server indicated a failure` ，那可能就是你指定的 keyserver 掛了。
        * ubuntu 的 key server 好像也滿常用到
        * `$ gpg --keyserver keyserver.ubuntu.com --send-keys [KEY_ID]` - 送出這一組的 public key 到 ubuntu key server
        * 如果你想以後都用 ubuntu 的 key server，可以在 `~/.gnupg/gpg.conf` 裡面加上 `keyserver hkp://keyserver.ubuntu.com`
* 從 server 下載 public key
    * `$ gpg --recv-keys [KEY_ID]`

# 加密、解密與簽名

前面千辛萬苦終於把 keys 都搞定了，現在開始真正使用 gpg 來做正事

* 針對某個文字檔案做簽名
    * 產生一個新的可讀文字檔案，把原檔的東西放進去，同時也把簽章的資訊放進去
    * `$ gpg --clear-sign [Filename]` - 不影響原檔案，額外產生 `[Filename].asc`
    * `$ gpg -u [SUBKEY_FINGERPRINT]! --clear-sign [Filename]` - 如果有多個 subkey，使用驚嘆號指定某個 subkey 來 sign，
        * 也可以在 `gpg.conf` 裡面寫上 `default-key [SUBKEY_FINGERPRINT]!` 當作預設使用的 subkey
    * 驗證簽名
        * `$ gpg --verify [Filename].asc`

* 僅僅只產生一個額外的簽章資訊
    * 對方收到檔案之後，再根據這個簽章的資訊驗證檔案是不是真的來自你
    * `$ gpg --sign --detach-sign [Filename]`   產生 `[Filename].sig`，把簽名跟檔案本體分開來
    * 驗證簽名
        * `$ gpg --verify [Filename].sig [Filename]`

* 對稱式加解密
    * 就是一般常見的用密碼加密，其實要用這招的話，也不需要搞一堆 key 了
    * `$ gpg -c [Filename]`
    * 或是 `$ gpg --symmetric [Filename]`
    * 會產生 `[Filename].gpg`

* 用對方的 public key 加密
    * `$ gpg --encrypt -r [UID] [Filename]` - 指定 recipient 為 UID，會產生加密檔案 `[Filename].gpg`
    * 如果對方有很多把 subkey，想要用其中一把 subkey 加密
    * `$ gpg --encrypt -r [Recipient's Public Subkey Fingerprint]! [Filename]` - 注意 subkey 後面有個驚嘆號，驚嘆號就是用來指定 fingerprint

* 用自己的 private key 加密，讓對方可以用我的 public key 解密
    * 其實，這就是對這個檔案做簽章，並且不要把簽章的資訊額外放到別的檔案
    * `$ gpg --sign [Filename]`

* 如果手上有對方的 pubkey，不想要把對方的 pubkey 加到 database 裡面，也能用 pubkey 檔案來加密
    * `gpg --encrypt -f [Recipient PubKey Filename] [Filename]`
        * 如果這個檔案包了好幾個 pubkey，那麼 gpg 應該會用最新的那個
        * 所以對方最好是只給一個 pubkey

# 檔案管理與備份

除了使用 gpg，妥善地管理檔案以及備份也很重要。不過我個人比較懶惰，通常是直接備份整個工作目錄，比較挑惕的人可以稍微理解一下目錄底下哪些檔案是做什麼的

gpg 的預設工作目錄是 `~/.gnupg`，也能由 `--homedir` 或是環境變數 `GNUPGHOME` 來指定其他地方為工作目錄。我在讀文件的時候，就是用 `--homedir` 來練習。不過要注意，gpg 會跑起一個 daemon `gpg-agent`，它會暫存一些資訊，譬如說輸入的 passphrase，有時候需要砍掉這個 daemon 清光 cache。

在 2.1 之後就不用 `secring.gpg` 這個檔案了。分開維護 public keyring 跟 private keyring 常常會引起問題，所以在 gpg 2.1 之後，全部把工作都交給了 gpg-agent (真正的 encryption engine)來做

* `pubring.kbx`
    * **需要備份**
    * public key box
    * v2.1 之後新增的 https://www.gnupg.org/faq/whats-new-in-2.1.html#nosecring
    * 給 `gpgsm` 共享使用。(`man gpgsm`)，所以這個檔案可能在早就被產生了
* `openpgp-revocs.d`
    * **需要備份**
    * 裡面的東西可以用來 revoke 正在使用的 key，所以最好分開存放，或是印出來收藏
* `private-keys-v1.d`
    * **需要備份**
    * `gpg-agent` 會把 private key 存在這裡面
* `trustdb.gpg`
    * 不需要備份
    * trust database
    * 真的要備份，會用 `--export-ownertrust` 匯出檔案

# 後話閒談

前一陣子在想，數位資產的所有權(暫且先不管「所有權」的嚴格定義)認證目前都是綁定在網站的帳號之上，可是我們註冊帳號的時候往往要提供一個 e-mail，這年頭許多人(包括我)都是直接用 gmail。如果我的 gmail 帳號因為任何原因而被停權，也代表我有風險失去在某個網站上面的數位資產。如果我想要自己架 mail server 給自己發 e-mail 帳號，實際上我的 domain name 也是被某個網路巨頭所掌控。如果想要證明登入的人真的是「我」，似乎都要被某個網路巨頭箝制？

「如何在網路上證明自己的身份，而不倚靠大公司的服務？這不就是 GnuPG？！」是啊，這個問題早就有個解法了。

最近幾年大家對「網路巨頭」很不信任，不過我身為這個行業裡面的一員，雖然有點提防但也沒有到很緊繃的程度，對於民主國家裡面比較有聲譽的大公司，其實我還是有一定程度的信任，也因此我對「完全去中心化」這種目標並不是很有興趣。

即便如此，我還是強烈同意並主張，**人們應該要有個可以驗證自己的方法，不受大公司的限制**

用 gpg 的例子來說，就是我可以用 gpg subkey 來註冊帳號，而非使用 e-mail。註冊帳號只是例子，其實泛指的是任何需要 authenticate 的事情。這只是個理想，而現實的其中一個關卡就是，gpg 真的很難用。

Gpg 指令難用之一在於，它會故作聰明地幫你找一堆 fallback，經常你搞錯 keyid 或是 fingerprint，忘記加上驚嘆號都會動，或著在 conf 裡面設定錯誤的 default-key，這些弄錯都還是會動，但是結果跟你預期的不大一樣。從大學就喜歡用 Linux command line 的我，依然覺得 gpg 的指令很反人類。它的功能很完整，熟悉的人可以很好地使用它。但是在那之前，gpg 混亂的文件說明與複雜的指令，沒有強大耐心的人應該都沒動力弄懂 gpg，多半就只有拿來 gen key 而已。


# 參考連結

* [RFC4880: OpenPgp Message Format](https://datatracker.ietf.org/doc/html/rfc4880)
* [Anatomy of a GPG Key](https://davesteele.github.io/gpg/2014/09/20/anatomy-of-a-gpg-key/)
* [中文：GnuPG Keysigning Party HOWTO](http://jedi.org/blog/archives/gpg_party_zh_tw.html)
* [知乎：使用 GPG Key 来构建签名、加密及认证体系](https://zhuanlan.zhihu.com/p/481900853)
* [知乎：关于GnuPG的subkey（子密钥）的使用](https://zhuanlan.zhihu.com/p/24103240)
