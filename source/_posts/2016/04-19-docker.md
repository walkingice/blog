title: Docker volume 簡單用法
s: docker
date: 2016-04-19 17:27:09
tags:
    - geek
    - docker
---

最近在設定開發環境的時候有個使用 docker volume 的機會，在這邊筆記一下指令，以便自己下次查詢使用。

比起在系統裡面裝一個 postgresql，我比較喜歡透過 [docker](https://hub.docker.com/_/postgres/) 來使用。藉此降低對 host packages 的相依性，雖然會有一些效能上的 overhead 我覺得還是值得。

既然要用 docker，馬上就要考慮 persistent data 的問題：**如何把資料獨立在 image 之外來保存？**，第一個聽到的解答就是 volume，因為開發需要，我的需求又有點不一樣

* 希望能用官方的 pg image
* db 資料是獨立的，這樣可以獨立升級 pg 版本
* db 的資料希望能有 snapshot，這樣方便寫 unit test 或是開發，不管怎麼惡搞資料都能簡單回復


這邊紀錄下我最近的作法，未來遇到更好的作法再更新於此。

<!-- more -->

# 準備 postgres

首先當然是先抓下 pg 的 image，假設指定安裝 9.5 版 pg，方法如下。如此一來系統只要安裝 pg client 使用 **psql** 就能連進去了。

```bash
$ docker pull postgres:9.5
```

# 什麼是 docker volume

[深入理解 Docker Volume（一）](http://dockone.io/article/128)已經寫得很好，可以先去看一看。這邊條列幾個重點：

* Docker image 由多個 read-only 的 file system 疊加而成一個 stack，這些組合稱為 Union File System。
* 啟動 container 的時候，會在 stack 上方添加一個 read-write layer，更動都在這邊，砍掉 container 這邊也就沒了
* Volume 是為了要解決 container 之間資料共享與資料保存而提出的
* Volume 就是目錄或是檔案，可以繞過 UFS 以正常的檔案或目錄的方式存在 host 本機上
* 啟動 Container 的時候產生 volume，如果 Volume 掛載的目標路徑已經有檔案存在於 Image 上面，則 Image 上面的檔案會 copy 到 volume 上，[但不包括 host directory](https://docs.docker.com/engine/userguide/containers/dockervolumes/#mount-a-host-directory-as-a-data-volume)

## 基本 Volume 使用方法

先來基本的使用方法，就是透過 volume command 以及它的 sub-commands 來操作。前面說到 volume 是一個獨立於 container 之外的檔案，那麼就來練習

* 產生一個空的 volume
* 在 container foo 裡面塞個檔案進去 volume
* 再新開一個 container bar 觀察一下是不是一樣有該檔案

```bash
# 產生一個 volume，假設它的 id 是 AABBCCDDEE
$ docker volume create
AABBCCDDEE

# 用 busybox image 生出一個 container，把 volume 掛到 /foo 底下，並且新增一個空白檔案 Blah
$ docker run -v AABBCCDDEE:/foo --name foo -it busybox /bin/sh
/ # cd /foo
/foo # ls
/foo # touch Blah
/foo # exit

# 把原來的 container 砍掉，重開一個，把 volume 掛進去還是可以看見該檔案 Blah 被放在 /foobar 底下
$ docker run -v AABBCCDDEE:/foobar --name bar -it busybox /bin/sh
```

還有一些基本觀察 volume 的指令

```bash
$ docker volume ls
$ docker volume inspect THE_VOLUME_ID

$ docker inspect -f '{{.Mounts}}' foo  #查看某一個 container 的 volume 狀況
```

### 啟動 container 自動產生 volume

除了前面的作法，要使用 volume 還有兩種方法，一個是在 Dockerfile 裡面使用 <code>VOLUME</code>，另一個是啟動 container 的時候加上選項 <code>-v</code> 並不指定 mountpoint(要拿哪一個 volume 來用)，這樣就會自動產生一個新的 volume

```
# 方法一
FROM debian:wheezy
VOLUME /data

# 方法二
$docker run -v /tmp --name foo busybox ls /tmp
```

### 移除 volume

既然 volume 是獨立於 container 而存在的檔案，移除 container 的時候要記得一併把沒用到的 volume 拿掉。或是一段時間把孤苦無依的 volume 清空一遍

```bash
$ docker rm -v the_container_id

$ docker volume rm `docker volume ls -f 'dangling=true' -q` # clean up
```


# 自製 pg data image

前面簡介了 docker volume 的用法，知道怎麼把玩之後，回到我原來的需求。

因為 pg 會把資料庫的內容放在 <code>/var/lib/postgres</code>，我的想法是把該目錄的資料 commit 到一個很小的 container 裡面，產生一個幾乎只有純 data 的 image 來做到 snapshot 的功能。接著利用這個 image 生出 container，產生的同時也生成一個 volume，最後啟動 pg 的時候把 volume 掛上去就行了


先透過官方的 pg 產生出合用的 **/var/lib/postgresql**

```bash
$ mkdir /tmp/db   # 匯出的檔案將放在 host 的 /tmp/db
$ docker run -d -v /tmp/db/:/tmp/db --name upstream -e POSTGRES_PASSWORD=thepwd postgres

# 這時候已經跑起 pg 了，可以透過 psql 進去做一些操作，把我們的測試資料放進去，密碼就是 thepwd
$ psql -U postgres -h 172.17.0.2
.......(to add fixture data)........

# 用 tar 打包資料，保留 file permission
$ docker exec -it upstream /bin/bash
root@bfb8bc37341e:/# cd /
root@bfb8bc37341e:/# tar cvvf /tmp/db/archive.tar /var/lib/postgresql

$ docker rm -vf upstream
```

接著用已經夠小的 busybox image 來做 snapshot。其實也可以再用一次掛 volume 的方式讓 staging 找到 archive.tar，但是這樣 commit  image 之後會留一點渣渣，我選擇在 host 跑 http server，再從 container 裡面用 wget 抓，效果一樣

```bash
$ docker pull busybox
$ docker run --name staging -it busybox

/ # wget 192.168.42.100:8000/archive.tar
/ # tar xvf archive.tar   # 解開 fixture data
/ # rm /tmp/archive.tar
/ # exit

$ docker commit staging data_img
```

接下來就是真正使用 snapshot 了。跑起一個 data_provider container 只是為了產生 volume，它後來就這樣關掉也沒關係，只要 volume 還在就好。

啟動 pg image 的時候，只要說「我要從 data_provider 而來的 volume」即可

```bash
$ docker run -v /var/lib/postgresql/data --name data_provider data_img
$ docker volume ls

$ docker run -d --name running_pg --volumes-from data_provider postgres
$ docker exec -it running_pg /bin/bash
```
