title: Mac OS X 環境設定與小技巧
date: 2026-02-03 11:28:33
tags:
    - mac
    - macosx
    - geek
---

現在的開發環境常常是 Mac OS X，鑑於 Mac OS X 沒辦法像 Linux 那樣，把 config 檔備份下來就能重製環境。用這篇文章記錄我經常更改的環境設定(偶有更新)

<!-- more -->

## 設定常用 Gesture

忘記預設行為是哪些，但我習慣用四根手指滑動，打開 Mission Control

* System settings / Trackpad / More Gestures
* Mission Control 設定成 `Swipe Up with Four Fingers`

## 增加虛擬桌面數量

古早以前是在同一個桌面裡面打開 N 個視窗，然後用 `Alt + Tab` 切換視窗。

現在不論是在 Linux, Windows 或 Mac OS X 桌面，我都習慣開 10 個虛擬桌面，接著把特定的 App 擺在固定的虛擬桌面。譬如說

* 第一個桌面永遠都是 Terminal
* 第二個桌面都是 IDE 或是當下正在工作的 App
* 第三個桌面是 Firefox Browser
* 第四個桌面是聊天用的 Apps
* 第五個桌面放音樂 App
* ....等等

接著設定 `Option + 數字` 會直接跳到某個桌面，也就等於直接顯示某個 App，非常方便。設定方法如下

* 先打開 Mission Control，右上角有個 `＋` 符號，點下去就能增加一個虛擬桌面。
* 打開 System Settings / Keyboard / Keyboard Shortcuts
* 左邊的 Tabs 選 `Mission Control`，在右側展開 `Mission Control` 的欄位，依序為 `Switch to Desktop N` 設定你想要的快速鍵

### 其他快速鍵

* 另外在左邊的 `Spotlight` Tab 裡面，我習慣設定 `Option + R` 給 `Show App`，這樣可以快速執行 app
* 設定 Caps Lock 為 Contrl，在左邊的 `Modifier keys` Tab 裡面

## Disable Cmd-H 快速鍵 / 或客製快速鍵

Mac OS X 有個通用的快速鍵 `Cmd + H`，可以隱藏當前的 App 視窗。另外還有個 `Option + Cmd + H` 可以隱藏當前 App 以外的所有視窗。

我個人滿討厭這個 Shortcut。

因為我會開多個虛擬桌面，每個桌面放特定的 App，完全沒有隱藏視窗的需求。而且 `Cmd + H` 是個建議好用，又容易誤觸的快速鍵。常常一不小心就把一堆正在用的視窗藏起來，所以想把它改掉。

關閉 **Hide Others** shortcut

* 打開 System Settings / Keyboard / Keyboard Shortcuts
* 在左邊點選 App Shortcuts / 展開 `All Applications` / 新增一個 custom shortcut
    * Menu title 填入 `Hide Others`
        * 我都是用英文環境。也許中文環境要設定別的 title string
    * Keyboard shortcut 隨便設定一個根本不會按到的快速鍵組合

點開上方的 Menu Bar，能看到各式各樣的 Menu Item 以及對應的快速鍵。這邊修改邏輯就是，找出其中一個 Menu Item，重新綁定對應的快速鍵。所以才會有前述的 `字串 --對應--> 快速鍵` 的設定。

All Applications 則是對所有的 App 都套用這個設定。當然也能只針對特定 App 才套用的設定。

`Hide Others` 是共用的，所以套用到 All Applications 沒問題。無奈的是 `Cmd + H` 對應的 `Hide XXXX`，會因為 App 的名字而更動。

因此要逐一設定 `Hide Finder`, `Hide iTerm2`, `Hide Firefox`....等等之類的快速鍵，略為煩人。不過實務上來說，會用到的 App 就是那幾個，所以也還算可以接受，痛苦個十分鐘就能換好幾年的清淨。

透過類似的設定，也能客製化快速鍵。譬如說，我常在 Finder 的一個視窗內，開 N 個 Tab 顯示不同的目錄。所以我會修改切換左右 Tab 的快速鍵

* `Show Previous Tab` -> `Option + Cmd + Left`
* `Show Next Tab` -> `Option + Cmd + Right`

## 快速開啟特定的 System Settings 頁面

我是從這個影片 [7 Quick Ways To Access System Settings on a Mac](https://www.youtube.com/watch?v=MoHUPsnFvUk) 學到。

值得一看，裡面的小技巧都頗有趣。譬如說，能夠透過打開 Url 的方式，直接打開 System settings 的某個頁面。

不過我比較懶，用的是最後一個方法

打開 Finder，按 `Shift + Cmd + G` 打開 Go to 的輸入框，輸入 `/System/Library/PreferencePanes` 之後直接跳到該目錄底下

把想要的 Pane 拖拉到 Dock 上面，以後點一下就能直接打開了。若有經常需要修改的系統設定，用這招就會方便很多

## 避免 `Option + ]` 輸出特殊字元

Mac OS X 預設的英文輸入法，`按下 Option + ]` 會輸出特殊的字元。實測一下就能發現 `Option` 加上其他鍵，可以[輸出各種不同的特殊字元](https://apple.stackexchange.com/questions/388552)

很不幸地，IDE 經常利用 `Option` 組合的快速鍵，因此對程式宅來說，這個行為非常困擾。畢竟我們幾乎都用不到那些特殊字元，但是我們很需要快速鍵。

關閉的方法就如同前面的連結所述

* System setting / Keyboard / Input Source 點下 "Edit"
* 把 `ABC` 拿掉，新增一個 source，在最底下的 `Others` 裡面有個 `Unicode Hex Input`，用它代替 `ABC`

