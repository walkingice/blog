title: 被攻陷的 Android 平板
s: crack
date: 2015-10-21 22:03:51
tags:
    - android
categories: life
---

某天下午，我叔叔拿著他的 Android Nexus 7 平板來找我，說是不能操作了。拿來一看發現螢幕都被綁架得相當徹底。

<!-- more -->

<div class="img-row">{% asset_img launcher.png 被綁架的首頁 %}</div>

看起來只有中間一個 Alert Dialog，實際上整個螢幕除了它之外都不能點，左邊還多了一個設計醜到爆炸的錢袋，一看也知道是同一個流氓軟體塞進去的。

因為叔叔在練二胡，他想上網看看相關影片，這類影片多是中國才會有，自然也到中國的影音網站去找。叔叔說他一開始看到一個視窗想把它關掉，但是因為點不到，愈按愈多最後就變成這樣了。


<div class="img-row">{% asset_img installing.png 安裝中的程式 %}</div>

除了綁架 Launcher 之外，還在努力下載其他的 App，對於中國公司這樣的玩法只能用「野蠻」來形容，相當可怕。

此時平板運作得非常慢，電力消耗得很快，好不容易打開 adb 進去之後一看 process 差點昏倒

<div class="img-row">{% asset_img adb.png 正在運行的 processes %}</div>

粗算了一下從 pid 3026 開始到 pid 3554 有 500 多個 process 努力把自己偽裝成 <code>com.google.patch.launch</code>，可是這麼多同時在跑，應該是有地方寫壞了，才會搞到被綁架的用戶完全不能操作電腦。

跟朋友討論了一下，可能是因為某個 notification 而不小心安裝了 app 才開始這一連串的悲劇，還說這些 app 會偵測系統使用不同的對策，如果是 root 過的裝置，還會 remount 之後裝到 <code>/system</code> 底下，換句話說就是 factory reset 也無效。

看了一下 settings 裡面的已安裝程式，果然如預料中沒有看見這些流氓軟體，後來也沒特別心情去研究技術，就直接把整個裝置回復原廠設定了。
