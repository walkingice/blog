title: 'Kotlin 的 scope function: apply, let, run..等等'
s: kotlin
date: 2018-05-05 10:51:57
tags:
    - geek
    - kotlin
categories: geek
---

先講結論：這些 functions 目的是希望，執行程式碼裡面的 function literal 的時候有更好的可讀性。

`Function`   | `identifier` | `return value`
-------------|:------------:|:-------:
let          | it           | last line of literal
run          | this         | last line of literal
also         | it           | this
apply        | this         | this

**<i><span style="color:#FF0000">Let it</span> <span style="color:#FF8888">Run this</span> literal</i>, <span style="color:#0000FF">it Also</span> <span style="color:#8888FF">Apply this</span>**

這是我背誦記憶的口訣：**Let 用 it，Run 用 this，都是回傳 function literal 最後一行的值。Also 用 it，Apply 用 this，都是回傳 this**

<!-- more -->

在學習 Kotlin 時，一定都會注意到它有許多 `let` `apply` `run` 這些 function，它們看起來大同小異，用起來似乎也經常可以相互替換，稍做修改就讓程式依照正確的邏輯執行。初期我以為有什麼深層的原因，使得 Kotlin 要加入這麼多相似的東西。後來把 Kotlin 的原始碼抓下來翻 Log，從 commit 訊息來看，似乎只是為了要增加 functional literal 的可讀性。

也就是說，這些 function 的使用上，真的互相替換也沒麼大不了，只要你覺得這樣讓你的程式碼更好讀就可以了。

