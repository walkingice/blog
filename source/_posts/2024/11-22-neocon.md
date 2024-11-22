title: 連接 Serial Port 的程式 neocon
date: 2024-11-22 17:44:53
tags:
    - hardware
    - linux
---

連接 `/dev/tty*` console 的軟體，許多人用的是 minicom

而我個人最愛則是輕薄短小的 neocon

<!-- more -->

neocon 是 Kernel Hacker [Werner Almesberger](https://en.wikipedia.org/wiki/Werner_Almesberger) 在 Openmoko 時期寫的小程式，具有連進 tty console 的基本功能，而且全部都在一個檔案裡面，只要自己備份那一個 `.c` 檔案，配合 gcc 隨時都能生出連線的工具，不用再花時間安裝其他東西

下載 [neocon.c](https://github.com/openmoko/neocon/blob/master/neocon.c) (我這邊的備份：{% asset_link neocon.c %}, hash: commit 504449a8c707fe9e93b670e024db3a315ce47a26)

```bash
# compile
$ gcc -o neocon neocon.c

# show usage
$ ./neocon -h

# connect
$ ./neocon /dev/ttyYourPath
```

要離開的時候輸入 `~.` 即可

