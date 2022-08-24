---
title: Android 的 View.canScrollVertically 的誤讀
date: 2022-08-24 21:54:19
categories: geek
tags:
    - android
    - geek
---

在 Android 官方文件裡面有個 `View.canScrollVertically`，起初我認為這個 API 文件寫錯了，後來再看了更多文件，才覺得是我的誤會。

<!-- more -->

官方文件是這麼寫的

> public boolean canScrollVertically (int direction)
>
> Check if this view can be scrolled vertically in a certain direction.
> direction: Negative to check scrolling up, positive to check scrolling down.

如果傳入小於零的值，而 View 可以往上 Scroll 的時候，`View.canScrollVertically(-1)` 會回傳 `true`。

看起來很簡單，但是當我們實際去測試，明明「手指還能往上滑」的時候，這個 API 竟然會回傳 `false`，怎麼跟觀察到的結果不一樣？是不是文件剛好寫反了？

其實這個 scroll 並非「手指往上 scroll」，而是「視窗/Viewport 往上 scroll」，因此我們要區分 Scroll 跟 Drag 的差別。

<div style="max-width: 60%;" class="img-row">{% asset_img gesture.png %}</div>

**Scroll** 的概念是來自於滑鼠的時代，我們把滑鼠滾輪往上滾，就是 **Scroll up**，也就是把 Viewport 往上方移動，但是在 Touch panel 的時代，**Drag down**(往下拖動) 會有同樣的效果。

網頁 JS 裡面的 [window.scrollTo](https://developer.mozilla.org/en-US/docs/Web/API/Window/scrollTo)，也是同樣的效果，傳入座標後，實際上就是把 `Viewport 的 (left, top)` 移動到給定的 `(x, y)`

**Scroll 是在移動 Viewport/Window**

這樣去思考的話，回頭看 Android API 就會得到相同的答案了。

前一陣子在寫內部的文件時，我也一直常被混淆：「大家都在講 scroll，到底是 scroll 什麼」。太常在手機上面開發的人，很容易就把 Scroll-up 想像成「手指按著螢幕，由下往上滑」，如果團隊裡面沒有把這件事情講清楚，Web deverloper 跟 Mobile app developer 很容易就雞同鴨講。

嚴格說起來沒有受詞的 drag 也是曖昧不明，譬如說 Drag-down 究竟是把 View 往下拖動，還是透過 Drag 的動作把畫面移動到底下呢？

至少我自己寫文件的時候會盡量遵循這樣的規則

* scroll 的作用對象都是 Viewport，scroll up 就是把 view port 往上方移動
* drag 是手指在螢幕上的拖動方向，drag down 就是手指按著螢幕，往下滑
