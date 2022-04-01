title: Android 的 ExternalStorage
s: storage
date: 2017-08-21 00:29:52
tags:
    - android
categories: geek
---

在 Android 上要儲存檔案經常會用到 Internal 或 External [Storage](https://developer.android.com/guide/topics/data/data-storage.html)，對部分的應用程式來說，常見的需求是「把檔案存到 SD card」。

從結論來說，Android 並沒有 SD card 這樣的概念，External Storage 也不一定是外接式儲存裝置。先有這樣的認知，接著就可以問以下幾個問題

* 有哪些 Storage 可以用？
* Context 與 Environment 有哪些 Storage 的 API 可以用？
* 有怎樣的權限問題？

<!-- more -->

## 有哪些 Storage 可以用

打開 API 文件就可以看到幾個名詞

1. Internal Storage
1. Primary External Storage
1. Secondary External Storage

### Internal Storage

User 不能直接存取，只有 app 可以。路徑通常就是 **data/data/<app_packageName>**。就是手機內部的儲存空間(internal flash)，app 的設定檔或是 SQLite 資料庫就是寫在這裡面。屬於各個應用程式的私密空間，寫在這裡面的東西，在系統安全無虞的前提下，只有該 App 可以存取。

雖說不能直接存取，但如果是你自己正在開發的 app，所以會安裝 debuggable 的 app，就能透過 adb 的 run-as 去存取該目錄

```bash
$ adb shell
$ run-as <app_packageName>
```

### Primary External Storage

不管系統有沒有 SD card，你一定有 Primary External Storage 可以用。所以將 Primary external storage 當成 SD card 一定會被搞得很混亂。正如前述，External Storage 不一定是外接式儲存裝置。把 External 換成 Shared 就會變得容易理解：**Primary Shared Storage**

相較於 Internal storage 專門儲存一些 app 私密的資料，有時我們會想存下一些可供分享的檔案，好比程式產出的圖片、照片，或是錄下的聲音，透過網路抓下來的檔案...等等。這些檔案想要被其他程式使用，系統提供了一個非 Internal 的 Stroage，這就是 External Storage。

Primary External Storage 可能是 sd card，也可能是系統自己從 internal flash 分割一塊出來的空間。

### Secondary External Storage

Primary 以外，剩下的就是 Secondary External Storage，SD-card 或是外接式 USB 硬碟，往往都是這一類。早期的 API 並沒有提供存取 Secondary External Storage 的方法，後來漸漸提供了稍微受限的存取方式，後面再講。

### Traditional Storage and Adoptable Storage

API 上面看不見，但可以從[文件](https://source.android.com/devices/storage/adoptable)裡面看到這個名詞。Traditional Storage 就是前述那些可以拔插置換的傳統儲存裝置，相對的名詞則是 Adoptable Storage。

買了手機卻擔心 internal flash 不夠大？在 Android 6.0(M, API 23) 之後提供了這個功能。有些手機的 SD 卡插槽設計得難以拔插，實務上可以視為半永久的儲存裝置。在這種插槽裝上 SD 卡，系統就會詢問是否要將其「收養」，變成 Adoptable Storage

adopt 之後系統便將此 storage 重新格式化、加密，接著把 Primary shared storage 的東西搬移到上面，開始把這顆裝置當成 Priamry shared storage 來使用。也因為會被加密，所以沒辦法拔下來給其他裝置使用。

## API 使用

Context 跟 Environment 都有 storage 相關的 API，乍看會有點混亂，表列之後就清晰許多。Context 是跟 App 相關的東西，Environment 是跟系統相關的東西。在 Android Froyo 之前，app 只能拿到 internal 的目錄來放檔案，想要分享檔案，就要取得系統的 primary external storage 的最上層目錄，自己手動建立目錄把檔案放進去。

後來才透過統一的 API 來給各個 App 放置自己的 cache 或是 files；所以 Context 傳回來的都是「專屬於此 App 的相關目錄」

相較之下，Environment 傳回來的就是 external storage 的最上層目錄，或是共用的 Public directory

* `Context.getCacheDir()` - 取得 **/data/data/<application package>/cache**
* `Context.getFilesDir()` - 取得 **/data/data/<application package>/files**
* `ContextCompat.getExternalFilesDirs(Context context, String type)`
    * 回傳 **application-specific** 的目錄，專屬於這個 app，app 移除的時候會一併砍掉這個目錄
    * 存取不需要 Permission
    * 回傳的 File[]，第一筆是位在 Primary external storage 上面的目錄
* `ContextCompat.getExternalCacheDirs(Context context)`
* `Environment.getExternalStorageDirectory()`
    * 回傳 primary external storage top path
    * 若有複數使用者 (UserManager)，每個 user 看到的 external storage 不一樣
    * 砍 App 的時候不會把這裡面的一起砍掉
* `Environment.getExternalStoragePublicDirectory(String type)`
    * 回傳 primary external storage 相對應 type 的 目錄
    * 目錄可能不存在，要自己呼叫 `File.mkdirs()`

## 歷史共業

SD card 的歷史從蠻荒時代的 1.5 就有了，看完一些文章的整理如下

* 最早開始 external storage 是一個能夠 expose 給 PC 的 Volume
    * 可能是 internal flash 上面的一塊空間，也可能是 sdcard。
    * 唯一的規則是，它若沒有被 PC 拿去用，就要被預設掛載起來
    * 單一的 permission 就可以對整顆 external storage 的任何位置讀寫
* Froyo 2.2 (Api level 8) 之後
    * permission 跟之前一樣
    * 加入了 application specific directory 的概念 - **/Android/data/[Package name]**
    * 但是移除 app 的時候，會順便連這個目錄一起移除
    * 若檔案被拿到這個目錄之外，則不會被移除
* 在早期的時候，系統廠可能自己弄一個 primary external storage 及數個 secondary external storage，但此時的 API 都沒有開放存取 secondary。Samsung galaxy 手機就是這樣，而 sdcard 都是 secondary，所以 API 都無法存取。只有系統的 app 能夠把資料存在上面

* Kitkat 4.0(Api level 19) 之後
    * 開始能夠存取 secondary external storage。此時 primary external storage 的權限跟以前一樣，secondary 的權限規則就不一樣
    * 還是沒有 api 能夠存取到 secondary 上，application-specific 以外的目錄，好比 secondary 的根目錄
    * 所以 secondary 上面的 **/DCIM** **/Picture** 都拿不到囉

* Android N 7.0(Api level 24) 之後
    * Storage 的邏輯上沒什麼大改變。主要是增加 [Scoped directory access(限定範圍目錄存取)](https://developer.android.com/about/versions/nougat/android-7.0.html#scoped_directory_access)

## 存取權限

Android 上面的 storage 讀取權限，主要是靠 Linux 上面常見的檔案系統與 UID/GID 來控管，以 FUSE 提供檔案存取的介面。以前是在 AndroidManifest.xml 裡面宣告了權限，安裝 App 的時候就批准所有權限。後來在 6.0 之後則是執行到需要的時候，才會[向使用者要權限](https://developer.android.com/training/permissions/requesting.html)

取得了 `READ_EXTERNAL_STORAGE` 或 `WRITE_EXTERNAL_STORAGE` 權限之後，便把該 app 加入 **sdcard_r** 或 **sdcard_w** 的群組。所以系統只要設定好各個 storage 上面各個目錄的權限與群組，就可以控管該 app 能不能碰檔案了。


* Froyo 2.2 (Api level 8) 之後
    * **/storage/sdcard** 底下的檔案，所屬的 group 是 **sdcard_rw** owner 是 *root*
    * **Android/data/[Package Name]** 也是一樣的權限
    * `WRITE_EXTERNAL_STORAGE` 會讓 app 加入 **sdcard_rw** 這個 group，所以就拿到完整的讀寫權限
    * `READ_EXTERNAL_STORAGE` 會拿到 **sdcard_r** 的權限，但是在 4.3 之前都沒用

* Kitkat 4.0(Api level 19) 之後
    * **/storage/sdcard** 底下的檔案所屬的 group 是**sdcard_r**
    * 取得 `READ_EXTERNAL_STORAGE` 就能加入 **sdcard_r** group，能讀取 volume 上所有的檔案
    * 取得 `WRITE_EXTERNAL_STORAGE` 則會加入 **sdcard_r** 與 **sdcard_rw**
    * 非 owner 非 group member 者，在這 volume 上的 read 權限被拿掉了
    * app-specific-dir 底下的 group 也屬於 **sdcard_r**，但是檔案的 owner 換成 *app-id*，所以 app 存取自己的目錄時不需要任何權限

Primary 與 Secondary 的目錄權限設定上也略有不同，所以就會有讀寫能力不完全相同的情況，表格整理之後就是這樣

Action                   |   Primary   |   Secondary
------------------------ |  ---------  |  -----------
讀最上層目錄             |     R       |      R
寫最上層目錄             |     W       |      N
讀自己的 Data Directory  |     Y       |      Y
寫自己的 Data Directory  |     Y       |      Y
讀他人的 Data Directory  |     R       |      R
寫他人的 Data Directory  |     W       |      N

R = 需要 Read Permission, W = 需要 Write Permission, Y = Always, N = Never

## Reference

* [On the Edge of the Sandbox: External Storage Permissions](https://possiblemobile.com/2014/03/android-external-storage/)
* [Android Security: An Overview Of Application Sandbox](http://pierrchen.blogspot.tw/2016/09/an-walk-through-of-android-uidgid-based.html)
* [Diving into SDCardFS: How Google’s FUSE Replacement Will Reduce I/O Overhead](https://www.xda-developers.com/diving-into-sdcardfs-how-googles-fuse-replacement-will-reduce-io-overhead/)
