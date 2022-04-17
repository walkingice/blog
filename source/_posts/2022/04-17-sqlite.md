title: 在本機上存取 Android 的 sqlite 檔案
date: 2022-04-17 21:14:04
categories: geek
tags:
    - geek
    - android
---

之前紀錄過一篇怎麼在 Android Studio 裡面用 SQLScout 的 Plugin 存取 sqlite database

其實用指令把 sqlite db 抓到本機端測試也一樣

<!-- more -->

先確保你安裝的是 debuggable app，假設 package 是 `foo.bar`

```bash
$ adb shell
$ run-as foo.bar
$ cd /data/data/foo.bar/database
```
找 `db` 結尾的檔案就是 sqlte 的資料庫檔案了。如果你的資料庫名稱是 `my-db`，多半檔名就是 `MyDb.db`

```bash
cp MyDb.db /sdcard
```

接下來用 adb pull 就能抓下來了。知道路徑的話，可以用一行指令做完

```bash
$ adb exec-out run-as foo.bar cat /data/data/foo.bar/databases/MyDb.db > backup.db
```

以後用 sqlte3 的指令操作這個檔案就可以測試了，習慣指令操作的人，會覺得比起用 plugin 更有彈性

