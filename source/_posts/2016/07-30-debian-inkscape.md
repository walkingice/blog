title: Inkscape crashed in Debian testing
s: debian_inkscape
date: 2016-07-30 11:34:22
categories: linux
tags:
    - debian
    - linux
---

# Summary

這個問題持續發生好長一段時間了：只要同時選擇了兩個以上的物件，Inkscape 就會 crash。問題簡單來說就是中文翻譯的問題，所以用其他語言的人不會遇到。

**For debian inkscape 0.91, It only happens in Traditional-Chinese conext**.

You could reproduce this problem by launching inkscape in Traditional-Chinese context. (It won't happen in Simplified-Chinese context. explain later).

```bash
$ LANGUAGE=zh_TW.utf8 /usr/bin/inkscape
```

Then create any two objects, select all of them. boom!

<div style="max-width: 800px; margin: auto;">{% asset_img inkscape_crash_in_zh.png %}</div>

If you only need workaroud for now.

```bash
$ LANGUAGE=en_US /usr/bin/inkscape
```

<div style="max-width: 800px; margin: auto;">{% asset_img inkscape_works_in_eng.png %}</div>

因為困擾我超久的，今天就用 gdb 稍微追一下問題

<!-- more -->

# Environment

* Distribution: Debian testing, upgraded to latest
* Window manager: Awesome window manager
* Language: zh-TW.utf8
* Inkscape: 0.91-9

其實這個問題存在超過半年以上，詳細時間我已經記不清楚了。某一次更新之後發生這個問題，激發我想要修的動力，跑去 [launchpad](https://launchpad.net/inkscape) 上面把 [source code](https://code.launchpad.net/~inkscape.dev/inkscape/trunk) 抓下來，編譯完之後跑起來發現沒問題就一直用著。但是每一次 c library 升級，我就要整個重編一遍。強制把 inkscape 固定在沒問題的 0.48 版本又使得其他 lib 不能升級。想要編個 static linked 的 binary 又失敗(原因不明，懶得找)。甚至還想過要用 docker 把整個 inkscape 包起來。

經過一連串的掙扎，持續在可用，爛掉之間的輪迴好一段時間之後，昨天比較早，八點多離開公司的時候，覺得人生充滿希望，吃完晚餐就決定要來看一下原因到底出在哪。不然我這麼愛用 inkscape，卻又讓它一直爛掉實在不像話。

其實也只是編譯，開 gdb 就好了......

# Compiling inkscape

一開始直接用 gdb 去追 stacktrace 會看到一堆問號，inkscape 也沒有 dev package 可以用，只好自己手動重編一份。感謝 Kanru 以前的教學，要編 debian 的 package 實在很簡單

```bash
$ mkdir foobar
$ cd foobar
$ apt-get source inkscape

$ dpkg-source -x inkscape_0.91-9.dsc    # apply patch
$ ls debian    # 應該會有 debian 這個目錄，檢查一下
$ sudo apt-get build-dep foo-package    # 安裝 dependency libraries，編譯 inkscape 的時候才會順利

$ dpkg-buildpackage    # 編譯！ for normal purpose
# get a cup of coffee
```

上面是一般的 debian package 編譯方法，理論上會生出跟官方一樣的 deb 檔 (當然一些 info 是不一樣的)。但現在要 debug inkscape，所以編譯的時候要塞一些選項

```bash
$ CFLAGS="-g3 -fPIC -O0" CXXFLAGS="$CFLAGS" dpkg-buildpackage
```
用 <code>-g3 -O0</code> 讓 compiler 保留多一點 debug info，因為我的是 x86-64，一開始編譯沒加上 <code>-fPIC</code> 結果 link 的時候失敗，結果整個重編一次才行。夏天天氣熱，電腦又一直發燒，這是最痛苦的地方。總之通過漫長的編譯過程就可以開始真正找問題了。

# Debug

用 gdb 跑起 inkscape 之後，觸發 bug 再看一下 backstrace

```
$ gdb ./inkscape
GNU gdb (Debian 7.11.1-2) 7.11.1
Copyright (C) 2016 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.  Type "show copying"
and "show warranty" for details.
This GDB was configured as "x86_64-linux-gnu".
Type "show configuration" for configuration details.
For bug reporting instructions, please see:
<http://www.gnu.org/software/gdb/bugs/>.
Find the GDB manual and other documentation resources online at:
<http://www.gnu.org/software/gdb/documentation/>.
For help, type "help".
Type "apropos word" to search for commands related to "word"...
Reading symbols from ./inkscape...done.
(gdb)
(gdb)
(gdb) r
Starting program: /home/walkingice/code/inkscape/inkscape-0.91/src/inkscape
[Thread debugging using libthread_db enabled]
Using host libthread_db library "/lib/x86_64-linux-gnu/libthread_db.so.1".
[New Thread 0x7fffe6a94700 (LWP 6493)]
[New Thread 0x7fffe6293700 (LWP 6495)]
[New Thread 0x7fffe5a92700 (LWP 6496)]
[New Thread 0x7fffe2ff2700 (LWP 6497)]
[New Thread 0x7fffe27f1700 (LWP 6498)]

Thread 1 "inkscape" received signal SIGSEGV, Segmentation fault.
0x00007fffeee51410 in vfprintf () from /lib/x86_64-linux-gnu/libc.so.6
(gdb) bt
#0  0x00007fffeee51410 in vfprintf () from /lib/x86_64-linux-gnu/libc.so.6
#1  0x00007fffeef00045 in __vasprintf_chk () from /lib/x86_64-linux-gnu/libc.so.6
#2  0x00007ffff0003ce9 in g_vasprintf () from /lib/x86_64-linux-gnu/libglib-2.0.so.0
#3  0x00007fffeffde67d in g_strdup_vprintf () from /lib/x86_64-linux-gnu/libglib-2.0.so.0
#4  0x00007fffeffde739 in g_strdup_printf () from /lib/x86_64-linux-gnu/libglib-2.0.so.0
#5  0x0000555555a463fd in Inkscape::SelectionDescriber::_updateMessageFromSelection (this=0x55555d63eec0, selection=0x5555577cee00)
    at selection-describer.cpp:217
#6  0x0000555555a46fde in sigc::bound_mem_functor1<void, Inkscape::SelectionDescriber, Inkscape::Selection*>::operator() (this=0x555559712c38,
    _A_a1=@0x7fffffffc548: 0x5555577cee00) at /usr/include/sigc++-2.0/sigc++/functors/mem_fun.h:2064
```
# Problem addressed

stack 很深，理論上 libc 那邊比較不會有問題，第五行可以第一次看到 inkscape 的相關訊息。在 inkscape 的 selection-describer.cpp 第 217 行爛掉，code 如下

```c
gchar *objects_str = g_strdup_printf(ngettext(
    "<b>%i</b> objects selected of type %s",
    "<b>%i</b> objects selected of types %s", n_terms),
     objcount, terms);
```

看起來不是 ngettext 那邊出了問題，就是 g_strdup_printf 有問題。稍微改一下 code 再重新 make 一次，看 ngettext 的回傳值是什麼

```
(gdb) set listsize 15
(gdb) list
210                 int objcount = g_slist_length((GSList *)items);
211                 char *terms = collect_terms ((GSList *)items);
212                 int n_terms = count_terms((GSList *)items);
213
214                 char* foo = ngettext("<b>%i</b> objects selected of type %s",
215                         "<b>%i</b> objects selected of types %s", n_terms);
216                 gchar *objects_str = g_strdup_printf(foo,
217                      objcount, terms);
218
219                 g_free(terms);
220
221                 // indicate all, some, or none filtered
222                 gchar *filt_str = NULL;
223                 int n_filt = count_filtered((GSList *)items);  //all filtered
224                 if (n_filt) {
(gdb) p foo
$1 = 0x7ffff7f60ad1 "已選擇類型 %s 的 <b>%i</b> 個物件"
(gdb) ptype objcount
type = int
(gdb) ptype terms
type = char *
```

原來如此，ngettext 拿到的 string 是 %s 在前 %i 在後，可是 format 的時候是先給 int 再給 char*，當然就爆炸了。那麼其他語言會有這個問題嗎？

# PO file

fr.po
```
msgid "<b>%i</b> objects selected of type %s"
msgid_plural "<b>%i</b> objects selected of types %s"
msgstr[0] "<b>%i</b> objets de type %s sélectionnés"
msgstr[1] "<b>%i</b> objets de types %s sélectionnés"
```

zh_CN.po
```
msgid "<b>%i</b> objects selected of type %s"
msgid_plural "<b>%i</b> objects selected of types %s"
msgstr[0] "<b>%i</b>个对象选择"
msgstr[1] "<b>%i</b>个对象选择"
```

zh_TW.po
```
msgid "<b>%i</b> objects selected of type %s"
msgid_plural "<b>%i</b> objects selected of types %s"
msgstr[0] "已選擇類型 %s 的 <b>%i</b> 個物件"
msgstr[1] "已選擇類型 %s 的 <b>%i</b> 個物件"
```

# Patch

嗯，只有繁體中文是這樣，<del>其實上個簡單的 patch 就行了</del> **更新：這樣的 patch 是不對的**

```diff
diff --git a/src/selection-describer.cpp b/src/selection-describer.cpp
index 1cb96fe..ab719a0 100644
--- a/src/selection-describer.cpp
+++ b/src/selection-describer.cpp
@@ -212,8 +212,8 @@ void SelectionDescriber::_updateMessageFromSelection(Inkscape::Selection *select
             int n_terms = count_terms((GSList *)items);
             
             gchar *objects_str = g_strdup_printf(ngettext(
-                "<b>%i</b> objects selected of type %s",
-                "<b>%i</b> objects selected of types %s", n_terms),
+                "<b>%1$i</b> objects selected of type %2$s",
+                "<b>%1$i</b> objects selected of types %2$s", n_terms),
                  objcount, terms);
 
             g_free(terms);
-- 
2.8.1

```

<del>不過對 ngettext 不熟，不確定這樣是不是正確的解法，也還不是很清楚 debian 的 contribution 方法。所以還是要先看一些文件才能送 patch  orz</del>

後來看了一下 ngettext 的 man page，才比較了解它的用法。

```c
#include <libintl.h>

char * ngettext (const char * msgid, const char * msgid_plural,
                    unsigned long int n);

The  ngettext attempt to translate a text string into the user's native language....
```

簡單來說，這個 function 就是用來取得「翻譯成使用者的語言，拿來做 formatting 的字串」，就是類似「Hi, I am %s -> 嗨, 我是 %s」。但是有三個參數，而非一個 msgid 就可以了，為什麼呢？

因為很多語言有複數型態的問題，根據 man page 的說法，有些語言除了單數、雙數、複數，甚至有四種以上的形式，雖然中文沒有，但是我們熟悉的英文就有複數型。所以最後一個變數 <code>n</code> 就是用來指定要使用第 **n** 種型態，**n=1** 就是單數型，以此類推。

<code>msgid_plural</code> 就是遇到複數型態的時候，要找的對象的 msgid。通常單數型與複數型的翻譯會寫在一起，這樣好像功能重複了？所以 msgid_plural 的用處更像是 fallback。

> In  the  "C"  locale,  or  if  none of the used catalogs contain a translation for msgid, the ngettext functions return msgid if n == 1, or msgid_plural if n != 1.

如果沒有找到合適的翻譯，那麼就拿 <code>msgid</code> 的字串當成單數型來用，<code>msgid_plural</code> 的字串當成複數型來用，要用哪一個由 <code>n</code> 來決定。

看到這邊就知道上面的 patch 錯了，因為它把整個 id(key) 變成了另外一個值，這會使得其他所有的 po 檔都失效，永遠找不到對應的翻譯。由於有 fallback 的關係而不會爆炸，但這樣的行為就不對了。

於是比較合理的作法，就是改 po 檔，從翻譯裡面指定 formatting 的時候參數的位置要擺到哪，這樣就不會出錯了。

```diff
diff --git a/po/zh_TW.po b/po/zh_TW.po
index db57029..fd694bd 100644
--- a/po/zh_TW.po
+++ b/po/zh_TW.po
@@ -12091,8 +12091,8 @@ msgstr "使用 <b>Shift+D</b> 找尋訊框"
 #, c-format
 msgid "<b>%i</b> objects selected of type %s"
 msgid_plural "<b>%i</b> objects selected of types %s"
-msgstr[0] "已選擇類型 %s 的 <b>%i</b> 個物件"
-msgstr[1] "已選擇類型 %s 的 <b>%i</b> 個物件"
+msgstr[0] "已選擇類型 %2$s 的 <b>%1$i</b> 個物件"
+msgstr[1] "已選擇類型 %2$s 的 <b>%1$i</b> 個物件"
 
 #: ../src/selection-describer.cpp:225
 #, c-format
-- 
2.8.1

```

額外一提的是，inkscape 跑起來的時候讀的 po 檔是 */usr/share/locale/zh_TW/LC_MESSAGES/inkscape.mo*，我一開始不知道，想說怎麼改 po 檔的內容，make 完之後都沒變呢？後來才注意到即使是我自己編的 inkscape 還是跑去讀系統的 mo 檔。而且我找不到方法用 make 編出 mo 檔(除了整個 deb 重編以外 =.=)。不過我很偷懶地直接把 zh_TW.gmo 給硬塞進系統的位置，也算是另一個 workaround 吧 :P

啊，還是不知道 debian 送 patch 的流程 orz

