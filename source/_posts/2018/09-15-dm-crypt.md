title: 在 Debian 上 dm-crypt 的緊急救援
date: 2018-09-15 17:26:15
categories: geek
tags:
    - linux
    - geek
---

這兩年來工作的主力機是 MacBook Pro，原本的 x220 比較少出動，系統的 debian testing 也因此許久沒更新。

前幾天拿出來用 aptitude 升級套件的時候，因為衝突與相依數量太多，一度讓 aptitude 不會動。同時自己又不知道在哪個地方犯了傻，有些套件只更新到一半就重開機，結果掛載家目錄的時候失敗，一直無法開機，連 tty 都沒拿到。

所幸只是小問題，以下紀錄過程，希望能給類似的苦主一點幫助。

<!-- more -->

## 先拿到 shell

我的硬碟 sda 切成三份，都是用 [dm-crypt](https://en.wikipedia.org/wiki/Dm-crypt) 加密

* /dev/sda2 - root file system
* /dev/sda3 - swap
* /dev/sda4 - home

幸好 root 跟 swap 都能成功掛載，所以問題比較單純。開機掛載完 root 跟 swap，繼續跑 init service，原本應該要出現提示號，叫我輸入掛載家目錄的 passphrase，卻遲遲未出現，而是顯示另外一個錯誤訊息

```
A strt job is running for Cryptography Setup for sda4_crypt
A strt job is running for dev-mapper-sda4_crypt.device
```

因為根目錄能夠順利掛載，所以會有 shell 能用，於是指定開機的時候就先執行 shell

* 在 grub 按 e 開始編輯開機選項
* 在 `linux` 那一行的後面加上 `init=/bin/bash`，kernel 開機完之後，就會馬上執行 bash，讓我們拿到 shell

## 嘗試手動掛載 dm-crypt 加密的磁區

dm-crypt 的 dm 是指 device mapper，linux kernel 2.6 開始的功能。把幾個 physical device 映射成一個 logical device。我沒有看細節，猜測應該是利用 device mapper 的功能，把加密的 **/dev/sda4** 解密後映射到 **/dev/mapper/sda4_crypt**，系統只要用後面這個 device node 就能正常使用。所以第一步就是手動慢慢執行每個過程，看看是在哪一步出錯

首先檢查 **/etc/crypttab**，這是 dm-crypt 會用到的表格，用來映射加密前、解密後的 device node。table 看起來很正常，接著就是手動解開 **/dev/sda4**

```
把解密過的 device 放在 /dev/mapper/sda4_crypt 底下，噴出所有 debug log

# cryptsetup --debug --verbose open /dev/sda4 sda4_crypt

接著就一直停在這個錯誤訊息

# Udev cookie 0xd4d1f93 (semid 65536) decremented to 1
# Udev cookie 0xd4d1f93 (semid 65536) waiting for zero
```

udev? 搜尋了一下，看到有人說是因為[少了 /lib/udev/rules.d/${NUMBER}-dm.rules](https://serverfault.com/questions/739531) 相關的檔案。不過這個檔案我有，翻一下它的內容看見跟 **cookie** 的段落如下

```
ENV{DM_COOKIE}=="?*", IMPORT{program}="/sbin/dmsetup udevflags $env{DM_COOKIE}"
```

執行 `dmsetup` 指令馬上就收到錯誤訊息，無法成功執行，當然沒辦法讓 dm-crypt 的 script 跑完！

```
dmsetup: /lib/x86_64-linux-gnu/libdevmapper.so.1.02.1: version `DM_1_02_138' not found (required by dmsetup)
```

`dmsetup` 會用到 `libdevmapper` 函式庫，載入的時候就失敗了，應該是版本的問題。

```
# dpkg-query -p libdevmapper1.02.1
# dpkg-query -p dmsetup

Version: 2:1.02.85-2
```

到 `/var/cache/apt/archives` 比對 dmsetup 與 libdevmapper1.02.1 的 deb 檔，md5 都沒錯，可是比對安裝後的檔案，md5 錯了，可能是安裝過程出了問題，東西只裝了一半。於是回到 `/var/cache/apt/archives` 把 `libdevmapper` 的 deb 檔用 `dpkg` 重新安裝一遍，`dmsetup` 就能順利執行，當然開機也就沒問題了！

