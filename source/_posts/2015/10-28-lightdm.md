title: 從 GDM 換成 lightDM
s: lightdm
date: 2015-10-28 01:27:51
tags:
    - linux
    - geek
---

最近發生了奇怪的事情，就是我的 root file system (/) 會整個被吃光，同時還注意到 rsyslogd 會跑到 CPU 100%。接著就發現是 <code>/var/log</code> 底下的 syslog 或是 message 幾個檔案變成好幾 Gigabyte 的大小，裡面滿是 gdm3 的錯誤訊息

幾乎都是發生在我跑 Android Studio 之後才會遇到這樣的情況，我還以為是 openjdk 配上我的 awesome window manager 有什麼問題。今天還忍痛換成 orcale java 8，也停掉了 rsyslogd。直到剛剛我才發現一件奇怪的事情

> 竟然有個 gnome-shell 的 process 在跑！
> 竟然有個 gnome-shell 的 process 在跑！
> 竟然有個 gnome-shell 的 process 在跑！

<!-- more -->

莫名其妙，我已經改用 awesome wm 很久了。雖然說有裝著 gnome-shell，只是當著備用，哪天 awesome wm 出問題的時候還有個 window manager 可以開。結果 gnome-shell 這樣糊里糊塗跑起來，難怪一堆 gdm3 的錯誤訊息。

心中一陣不快，就打算把 gnome-shell 給移除，結果 aptitude 連帶要求我把 gdm3 也一起拿掉。好吧，我對這些東西是徹底失望了

搜尋了一下，其實有好幾個替代品

* mdm
* kdm
* gdm
* lightdm

我後來選了 [lightDM](https://zh.wikipedia.org/wiki/LightDM)。要用 lightdm 的人記得它[不會去讀 ~/.profile](https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=636108)，所以要把設定寫到 <code>~/.xsessionrc</code> 底下。
