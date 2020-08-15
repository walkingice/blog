title: 實作 Google Authenticator 的兩階段驗證
date: 2020-08-16 02:22:34
categories: geek
tags:
    - android
    - kotlin
---

之前聽到 Tim 說 PTT 的現有的登入方式不改的話很沒救，我想到兩階段驗證的方法，所以好奇研究了一下該怎麼做。實際上看了才知道比想像中簡單，PTT 有意願的話，實作難度真的不高。

Google Authenticator (後面簡稱 GA)是常見的兩階段驗證(2FA)會用到的程式，好比 GitHub 或是 Facebook 的兩階段驗證都能用這隻程式取得驗證碼。如果你的網站服務想要利用 2FA 增加安全性，利用 GA 可以算是非常便宜的方案 - 不需要自己寫 client App，只要自己的網站加上一些簡單的流程與演算法就能取得 2FA 的優點。

本文簡介如何實作，並且附上驗證的程式。

<!-- more -->

要實作 GA 的 2FA，核心的部分只要弄好三個東西

* RFC4226：HOTP: An HMAC-Based One-Time Password Algorithm 的實作
* RFC6238：TOTP: Time-Based One-Time Password Algorithm 的實作
* Base32 的編碼演算法

其中 RFC6238 其實是基於 RFC4226 的實作，只是拿時間當變數而已，所以搞定 RFC4226 就沒什麼大問題，以下拿 Kotlin 語言，依序介紹三者如何實作。*(其實我覺得看圖可能就懂了)*

# 準備

2FA 的概念是，使用者登入的時候除了輸入密碼，還要用私人擁有的裝置(手機)顯示一個隨機變動的數字，登入時提供給 Server 以宣稱「我除了知道密碼，還知道一個只有我手機才會產生的數字」，以此提昇帳號的安全性。要做到後者，Client 跟 Server 之間一定要共享某一個 Key，還有一個不斷變動的 Counter ，由這兩個參數生出隨機亂數。

因此在起始 2FA 流程的時候，網站首先要給使用者產生隨機的文字當做 Key，以下都拿經典的台詞 **GoAheadMakeMyDay** 當做產生出來的 Key，後面的程式碼會用到的 **key** 都是這個字串。

把這串 Key 的 ascii 直接轉成 byte 會拿到 `[47 6f 41 68 65 61 64 4d 61 6b 65 4d 79 44 61 79]`

# RFC4226

以下稍微解釋 RFC 裡面講的東西，沒耐心的可以跳到下一節

RFC 的標題就是 HOTP: An HMAC-Based One-Time Password Algorithm。MAC, Message Authentication Code 是用來驗證訊息的另外一串比較短的訊息，HMAC 則是基於 hash function 的 MAC。舉例來說 HMAC-MD5 就是我們常用的 md5，對於一個大的檔案，產生一個比較小的 hash code (MAC)，可以用來驗證原來的大檔案有沒有出錯。

RFC4226 的第五章講演算法，看完就知道怎麼實作了。但是要用在自家服務上的人，至少要把第六章也看完，才會知道 RFC4226 要基於哪些條件才會有安全性。不過我這篇短文，聚焦在演算法就好

Section 5.1 有些簡單的名詞解釋

* C: 8 bytes 的 counter value，又稱 moving factor，HOTP generator (client，也就是使用者手上的手機) 跟 HOTP validator (server) 要同步這個值。譬如說兩邊可以用 1, 3, 5, 7 的奇數遞增，或是 100, 200, 300...，反正兩邊有共識就行了。(如果你很聰明地拿「時間」來當兩邊同步的基準，就是 RFC6238 的 TOTP 囉！)

* K: shared secret，長度至少要 128 bits，也就是上面假設的 key (GoAheadMakeMyDay)
* T: throttling parameter，嘗試 T 次失敗之後會拒絕連線 (總不能讓黑鬼無限制地猜下去吧！)

K, C 都是 high order byte first，產生出來的都是 big endian，也就符合我們平常的習慣

```
HOTP(K,C) = Truncate(HMAC-SHA-1(K,C))
```

1. HS = HMAC-SHA-1(K,C) - HS 就是 HMAC-SHA-1，長度為 20 bytes
1. 產生一個 4 bytes 長度的值：Sbits = DT(HS) - DT 會回傳一個 31 bits String
1. Snum = StToNum(Sbits) - 把 S 轉成一個數字，範圍是 0 ~ 2^31 -1
1. 回傳 D = Snum mod 10^Digit - D 也是一個數字，範圍是 0 ~ 10^D - 1

DT 是為了要從 160 bits 之中取出 4 bytes dynamice binary code

DT: 當 String = String[0] ~ String[19] 時，OffsetBits 就是 lower order 4 "bits"，也就是 String[19] 的最後 4 bits (if String[19] = 0x4A, offset 就是 0xA)
Offset = StToNum(OffsetBits) // `0 <= Offset <= 15`
接著 P = String[Offset]...String[Offset + 3]
最後再回傳 P 的最後 31 bits

