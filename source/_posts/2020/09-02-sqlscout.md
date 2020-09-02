title: Android Studio 觀察 Sqlite Database
date: 2020-09-02 22:03:07
categories: geek
tags:
    - android
---

最近在改一個 open source 記帳 App，其實也只是隨便亂改一通，讓它的 UI 看得順眼且堪用而已。修改的過程中需要查詢 App 裡面用到的 SQLite db，所以搜尋到一個 SQLScout 的 IDEA IDE Plugin，覺得滿好用，順手紀錄一下用法。

<!-- more -->

首先在 [SQLScout 的網頁](https://www.idescout.com/sqlscout/)就能看見它強調這個 plugin 沒有 open source，可能有些人比較在意，在此先提醒一下。

## 安裝

<div style="max-width: 100%; margin: auto;">{% asset_img sqlscout.jpg %}</div>

安裝很簡單，打開 Android Studio 的設定，搜尋 Plugin 的關鍵字，安裝完之後重開就行了。

## 從手機下載 database 到 host

<div style="max-width: 100%; margin: auto;">{% asset_img download_db.jpg %}</div>

* App 先編譯成 debuggable，這樣才能取出 database 的資料
* Android Studio 右邊會多出一個 SQLite Explorer 的 Tab，點一下就能打開上圖的畫面
* 點「+」好，選「Android (Download Database Locally)」就能 dump 一個資料庫檔案，存到自訂的目錄

## Table Data

<div style="max-width: 100%; margin: auto;">{% asset_img table.jpg %}</div>

資料庫下載完之後，馬上就能看見所有的 Table。還能夠直接修改 Table 裡面的數值，再上傳回手機裡面

## Console

<div style="max-width: 100%; margin: auto;">{% asset_img console.jpg %}</div>

還有 Console 能夠使用，直接輸入 SQL 指令，附帶 auto complete，指令敲完以後點左邊的綠色執行鈕就好，很方便
