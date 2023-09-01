title: Git Subtree 簡單介紹
date: 2023-09-01 22:27:27
categories: geek
tags:
    - git
---

簡單快速地理解 git subtree 是怎樣的東西

<!-- more -->

如果有一個 git repository 同時會被幾個不同的專案用到，通常有兩個選擇：1) 使用 git submodule 2) 使用 git subtree

## git submodule 的概念

<figure class="img" style="max-width: 100%;">{% asset_img git_submodule.svg "git submodule" %}</figure>

* 有兩個 repo: Main Repo 跟 Sub Repo，希望在 MainRepo 底下的 DirSub 放 Sub Repo 的東西
* Main 裡面會有兩個 `.git` 目錄，一個是 Main 本身的，另外一個是 Sub Repo
* Main 跟 Sub 的 commit history 是分開的
    * 在 Main 裡面看不到 Sub 的 commit history
* 對 Main 而言，它看不見 Sub 底下的 `Foo` 或是 `Bar` 目錄
* 對 Main 而言，`DirSub` 是個特殊的檔案，Main 只在乎 `DirSub` 會用到 sub repo 的哪個 commit

## git subtree 的概念

<figure class="img" style="max-width: 100%;">{% asset_img git_subtree.svg "git subtree" %}</figure>

* 同上，有兩個 repo: Main Repo 跟 Sub Repo，希望在 MainRepo 底下的 DirSub 放 Sub Repo 的東西
* Main 裡面只會有一個 `.git` 目錄
    * Main 可以說是根本不知道 Sub Repo 的存在
    * Main 也看得到 `DirSub` 的 commit history
* `DirSub` 對 Main 而言只是個普通的目錄
    * 只是這裡面的東西會用某種方式，從其他 repo 拿過來

## 簡單比較 git submodule 跟 git subtree

我是這麼解釋這兩者的差別

* git submodule 是把兩個 git repo 擺進同一個 work directory 裡面，用這個 work directory 來編譯專案
* git subtree 是用一個 git repo 產生一個 work direcotry，用起來就像平常的專案
    * 只是其中一個目錄裡面的東西，是從別的 repo 拿過來

拿 cherry-pick 來比喻，cherry-pick 就是從別的 branch 拿幾個 commit 進到正在工作的 branch

而 subtree，相當於是從別的 repository，拿幾個 commit 進到正在工作的 repo/branch。

## 用 git format-patch / am 理解

如果你常用 `format-patch` 與 `am` 就能很快理解 `subtree`。

<figure class="img" style="max-width: 100%;">{% asset_img git_format.svg "git format-patch and am" %}</figure>

對於兩個完全沒關聯的 repository，我們可以

1. 從 sub repo 利用 `git format-patch` 對幾個 commit 生出 patch 檔案
1. 把這幾個 patch 檔案拿到 main repo 用 `git am` 把修改放進去

從上面的例子可以看到，最後 Main Repository 會多出兩個目錄 `Foo` 與 `Bar`，內容跟 Sub Repository 一樣。

不過常用這招的人，就會知道 Main 跟 Sub 的路徑要吻合，否則 patch 無法順利打上。

雖然這樣不是很精確，但是可以快速地理解 subtree 是什麼樣的東西

**subtree 就是針對某個子目錄，做類似 format-patch/am，同時也處理好路徑問題的工具**

「我在 Main Repo 開了一個 SubDir，請把 SubRepo 的 commits 都幫我用 `git am` 的方式放進 SubDir 裡面」，大概是這種感覺吧

## 差異

雖然用 format-patch / am 可以很容易理解，但是要注意，實際上還是有所差異。最明顯的就是 commit history，用 `git log --graph` 就能看到 commit history 與 am 截然不同。甚至用 `git whatchanged` 也能看出檔案路徑的差異
