title: 離線閱讀 Android Doc
date: 2020-12-10 21:53:39
categories: geek
tags:
    - android
---

寫 Android 的程式總是要時時閱讀本草綱目(API documentation)，才會確定應該呼叫哪個 API，就我所知某些人的做法是在 Android Studio 裡面直接看程式碼，不過我還是比較喜歡看精美的 HTML 檔案

雖然很多人是看 online 的版本，不過我從大學時期養成的習慣，還是比較喜歡下載一份 offline 的版本，自己開瀏覽器閱讀。只是這件事情在 Android 上面愈來愈難做到了...

<!-- more -->

JAVA JDK 的文件一直都能[直接下載](https://www.oracle.com/java/technologies/javase-jdk8-doc-downloads.html)，但是 Android 我還沒看到這樣的東西，幾年前我還會從 source code 自己 [make doc](http://0xwalkingice.blogspot.com/2010/03/javadoc-at-android.html)，隨著 AOSP 愈來愈癡肥，我也懶得自己生出 javadoc。

在 Android SDK Manager 裡面，可以直接下載 doc。很遺憾地，最近已經無法下載。在 stackoverflow 上頭有人說 [已經不再提供下載](https://stackoverflow.com/a/49262913)，也只能無奈地接受這個很 Google 的現況。

還好網路上還找得到一些 for Zeal 的 offline doc 可以直接下載，湊合著用尚且過得去，哪天受不了了再自己生出一份 doc 吧。Zeal/Dash 都很好，但我不是很喜歡再額外開個 app 來讀文件，如果能在瀏覽器裡面就搞定，我會盡量待在瀏覽器裡頭。

Android doc 雖然很長一段時間可以直接下載，但是有個搜尋的功能在 Android 7 之後在 offline 版本就壞掉了，使得我其實停留在 Android 6 doc 很久，真的需要查詢新的文件我才會開線上版本。

按下 `Command + /` 之後就能快速搜尋出某一個類別。

<div style="max-width: 100%; margin: auto;">{% asset_img search_official.gif %}</div>


不過線上版的這個搜尋功能做得很爛，總是先顯示一堆 Guide，殊不知真的來看文件的人，其實要看的是類別的說明，而不是 Guide。

前兩天突然想通，其實要做個簡易版本也不是很難。就先準備好每個頁面的連結與關鍵字的資料，頁面讀完的時候塞個 dom，輸入文字的時候去資料裡面查一下就好

就快速地花一個晚上寫了兩個小小的 script

* {% asset_link parser.pl %} - 很簡單的 perl script，搜尋出目錄底下的 html 檔，parse 之後吐到 stdout。所以把輸出的文字 pipe 到一個 js 檔就好
* {% asset_link loader.js_ %} - 這個 js 檔會在頁面塞進一個 input DOM，並且拿前述的資料來用
* 恰好 Android offline doc 的每一頁都會讀進固定的 js 檔，所以把前面兩個 js 檔塞到必讀的 js 檔的最尾端就好了

跑起來像這樣

<div style="max-width: 100%; margin: auto;">{% asset_img search_mine.gif %}</div>

其實還可以寫成 browser extension。不過仔細想想，也不知道多少人會有類似的需求，搞不好只有我會用這麼彆扭的方式開發，所以現在花一個晚上隨便弄弄會動就好，不要花太多力氣在這上面(聳肩)