了解這一點之後，我自己也比較放寬心去使用它們。底下就稍微介紹幾個 function，以及我個人偏好的使用場景(這是個人意見，誠如前述，你開心怎麼用就好)。他們都定義在 [Standard.kt](https://github.com/JetBrains/kotlin/blob/v1.2.41/libraries/stdlib/src/kotlin/util/Standard.kt) 裡面，以 function 的形式存在(並非 Kotlin 語言的一部份)，使用它們就像是在呼叫一般的函數一樣。

# 使用 Lambda 的慣例

開始之前先提 lambda 的慣例。Kotlin 在把 lambda 當成函數的參數之時，有個[慣例](https://kotlinlang.org/docs/reference/lambdas.html#passing-a-lambda-to-the-last-parameter)

> 當 lambda literal 是函數調用的最後一個參數時，可以放到括號的外面。如果 lambda 是函數的唯一一個參數，甚至可以拿掉括號

舉例來說，如果我有一個函數叫 **foo**，它接收一個參數，而且該參數是個 lambda

```kotlin
val lambda = { x: Int -> println(x)}
foo(lambda)
// 可以寫成
foo { x: Int -> println(x)}
```

前面提到的那些 function，全部都是用這種方式去運作。所以才會 let, apply 看起來像是關鍵字，用起來像是 kotlin 語言的一部份，其實只是個函數呼叫。

## run

* 產生一個短暫出現的中繼物件，利用它呼叫一些函數，我們在意的是最後執行完的結果，跟 `let` 不同之處為，`run` 不可以自訂變數名稱，並用該物件當成 context
* 感覺類似對一個物件不斷呼叫 map
* 最早的相關 commit: [11ad28812d9fbb2785a8623e0f8baef18aa681cb](https://github.com/JetBrains/kotlin/commit/11ad28812d9fbb2785a8623e0f8baef18aa681cb)
* issue: [KT-5235](https://youtrack.jetbrains.com/issue/KT-5235)

```kotlin
public inline fun <R> run(block: () -> R): R {
    return block()
}
public inline fun <T, R> T.run(block: T.() -> R): R {
    return block()
}

// example
val foo = Foo()
val value = run { foo.bar() }
// or
val another = foo.run { this.bar() }   // this 可以省略

// value 跟 another 都是 bar() 的回傳值

```

第一個定義的 `run` 用了泛型，定義它的回傳值是 **<R>**。`run` 接受了一個叫做 **block** 的 lambda，lambda 的型別是執行之後會回傳 **R**，所以 `run` 的實作就是執行 **block()** 之後拿到 **R**，再把那個 **R** 當成自己的回傳值傳出去。

第二個定義的 `run` 則是多了一個 **T** 綁上某個物件，執行 labmda 的時候就可以用 `this` 來當 context。感覺有點像 js 裡面的 bind 的差異

什麼時候用 run 呢？看了一些說明，主要是可以用比較美的方式執行一段 lambda 並且拿到執行完的結果

```kotlin
val foo = { ... }()   // 括號容易被忽略
val bar = run { ... } // looks better
```

## apply

* 拿一個物件來執行一段程式碼，並且把原物件交給下一棒。跟 `also` 不同之處，`apply` 不可以自訂變數名稱，並用該物件當成 context
* commit: [8a578a46f686b691320da2f7524f752fa573ee67](https://github.com/JetBrains/kotlin/commit/8a578a46f686b691320da2f7524f752fa573ee67)
* issue: [KT-6903](https://youtrack.jetbrains.com/issue/KT-6903)
* issue: [KT-6094](https://youtrack.jetbrains.com/issue/KT-6094)

```kotlin
public inline fun <T> T.apply(block: T.() -> Unit): T {
    block()
    return this
}

// example
val foo = Foo()
val apple = apply {
    foo.bar()
}
// or
val banana = foo.apply {
    this.bar()    // this 可以省略
}

// banana 拿到的會是 foo，而非 this.bar() 的執行結果
// 那麼 apple 呢？端看那一行執行的時候 `this` 是誰囉
```

定義與實作來看會覺得與 `run` 很相似，主要差別是回傳值。apply 回傳的是 literal 執行的 context(也就是 this) 而非 literal 的最後一行。在 issue 6094 的標題來看，就是增加彈性可以用不同方法初始化一個物件，譬如說我們可以這麼做

```kotlin
ArrayList<Rect>().apply {
    add(Rect().apply {left = 20})
}
```

不過寫成這樣，可讀性完全沒有比較好，只是舉例能幹嘛而已。

## also

* 拿一個物件來執行一段程式碼，並且把原物件交給下一棒。跟 `apply` 不同之處，`also` 可以自訂變數名稱(預設是 it)
* [官方 Release note 的介紹](https://kotlinlang.org/docs/reference/whatsnew11.html#also-takeif-and-takeunless)
    also is like apply: it takes the receiver, does some action on it, and returns that receiver. The difference is that in the block inside apply the receiver is available as this, while in the block inside also it's available as it

```kotlin
public inline fun <T> T.also(block: (T) -> Unit): T {
    block(this)
    return this
}

// example
var foo = Foo()
foo.also { it -> it.bar() }
// or
foo.also { myName -> myName.bar() }
```

定義與用法幾乎跟 `apply` 一樣，差別在於 `apply` 裡面用的 context/receiver 是 this，`also` 則是能夠自己指定名稱。

## with

* 產生一個物件之後，對它做一系列的初始化動作
* commit: [666cc6e6884643a404b40e33f283a6216551e6a5](https://github.com/JetBrains/kotlin/commit/666cc6e6884643a404b40e33f283a6216551e6a5)
* issue: [KT-3557](https://youtrack.jetbrains.com/issue/KT-3557)

```kotlin
public inline fun <T, R> with(receiver: T, block: T.() -> R): R {
    return receiver.block()
}

// Example
val w = Window()
with(w) {
    setWidth(100)
    setHeight(200)
    setBackground(RED)
}
```

with 的用法差不多就是這樣。從宣告上來看，`with` 定義了兩個 arguments，第一個 `receiver` 是執行期的 context，第二個是要掛在那個 context 下執行的 function，`with` 也會有回傳值，回傳的型別取決於 function block 裡面最後一行。以上面的例子來說，端看 `setBackground` 會回傳 Int 或是 String 或什麼都沒有。

它目的在於讓我們可以比較方便地呼叫某一個 instance 的 function，把一個 instance 當成 **context**，在 function block 裡面的 `this` 就是該 instance，通常 `this` 都能省略不寫，結果就是上面的範例。

以我的看法，`with` 的用途除了少寫幾個 this 以外，更多是為了可讀性：**把 w 包在 with 的小括號裡面，很明顯地知道作用的對象是 w。在這個 function block 的每一行敘述，都是針對 w 這個物件來執行的**。當然可以故意執行別的 instance 的 function，但這樣就破壞可讀性了。

## let

* 產生一個短暫出現的中繼物件，利用它呼叫一些函數，我們在意的是最後執行完的結果，跟 `run` 不同之處為，`let` 可以自訂變數名稱(預設是 it)
* 感覺類似對一個物件不斷呼叫 map
* commit: [a9638d9fa4fbda85e5f97275bdab5f4c3fcbdda3](https://github.com/JetBrains/kotlin/commit/a9638d9fa4fbda85e5f97275bdab5f4c3fcbdda3)

```kotlin
public inline fun <T, R> T.let(block: (T) -> R): R {
    return block(this)
}
```

commit log 就簡單地寫一行 **"Opposite" for with called let.**，看起來就是在文法/程式閱讀上，剛好是 `with` 的相反概念。從宣告上面來看，`run` 跟 `let` 很像，都把某個物件當成 context 執行，差別在 `let` 可以指定一個 id，而 `run` 省略了 id，只能用 `this`。從 context 命名的角度來看，我覺得 `run` 更像是 `with` 的反面。

簡單來說，`let` 跟 `run` 用起來一樣，回傳值也同樣是 function literal 的最後一行，差別在於 `let` 可以自訂 context 的名稱。

在語感上面我覺得 with 有點像是「既然手上現在拿到了這個 instance，我們來按下 instance 上面的這些按鈕吧」。let 有點像是「如果那邊有了這個 instance，希望那個 instance 照著我這份清單的工作去執行一遍」

## takeIf

* 透過一個函數做條件判斷，決定如何初始化一個值
* [官方 Release note 的介紹](https://kotlinlang.org/docs/reference/whatsnew11.html#also-takeif-and-takeunless)

```kotlin
public inline fun <T> T.takeIf(predicate: (T) -> Boolean): T? {
    return if (predicate(this)) this else null
}

// example
var foo = FallbackFoo()
var tmp = Foo()
if (tmp.value > 10) {
    foo = tmp
}
// 配合 elvis-operator 可以改成
val foo = Foo().takeIf{ it -> it.value > 10} ?: FallbackFoo()
```

透過一個判斷條件，來決定要不要採用那個物件。如果條件不滿足，`takeIf` 會回傳 null，此時配合 elvis-operator 可以簡潔地放進 fallback

# 結論

寫了一大堆，感覺好像有點混亂。總之，記得最開頭的表格就好

* `let`, `run`, `apply`, `also` 比較常用，可以減少使用一些暫時的變數，而每個 literal 裡面的局域變數，也不會跑出去污染其他區塊
* 產生出新的物件，需要連續對它做一堆調整的時候，可以考慮用 `with`
* 透過特殊的條件來決定如何指派變數的時候，可以用 `takeIf`

# 應用

以下是從別的地方看見，或是我想到可以應用的地方

```kotlin
// 可以在 literal 裡面用很一般的 connection 命名，而且不用擔心污染到其他地方
DbConnection.getConnection().let { connection -> ...}
// 變數 connection 這邊就看不到了

// 不為 null 的時候才執行
DbConnection.getConnection()?.let { connection ->
    // 這邊能確保一定有 connection 才執行
}
```

有時候寫測試會這樣

```kotlin
val list = ArrayList<Foo>()
var foo1 = Foo()
foo1.id = "foo1"
foo1.value = 10
list.add(foo1)

var foo2 = Foo()
foo2.id = "foo2"
foo2.value = 20
list.add(foo2)

// use scope function to get rid of foo1, foo2

var list = ArrayList<Foo>()
Foo().apply { id = "foo1" }
    .apply { value = 10 }
    .let { list.add(it) }

Foo().apply { id = "foo2" }
    .apply { value = 20 }
    .let { list.add(it) }

// if we want to keep the reference
val ref = Foo().apply { id = "foo3" }
        .apply { value = 20 }
        .also { list.add(it) }
```

# 延伸閱讀

https://discuss.kotlinlang.org/t/let-vs-with-vs-run/30
http://beust.com/weblog/2015/10/30/exploring-the-kotlin-standard-library/

Kotlin 的人自己這麼說的

> Regarding to the question about is there a good reason to keep all three functions we have the following consideration: when such so-called scope function is used with a functional literal it introduces an identifier into the scope of the literal — either this or it, thus it may hide an identifier with the same name from the containing scope.
> Despite there are ways to disambiguate hidden identifier, we’d like to provide some flexibility, so that one can choose which form is more convenient in particular situation.

是不是三個 functions 都有用，有一些考量。這些用在 functional literal 上面被稱為 scope function 的東西，可能會引入一些 identifier 好比 `it` 或是自訂的名稱。或許外部的 scope 也用了同樣的名稱，但是在 scope 裡面我們能夠明確地知道 `it`(或自訂的變數名稱) 目前指的是誰，而且離開 scope 之後就沒了。多幾個 function 是希望保留彈性讓 programmer 自己選擇使用。