取最後 31 bits 的原因是要避免 signed 與 unsigned 的問題

實作最後至少要回傳 6 位數，依照情況也可以是 7 或 8 位數

```
int offset   =  hmac_result[19] & 0xf ;                // 取出 offset
int bin_code = (hmac_result[offset]  & 0x7f) << 24     // 從 offset 開始取出 4 bytes
   | (hmac_result[offset+1] & 0xff) << 16
   | (hmac_result[offset+2] & 0xff) <<  8
   | (hmac_result[offset+3] & 0xff) ;
```

## RFC4226 演算法

這節直接演練一次如何計算出 RFC4226 期待的結果

1. 準備一個 C (Counter value), 8 Byte 在 Kotlin 就是型別 Long 的變數，假設 Counter 為 `65535`
1. `65535` 就是 `0xFFFF`，轉成長度為 16 的 hex String 就是 `000000000000FFFF`
1. 把這個字串轉成 Byte Array，等下計算 SHA1 會用到。最後轉成 `[00 00 00 00 00 00 FF FF]` 的 8 Bytes array
1. 把 `GoAheadMakeMyDay` 轉成 Ascii 的 byte array，拿到 `[47 6f 41 68 65 61 64 4d 61 6b 65 4d 79 44 61 79]`
1. 拿 Counter 以及 Key 的兩個 Array 計算出 SHA1 值，SHA1 值是 Digest，也就是 RFC 裡面的 `hs`。以我們的例子，最後計算出的 `hs[] = [16 66 4A 59 58 57 E2 55 22 DC A3 1B 97 A9 C4 B5 7E D7 77 25]`

Digest 一定是長度 20 bytes Array。別人弱水三千只取一瓢，這邊 20 bytes 也只需要 4 bytes

1. 取出 array 的最後一個 byte (hs[19])，進行 `AND 0x0F` 運算，得到一個絕對在 0 ~ 15 之間的數字，這個數字是 offset - 現在得到 `offset = 5`
1. 在 hs 裡面以 offset 當位移取出 4 bytes `[57 E2 55 22]` (hs[5] ~ hs[8])，得到一個 4 bytes 的數字
1. 拿掉第一個 bit (`& 0x7FFFFFFF`)，避免正負號的問題 - 最後拿到一個整數 `1474450722`
1. 開始 truncate，如果只需要 6 位數，最後的結果就是 `450722`

<div style="max-width: 100%; margin: auto;">{% asset_img hotp_flow.svg %}</div>

# RFC6238

聰明的你一定馬上就想到可以拿「當下的時間」當做理應要不斷變動的 Counter Value。是的，這就是 RFC 6238 在做的事情，只是它用的單位叫做 Steps

* 以一個 T0 當做起始的秒數
* 每 X 秒當做一個 Step (預設是 30 秒)
* 現在的時間，減掉 T0 之後，共有多少 Steps？就是現在的 Steps

我們要實作給 GA，所以

* T0 就是 January 1, 1970 00:00:00.0 UTC.
* X 是 30
* 所以就是很簡單地，`currentTimeMillis()` 取得 Millis 後除以 1000，再除以 30，丟給 RFC4226 就搞定囉

換句話說，如果 client 的時間不準，或說沒跟 Server 同步，兩邊就會對不起來了，這算是 GA 的限制吧。

<div style="max-width: 100%; margin: auto;">{% asset_img totp_flow.svg %}</div>

# Bas32 (生出 GA 用的 URI)

這個反而比較麻煩一點。不能直接拿 Key `GoAheadMakeMyDay` 餵給 GA，要先轉成 Base32 的字串。因為不是常見的 Base64，所以最後我也是自己實作一份。Base32 的演算法如下

1. 把字串轉成 byte array
1. 每 5 個 bits 分成一組，最後不足 5 bits 的餘位，補上 0 成為 5 bits
1. 2 的 5 次方就是 32， 把每個數字拿去映射長度為 32 的列表 `'A', 'B', 'C', .... 'Y', 'Z', '2', '3', '4', '5', '6', '7'`. (沒有 1，可能是怕跟大寫 I 混淆)
1. 得到字串之後，最後補上等號 `=`，直到字串的長度為 8 的倍數，這就是能餵給 GA 的 Secret
1. `GoAheadMakeMyDay` 會得到 `I5XUC2DFMFSE2YLLMVGXSRDBPE======`

如果不想在 GA 裡面手動輸入這串字，就要生出一個 URI，格式為

```
otpauth://totp/foobar?digits=6&issuer=TEST&secret=I5XUC2DFMFSE2YLLMVGXSRDBPE======
```

拿這個 URI 去轉成 QR code，就能用 GA 掃描的方式輸入

<div style="max-width: 100%; margin: auto;">{% asset_img ga_secret.svg %}</div>

# Source code

如果對實作有興趣，可以看看我這很簡單的 Kotlin 版本: {% asset_link twofactor.zip %}，基本上就三個檔案分別是 HOTP, TOTP 與 Base32 的實作，還有 Unit Test 當做使用範例。
