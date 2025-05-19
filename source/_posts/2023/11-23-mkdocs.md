title: 使用 mkdocs 做私人筆記
date: 2023-11-23 14:11:26
tags:
    - geek
    - docker
---

最近開始用 mkdocs 來製作私人筆記，本文記錄我的心得。

<!-- more -->

## From Gitit

很久以前開始就有做筆記的習慣，包括在工作上所學的技術文件，以及日常生活中各種可能會忘記的事情。

一開始就選用了 pmwiki，後來在 2011 年 11 月改用 gitit，到現在的 2023 年的 11 月已經滿 12 年了，歲月真是可怕的殺豬刀。

我對 gitit 很滿意，幾次換電腦都能夠無痛轉移筆記。直到最近我用的 gitit docker image 還是[別人八年前所製作](https://hub.docker.com/r/marcelhuberfoo/docker-gitit)。身為業內人員，覺得同一個軟體用八年不用改也算是穩定到沒什麼好挑惕的。

最近會打算換筆記系統的原因，在於我想要用 plantuml 來記錄工作的東西。

plantuml 可以很容易地用文字描述就生出圖檔，相當有助於工作相關的溝通。任何修改也能快速生出圖片，純文字檔又能與 git 相處融洽。

但我之前的用法都是把兩者獨立開來，編寫原始描述之後生出 svg 檔再放進我自己的筆記做紀錄。因為 plantuml 之前會把原始描述當成 svg 的 comment 一併放入，所以備份 svg 也相當於備份原始檔。但是後來 plantuml 有些修改，把原始檔做(應該是base64)編碼，轉成一串長文字再放進 comment。如果從頭到尾都只用 plantuml 這個工具，倒是沒什麼問題。但我因為要整合進自己的筆記系統，不免感到麻煩。

也不是第一次興起了直接在筆記裡面寫 plantuml 檔案的想法，其實也早就有人幫 gitit 寫 plugin，把文件裡面被標示成 plantuml 的程式區塊，送去 plantuml server 繪圖再把結果替換進去原文件。也就是我想要的功能

然而，可是，不過，但是....gitit 是用 haskell 實作，而我根本不懂 haskell。

花了幾個晚上嘗試，雖然已經用 cabal 或是 stack 編譯了 gitit 好幾次，還是仍然搞不懂我為何無法順利啟動官方附上的 plugin，更別說想整合別人寫好的 plantuml plugin。在這過程也深深感受到我跟 haskell 八字不合，pandoc 之類的工具用起來很快樂(而且是同一個作者，神！)，但還是沒什麼幹勁去理解它。

另外也覺得這十年來 web 前端的變化翻天覆地，或許改用新的工具再採用新介面也不錯。

## To mkdocs

使用 gitit 這十年來，我發現自己的使用流程大多是

* 使用 vim 編輯與閱讀
* 直接用 git grep 搜尋 (笑)，因為我自己寫的東西，大概知道東西放在哪
* 需要看比較長的筆記，或是需要圖文並茂的架構，我會用瀏覽器看網頁
* 對於一些指令的用法，我也是用快速在瀏覽器開一個頁面來看該指令怎麼下

回顧過去十年的習慣，其實我並不真正需要一個 wiki 系統 - 可以讓其他人在網頁介面就編輯檔案。換句話說，我更需要的是一個文件生成系統，生出來的成果可以輕鬆用瀏覽器去讀取。(但如果你有多人用網頁介面即時編輯的需求，那 mkdocs 可能就不適合你)

稍微搜尋了一些流行的工具，後來選用 mkdocs，主要是看重它幾個特色

* markdown + git
    * 這是最基本的需求
* 不需要資料庫
    * 當然，因為這是文件生成系統
* 自帶 web server
    * 不過我後來因為效能問題沒用這個
* 可以生出一整包 pure html
    * 當然，因為這是文件生成系統
    * 可以輕鬆 export 出一包檔案，內含一堆易讀 static pages
* 支援網頁搜尋
    * 編譯的過程中會生出一個 json 檔
* 有不少客製化空間
    * 支援 theme 更換
        * 雖然許多人應該是直接用 mkdocs-material
    * 支援 plugin
        * 許多功能可以透過 plugin 補完
    * 也可以增添額外的 css 來修飾頁面的外觀
    * 支援 hook
        * 不想裝 plugin，可以寫 hook 快速改行為
        * hook 就相當於自己寫的 plugin，callback 會接到相同的 event
* 用 python 寫的

最後一點對我來說也是滿大的優點。python 的開發生態系相當完整成熟，配合 virtualenv 之類的東西，要開發 plugin 或是除錯都很容易。安裝 mkdocs 也差不多是幾行指令就能搞定

```bash
# you better use virtualenv
$ pip install mkdocs
$ mkdir /tmp/test && cd /tmp/test
$ mkdocs new .
$ mkdocs serve -f mkdocs.yml # DONE
```

我現在配合 plantuml server，跑起來大概像這樣

<div style="max-width: 100%; margin: auto;">{% asset_img mkdocs.jpg %}</div>

由於 mkdocs 是個 static site generator，讓你專注於寫文件，生出靜態網頁再發佈到公開網站，著重的是在發布後的結果。跟我的需求有點稍稍地不同：我會需要隨時能夠編輯，並且快速看到編輯完的結果。更重要的是，這個程式會常駐在我的系統裡，幾乎可以說是我開著電腦就會開著這支程式。

雖然 mkdocs 預設的功能就能做到這點，但考慮到效能，我不得不另找辦法。

就結論來說

* 我開著一隻 watchdog 程式，發現目錄底下的檔案有異動，就呼叫 mkdocs build
* 另外開一個 http service 專門顯示 build 完的檔案

## 用 mkdocs 內建指令

最常用就這幾個

```bash
# 生出靜態 html 檔案，放到 sites 目錄底下
$ mkdocs build -f mkdocs.yml

# 跑起一個 local server，直接用瀏覽器就能看到最新的文件
$ mkdocs serve -f mkdocs.yml
```

## 使用 mkdocs serve 的不適合之處

mkdocs serve 設計的原意是要讓你快速看到最新的文件結果，所以每一次修改都會重編所有文件。就我個人的使用經驗，寫了十多年也不過四百多個檔案，就現在的電腦效能不是什麼大問題。雖然會慢個幾秒但是能接受

如果想要很快看到結果，還有個 dirty 的選項，可以只編譯被修改的檔案。

```bash
$ mkdocs server -f mkdocs.yml --dirty
```

但是這個指令會讓 Navigation 亂掉。Navigation 就是上方截圖裡面，左側那些 Sqlite, Steam, Tar 之類的列表。讓你可以快速跳到某個頁面。加上 `--dirty` 選項雖然可以非常快速的只編譯單個頁面，但你會發現該頁的 Navigation 幾乎都顯示 `None` 這個字串。

由於 mkdocs 實作的關係(我只有稍微試一下)，這似乎沒辦法靠 Plugin 解決

其次就是 mkdocs 為了要 livereload，發現檔案有更動馬上重編，理所當然地用 watchdog 監控目錄。然而開著它會發現，在我的 M1 Max 上大概隨時都佔用 3% ~ 5% 的 CPU usage。由於我要整天開著，這當然不能接受。

沒有深入去找究竟哪塊在佔用 CPU，但是我用同一個 python watchdog module 所附帶的指令 `watchmedo` 來監控同一個目錄，發現它不會耗費多少 CPU。多半是 mkdocs 實作的其他部分在佔用資源。

為了要讓 mkdocs 能夠隨時都提供給我最新的修改完結果，雖然 mkdocs 內建的 serve 已經功能足夠，但它的設計初衷不同於我的需求，所以我不能接受這個效能損失。

## Search 功能無法在 `file://` 下使用

通常裝 mkdocs 也會一併裝 mkdocs-material。後者是個 theme，但是提供了許多優秀的功能，包括修改 ToC (Table of Content) 的位置、修改 navigation 的位置、更多的顏色調整空間、捲動隱藏 header bar...這些現代網頁講究的功能。

然而它也動到了 Search

當我們編譯出一包 html 檔之後，其實可以直接在瀏覽器裡面用 `file://` 來打開本機的檔案。用這個的好處是完全不用開任何 http server，恰好 mkdocs 就是要編譯出一堆靜態網頁，這樣的組合想起來完美到我都笑到要流口水

然而，主流瀏覽器在開啟 `file://` 的網頁時，會因為安全性原因禁止執行 js，而搜尋功能就是用 js 實作。

未啟動 mkdocs-material 之前，我用瀏覽器其實能夠順利搜尋。而 mkdocs-material 有用到一些現代的 bundle file 的技巧吧，總之會在 `file://` scheme 底下找不到某個 js。我原本以為是 mkdocs-material 的問題，後來才想到用 chrome 去測試，發現在 chrome 上面根本連搜尋框都看不到。

想想也能接受，本來就是希望使用者把生出了的檔案擺到某台主機上面來用，如果在本機端就該關掉 js。

## 我的解法

反正裝 mkdocs 的時候也會一併把 watchdog 裝好，我現在就倒過來用，直接用 watchdoc 去監看目錄，發現有更動就呼叫 mkdocs

2025 更新：最近換到 [Rancher](https://github.com/rancher/rancher/tree/master) 之後發現，在 host 編輯檔案並沒有辦法在 container 裡面觸發 event，進而沒法讓 mkdocs 再次編譯文件。查詢後看到 [mkdocs issue #184](https://github.com/mkdocs/mkdocs/pull/184) 用 polling 解決這個問題，所以稍微更新了底下的指令。

```bash
$ watchmedo shell-command -W --patterns='*' --recursive --command='mkdocs build -f mkdocs.yml' docs/

# 為了在 container 裡面能偵測到 host 端修改了 docs 底下的檔案，這邊使用 polling
# 並且用 --dirty 要求 mkdocs 只為有更動的檔案重新產生文件，以降低 rebuild doc 的時間
# 這樣做有可能會發生 nav link 出問題。真的出問題的話，把產生的文件全部清掉，重新觸發一次 rebuild 即可
# 我覺得這樣算是兼顧效能與可用性的解法
$ watchmedo shell-command -W --patterns='*' --interval 10 --debug-force-polling --recursive --command='mkdocs build --dirty -f mkdocs.yml' docs/
```

對我而言，下一步就是找個盡量輕巧的 http server 就行了。於是找到 docker hub 上面的 [joseluisq/static-web-server](https://hub.docker.com/r/joseluisq/static-web-server/)。rust 寫的，夭壽小又夭壽快，以靜態網頁伺服器這種明確穩定的需求，看到 rust 就莫名覺得安心 XD。

為了管理方便，我把 mkdocs 放進 docker 裡面來跑，現在沒編輯檔案的情況，兩個 container 消耗的資源能夠讓我接受了

```
# mkweb 就是 joseluisq/static-web-serve
CONTAINER ID   NAME      CPU %     MEM USAGE / LIMIT     MEM %     NET I/O           BLOCK I/O     PIDS
d76025485506   mkweb     0.00%     2.84MiB / 7.667GiB    0.04%     16.6kB / 1.37MB   0B / 0B       6
4dda3df0622f   mkwatch   0.02%     38.43MiB / 7.667GiB   0.49%     1.15MB / 118kB    0B / 1.29MB   4
```

## 處理 navigation

預設的 Navigation 有兩個地方我不是很喜歡。首先 navigation 的排序是固定的，但這可以透過 [awesome-pages](https://github.com/lukasgeiter/mkdocs-awesome-pages-plugin) 解決。這個 plugin 讓你在不同的目錄底下可以各自新增一個 metadata 檔案叫做 `.pages`，在裡面定義出優先順序。我需要這個是因為在我的私人筆記裡面，有幾個項目是我經常用到的，我希望排在最前面

其次就是 navigation 的顯示文字，並不是用檔名。這點我有點困擾，但是可以用 [filename-title](https://github.com/mipro98/mkdocs-filename-title-plugin) 來處理。

不過 filename title 好像沒有放上 pip？沒關係，這時候 mkdocs 的 hooks 就派上用場了。直接把該 `filename-title` 實作的主要 function 變成 hooks 讓 mkdocs 執行就搞定了

## 顯示 plantuml

有兩個 plugin 可以用

* [mkdocs_puml](https://github.com/MikhailKravets/mkdocs_puml)
    * 用 inline 的方式放進一個 svg 檔
    * 換言之不能複製圖片網址然後在新頁面開啟 svg 檔
    * 設定比較簡單
    * 需要另外跑個 plantuml server
* [plantuml_markdown](https://github.com/mikitex70/plantuml-markdown)
    * 有生出一個 img src 讓你可以在別的頁面開啟，甚至另存新檔
    * 需要另外跑個 plantuml server

其實兩個都需要 plantuml server，當然也可以很無恥地用官方的 server，只是速度一定很慢。

plantuml 有個 server 功能，提供 GET Api，只要把 plantuml 檔案的內容編碼之後當成參數呼叫 GET，server 就會回傳一個 svg 檔

```bash
$ curl http://www.plantuml.com/plantuml/svg/SoWkIImgAStDuNBAJrBGjLDmpCbCJbMmKiX8pSd9vt98pKi1IW80
```

要自己跑 plantuml server，可以抓別人做好的 docker 檔案。但是因為要有個 java runtime 的關係，通常都是幾百 MB 起跳。因為我的系統都一定會有 jdk 可用，而且 Java 向前相容都做得挺好，只要去 [Plantuml release](https://github.com/plantuml/plantuml/releases/) 抓下一個 jar 檔並且保存好，我想同樣也是用十年不會壞。

```bash
# 通常都開在 8080 port
$ java -jar /path/to/plantuml.jar -picoweb

# 在 Mac 下想要執行，並且不想要出現在 finder 裡面
$ JAVA_TOOL_OPTIONS='-Djava.awt.headless=true' java -jar /path/to/plantuml.jar -picoweb
```

至於設定檔，因為 `mkdocs_puml` 屬於 plugin，所以要放在 plugin 區塊

```yaml
plugins:
  - search
  # pip install mkdocs_puml
  - plantuml:
      puml_url: http://your.plantuml.server:8080
      num_workers: 2
      puml_keyword: plantuml
```

而 `plantuml_markdown` 不是 plugin，要在另外一個區塊

```yaml
# pip install plantuml_markdown
markdown_extensions:
  - plantuml_markdown:
      server: http://your.plantuml.server:8080
      cachedir: /tmp/mkdocs/
      format: svg
```

## 感言

這幾天玩 mkdocs + docker 玩得頗開心。在盡量不更動程式或是不違背 mkdocs 產品方向上，增添一些輕量的修改讓它適合我的需求。摸索的過程比較花時間，但是有了結論之後，以後要重建就很簡單。更何況東西都包成 docker 跟 jar 檔，理論上十年內應該都沒有執行環境的問題了。或許執行效能可能會有問題，但這要跑很久才知道，目前還看不出來。而且我覺得十年內電腦的效能進步應該會遠大於我的筆記尺寸增長。

選擇用 markdown 這種純文字格式做筆記，在這次的轉換也享受到好處。我一個 rename 指令把 gitit 用的 `page` 副檔名換成 `md` 就搞定。以後有什麼更酷更方便的工具，要轉換應該也很簡單。(但我還是喜歡用瀏覽器閱讀，開 app 總是讓我覺得麻煩)

mkdocs 的一個主要服務目標是技術文件的撰寫，譬如我上面所提及 rust 的 [static web server 的文件網站](https://static-web-server.net/)就是用 mkdocs。也因此 mkdocs 在連結不同頁面的功能面應該會做得不錯，不過我自己的筆記量小，自己也知道東西大概放在哪，所以目前也還沒深入理解這部分的功能，未來有空再說吧。
