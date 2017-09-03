title: 開發 Android App 階段啟用 StrictMode
s: strictmode
date: 2017-09-03 23:20:17
tags:
    - Android
categories: geek
---

開發 App 的階段，通常是把條件弄得更嚴苛會比較好。除了常見的，把 **Don't keep Activity** 打開以外，還有一個就是啟動 [StrictMode](https://developer.android.com/reference/android/os/StrictMode.html)

<!-- more -->

啟用 StrictMode 的方式，在網路上隨意 Google 一下就能看到很多教學。通常都直接 `detectAll()` 把所有的情況都抓出來，那麼要如何測試會不會動的？最簡單的就是測試 Resource mismatch。

新增一個 strings array 到 xml 檔，array 的內容放數字，然後把該 array 當成 integer-arry 來存取，就能觸發 penalty

```java
// add this to array.xml
//  <string-array name="my_str_array">
//    <item>9257</item>
//  </string-array>
final TypedArray ta = getResources().obtainTypedArray(R.array.my_str_array);
final int foobar = ta.getInt(0, -1);
```

我試著在 `Application.onCreate()` 裡面直接啟用，測試的結果卻發現 StrictMode 不會動，詳細的原因不明，但是透過 `StrictMode.getThreadPolicy()` 取得 instance 再看它的 mask 值，發現我在 Application 裡面順利設定的值，在 Activity 裡面重新檢查的時候已經不見，變回預設值了。

沒有追進 framework 裡面詳細檢查原因(註)，現在先暫時在每個 Activity 啟動的時候再設定一次，就能看到 penalty 了

<div class="img-row">{% asset_img penalty.png Penalty for detectResourceMismatches %}</div>

註：其實這個原因需要釐清，如果真的是被改成預設值而不知道條件為何，很可能開發階段以為 StrictMode 有啟動，實際上並沒有作用
