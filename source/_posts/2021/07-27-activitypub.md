title: ActivityPub 簡單介紹
date: 2021-07-27 20:43:59
categories: geek
tags:
    - geek
---

[Poga](devpoga.org) 架了 [g0v.social](https://g0v.social/) 之後就有在用。雖然分散式的版本處理系統 git 用了很久，但是一直想不透去中心化的社交網站是怎麼運作。也是大概等到 Trump 的 Twitter 帳號被封，大家開始討論科技巨頭握有太多權力，我才想起來要好好看一下 Mastodon 的 Spec。

不過我看完之後還是矇矇懂懂，所以請不要對這篇有什麼期待，哈。

<!-- more -->

Mastodon 是一個類似 Twitter 的 Microblog service application，比起 Twitter 這樣的集中式服務，Mastodon 讓你對自己帳號的掌控權「更多一點」。

Mastodon 稱自己為去中心化、聯邦式(Federation)的程式。

<div style="max-width: 100%; margin: auto;">{% asset_img ap_federation.svg %}</div>

Twitter 是集中式(Centralized)的服務，我們都很清楚 Centralized 的運作方式：要連上一個別人管理的網站，在上面註冊帳號、與其他帳號互動。

Git 就是我們習慣的分散式(Distributed)工具，在 Git 的使用情境裡，任何人都可以弄個 repository 成為別人的 upstream，彼此之間地位相等，隨時可以增加或減少網絡裡的節點。

Federation 則是介於兩者中間。基本上我們還是要連上某個別人架設好的伺服器；當然你要自己 host 一個也可以。各個服務器之間透過一個公認的規則(ActivityPub)交換訊息，或是透過某種潛規則排擠某一個伺服器。

於是乎，我就有了三個問題

1. 如果我的帳號在 Server A，而我的朋友十分鐘前剛剛自己架了一個 Server B，我有辦法 follow 他在新伺服器的帳號嗎？
    * 簡答：基本上可以，但有可能不行
1. 當我寫了一個新的 Post，我的 100 個 follower(來自 20 個不同 servers) 該怎麼知道我的新貼文？
    * 簡答：由 Server to Server federation protocol 處理
1. 既然沒有一個專屬的機構負責當真理部，該如何避免 spam 或是仇恨言論？
    * 簡答：潛規則的政治手段解決

查詢相關的 Spec，一定會看到三個 W3C spec: Activity Vocabulary, ActivityStream 與 ActivityPub

其實我覺得這三份文件，比起 RFC 都寫得滿籠統，定義會在三者之間相互指涉，看到後面都不懂某個名詞的確切意義是什麼，可能是我看 W3C 規格的功力不夠，這三份又特別有彈性。

我盡可能列出我對這三份文件的理解。以我的淺薄理解，很明顯對於實作沒有太大的幫助，可能要看過 Mastodon 的程式碼的人比較能回答細節的問題。

### Activity Vocabulary

> It is intended to be used in the context of the ActivityStreams 2.0 format and provides a foundational vocabulary for activity structures, and specific activity types.

* [Activity Vocabulary](https://www.w3.org/TR/activitystreams-vocabulary/)

這一份文件為 ActivityStream 會用到的字彙給出更細微的定義，定義每個 Type 或是 property 的意義。這裡面定義了三個 Core types `Object` `Link` and `Activity`。他們之間以及子類別的關係大致如下

<div style="max-width: 100%; margin: auto;">{% asset_img ap_activity_vocabulary.svg %}</div>

雖然 Activity Vocabulary 只定義了一些類別，但是開發者可以添加更多的延伸類別(由 Activity Stream 定義)。

一個基本的 Activity 看起來像這樣

```javascript
{
  "@context": "https://www.w3.org/ns/activitystreams",
  "type": "Activity",
  "summary": "Sally did something to a note",
  "actor": {
    "type": "Person",
    "name": "Sally"
  },
  "object": {
    "type": "Note",
    "name": "A Note"
  }
}
```

Activity Vocabulary 文件試著解釋每個 property 的用處為何。在閱讀 spec 的時候看到 `Domain` 指的是這個 property 可以被用在哪個 type 上面。`Range` 則是說這個 property 可以塞進哪些值

### Activity Stream

> This specification details a model for representing potential and completed activities using the JSON format.

* [ActivityStream](https://www.w3.org/TR/activitystreams-core/)

以 JSON 的格式，拿 Activity Vocabulary 定義好的 properties, type 來組合使用，呈現「活動」(actities)，換句話說，這份文件定義了，Activity Vocabulary 定義好的東西，該怎麼拿來用，才能呈現一個事件(Activity)

這個 Spec 對大多數的 Object 僅僅只有定義不完備的語意，所以可以在 Activity Vocabulary 之外延伸定義更多的細節，也能定義新的 Object type。但是如果仰賴太多延伸定義的類型，那麼 Server 之間會不能溝通

### ActivityPub

> The ActivityPub protocol is a decentralized social networking protocol based upon the ActivityStreams 2.0 data format.

* [ActivityPub](https://www.w3.org/TR/activitypub/)

以 ActivityStream 為基礎，定義出去中心化的社交網路協定分成兩部分

* Server to server federation protocol
* Client to server protocol

這份 Spec 裡面介紹了什麼是 Actor 以及幾個常見的 Activity

我們所創立的帳號就是 Actor (有沒有其他 Actor 我不確定)。一個 Actor 必須要有這些欄位

* inbox (OrderedCollection)
* outbox (OrderedCollection)
* following
* followers
    * 記錄這個 Actor 有多少 follower。以後新增 Activity 的時候會通知這些 follower
    * 實作上可以設定 filter 讓 authenticated user 有更高的優先權
* liked
* streams
* ......

Activity 就是 Actor 在平台上產生的活動，包括但不限於送訊息、追蹤別人、發貼文等等

Spec 裡面舉例了訊息該怎麼傳遞，畫成圖就是下面的樣子。每個帳號(Actor)都會有一個 INBOX 與 OUTBOX，送出訊息的話，先是把訊息透過 Client-to-Server-protocol 寫進自己的 OUTBOX，接著再由 Server-to-Server-federation-protol 把訊息送到對方的 INBOX。對方上線後去看自己的 INBOX 就能讀到訊息

<div style="max-width: 100%; margin: auto;">{% asset_img ap_send_message.svg %}</div>

從上面的訊息傳遞，可以看到一個重點：

**Server 之間的訊息傳遞，是以 POST 為主，而非 GET**

也就是說，當 Actor B 想要知道 Actor A 有沒有送訊息過來，並非發出請求叫 Server B 去 Server A 看看 Actor A 的 OUTBOX，而是 Server A 比需要主動檢查 Actor A 的 OUTBOX，把訊息傳遞給對應的 Server B (也可能是其他 Server)。如果 Server A 沒做好這件事情，則 Actor B 永遠不會知道 Actor A 對 B 說了某些話。這個互動的模型看起來是發生在 ActivityPub 的所有 Server-to-Server-federation-protocol 上面，也影響了後續的很多行為

接著來看 Follow，假設有 Actor A 想要 Follow Actor B

<div style="max-width: 100%; margin: auto;">{% asset_img ap_follow_activity.svg %}</div>

Actor A 發出了一個 Follow Activity 想要訂閱 Actor B，到 Step 3 之前都跟前面一樣。Step 4 就是一個由實作彈性決定的步驟：「要不要接受這個 follow」，也就是說要不要送出一個 Accept Activity

以 Twitter 的行為來舉例的話，就是「預設 Accept 所有的 Follow Request」，但是在 Activity Pub 裡面可以有 Reject 的空間，也可以預設就是 Accept

這邊的重點在於 **發出的 `Follow Activity` 要被接受，才會把 Actor 放進 Follower**。回到上一段講到的重點，Server 之間的互動是以 POST 為主。當 Actor A 的 Follow activity 被 Actor B 接受之後，Actor B 才會把 A 放進 Follower 的清單裡面。當 Actor B 發出新貼文的時候，只會通知 Follower。如果 A 不在清單裡面，那麼它不會知道 B 有新的貼文

畫成圖片就像這樣

<div style="max-width: 100%; margin: auto;">{% asset_img ap_create_activity.svg %}</div>

Actor B 寫了一篇新文，產生了 `Create Activity`，Step 2 找出 Actor B 想要通知 (POST) 的對象，然後把訊息送出去 (Step 3, 4, 5)。送出的接受對象可能是單一的 Actor，也可能是一個共用的 INBOX

ActivityPub 裡面就大概定義了這些看起來滿高階的行為，但是更細節的部分就沒討論到，大概是保留實作的彈性吧

## 結語

回到前面一開始的三個問題。理論上只要 Server 可以把 Follow Activity 送到其他 Server，那麼我就可以追蹤任意 Server 上面的帳號。但是 Server 之間可以相互封鎖對方，因此我認為答案是 Yes and No。問題二上面已經回答了。至於問題三，我認為是透過 Server 之間的封鎖(過濾)行為來達成的。

Server admin 可以決定哪些 Server 的訊息可以進來，就像每個城市都可以決定規則，允許哪些外人進入城內。對於總是產出壞人的城市予以拒絕，對於表現優良的城市給予通行，於是 Server 之間就組成了 Federation。Mastodon 的服務群，還能夠組成 [Fediverse](https://en.wikipedia.org/wiki/Fediverse)。

Serve 之間是依照彼此的價值觀組成一個群體，所以我認為是潛規則驅動，用政治方式解決仇恨言論的問題。有興趣的還可以看這篇 [How the biggest decentralized social network is dealing with its Nazi problem](https://www.theverge.com/2019/7/12/20691957/mastodon-decentralized-social-network-gab-migration-fediverse-app-blocking)。至於 Server 內的仇恨言論？當然就由 Admin 決定要不要一巴掌拍死囉

對於一般來說使用者來說還是受制於 Server admin 的管轄，所以我會說使用者只是對自己的帳號稍微多了一點掌控權(你不一定要從某個你很討厭的 server 加入 federation)

雖然這樣感覺起來似乎有稍微擺脫科技巨頭的箝制。但是回頭想想，Hosting, Domain name 跟我們註冊的 e-mail 帳號還是給大公司牢牢抓著，小個體戶擁有的自由依然比想像中小。
