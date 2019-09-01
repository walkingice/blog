title: Kotlin coroutine 基礎筆記
s: coroutine
date: 2020-03-22 21:59:50
categories: geek
tags:
    - android
    - kotlin
---

Coroutine 算是 Kotlin 裡面相當引人注目的功能，簡單來說就是可以幫助處理非同步需求的機制。

舉例來說，我們常常看到這樣的需求：按下一個 Fetch 的按鈕，去抓網路上的某個列表來更新手機內的資料，同時又不希望 UI thread 被卡住。在 Android 上面經常就是祭出 AsyncTask，或是用 Rx 的方式來處理。利用 Coroutine，這些功能都會變得比較簡單實作

<!-- more -->

Coroutine 用起來很簡單，但是鑽進去實作之後發現裡面錯綜複雜，很多東西看到後面我開始懷疑自己的理解，甚至要懷疑自己人生。這邊只會記錄我的粗淺理解，不寫下來就很容易忘記，未來還會繼續更新這裡。

* [Coroutine Context and Scope - Roman Elizarov](https://medium.com/@elizarov/coroutine-context-and-scope-c8b255d59055) - Kotlin libraries Tech Lead 寫的文章，同樣還有好多篇都值得看一下
* [Demystifying CoroutineContext](https://proandroiddev.com/demystifying-coroutinecontext-1ce5b68407ad) - 這篇也有講到一些內部實作的東西，值得看一下
* [协程的历史，现在和未来](https://blog.youxu.info/2014/12/04/coroutine/) - 習慣用 thread 的人，很推薦看一下這篇故事，了解 coroutine 的起源

> 最早提出“协程”概念的 Melvin Conway 的出发点，也是如何写一个只扫描一遍程序 (one-pass) 的 COBOL 编译器....
> 在 Conway 的设计里，词法和语法解析不再是两个独立运行的步骤，而是交织在一起。编译器的控制流在词法和语法解析之间来回切换...
> 简言之，协程的全部精神就在于控制流的主动让出和恢复

我們知道 Process 跟 Thread 的切換由作業系統負責，執行一段時間的程式碼被作業系統中斷，切換到另外一個 Process 或是 Thread。而 Coroutine 的精神則是程式碼執行到一個程度時，向 Scheduler 說：「我 OK，<del>你先領</del>下一位」。這部份的調度發生在 user space 裡面，從作業系統的角度來看，看不見「切換 coroutine」這件事情。

用比較不精確的想像，就是 Kotlin 利用了 syntax sugar 讓你寫出看似循序執行的程式碼，但是實際上程式碼被切碎，放進一堆 coroutines 裡面。這些 coroutines 依照能夠被預測的順序放進 thread 裡面執行。

用一開頭的例子，按下 fetch 鈕之後，透過 `launch` 產生一個 coroutine，繁重的工作放在裡面，所以最後那行 log 能在 coroutine 的工作完成以前就被執行到。
```kotlin
fetchButton.onClickListener { view ->
    currentScope.launch {
        val list = fetchListFromServer()
        updateLocal(list)
    }
    Log.d("button clicked");
}
```

「產生一個 coroutine」這句話困擾了我很久，到底產生了什麼東西？在 `Builders.common.kt` 裡面可以窺見

```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```

`LazyStandaloneCoroutine` `StandaloneCoroutine` 就是生出的 Coroutine，我們包在 launch 裡面的那些程式碼，就是 `block`

### CoroutineScope / CoroutineContext

追程式碼的時候會不斷看到這幾個詞，追程式碼的時候甚至看到 CoroutineScope 就是超簡單的 interface，直接包著 CoroutineContext

```kotlin
public interface CoroutineScope {
    public val coroutineContext: CoroutineContext
}
```

兩者看起來幾乎等價。Roman 的講法是：「The difference between a context and a scope is in their intended purpose.」，我目前的理解為

* CoroutineContext 用來提供每個 Coroutine 執行的資訊 (Job, CoroutineExceptionHandler...這些實作 CoroutineContext.Element 的類別)
* CoroutineScope 定義了 coroutines 之間的階層關係，透過 CoroutineScope 的 functions 建構 coroutine 的同時，也會把 parent-child 關係安排好

<div style="max-width: 500px; margin: auto;">{% asset_img coroutine_scopes_by_elizavor.png source:https://medium.com/@elizarov/coroutine-context-and-scope-c8b255d59055 %}</div>

借用一下 Roman 的圖。Kotlin Coroutine 的實作用了大量的 extension functions，最常用的 `CoroutineScope.launch` 也是其一。上圖可以看到 Parent 包著 Child scope，這也是 scope 的重要特性：當 Parent scope 被取消的時候，child scope 也會一併被取消。

上面還有個重要的訊息，就是從 context `A` launch 一個新的 coroutine `B`，但是 `B` 的 parent context 不一定是 `A`。因為 launch 能指定 additional context (也就是 function signature 裡面的 `context`)，這也可能成為 parent-context。這也是為什麼 launch function 的預設 context 是 `EmptyCoroutineContext`。

### GlobalScope

前面提到了 parent scope 停掉的時候，child scope 也會停掉，所以確認你的 coroutine 掛在正確的 scope 底下很重要。尤其 Android 裡面，經常有 Activity/Fragment 被關掉的機會，如果畫面都被關掉了，有些操作就該被停止。

所以 Android architecture 就提供了幾個 [coroutine scope](https://developer.android.com/topic/libraries/architecture/coroutines) 幫你處理瑣事。只要是從正確的 scope 發起的 coroutine，在 Activity / Fragment 關閉的時候，會記得幫你取消 child scope 裡面的 coroutine

而 `GlobalScope` 則是一個 singleton class，它的生命週期會長於 activity，沒有很確定自己在做什麼，盡量不要透過它生出 coroutine。

### Suspend function

`suspend` 本身只是一個 modifier，實作放在 Suspend.kt 裡面。會由 compiler [做真正的工作](https://stackoverflow.com/questions/47871868/what-does-suspend-function-mean-in-kotlin-coroutine/52925057#52925057)，把標示 suspend 的 function 轉成另外一個樣子，將 `Continuation<T>` 塞進 function signature 裡面。

實作的運作邏輯我還沒摸清楚，只從使用的角度來看，標示了 `suspend` 的 function，意思就是執行到這個 function 的時候，能夠讓出控制流給其他 coroutine。也就是這些 function 都會說：「我 OK，(有需要的話)你先請」，自己走到後面重新排隊

## Examples

來看一點簡單的範例

```kotlin
println("1")
launch {
    println("4")
}
println("2")
launch {
    println("5")
}
println("3")
```
`launch` 會產生一個新的 coroutine，整個執行會依序印出數字 1 ~ 5。


底下是(拔掉 `runBlock`) 簡化之後的範例。因為 foo 是 suspend function，執行到 `delay` 會讓出控制權，所以印出來的數字會是 `1 2 3 4 foo 5 6`

```kotlin
fun suspendTest() {
    println("1")
    launch {
        println("4")
        foo("foo")
        println("6")
    }
    println("2")
    launch {
        println("5")
    }
    println("3")
}

private suspend fun foo(param: String) {
    delay(1)
    println(param)
}
```

如果把情況弄得再複雜一點

```kotlin
fun traceSequence() {
    println("1")
    launch {
        println("5")
        foo("a")
        println("8")
        foo("c")
        println("10")
    }
    println("2")
    launch {
        println("6")
        foo("b")
        println("9")
    }
    println("3")
    launch {
        println("7")
    }
    println("4")
}
```

印出來的結果會是 `1 2 3 4 5 6 7 a 8 b 9 c 10`，印出 "c" 之前那個 coroutine 會讓出控制權，讓 "b" 先被印出

前面的結果可以觀察到，coroutine 雖然讓程式碼的執行變成非同步，但是運作的順序還是可以預測，不像 thread 或 process。coroutine 的概念就像是排隊買口罩，某一家的人可能會跑到隊伍後面重新排起。

這又引入了另外一個問題，如果我產生了兩個 coroutines，它們都很溫馨地跑到隊伍後面重排，但是排在前面的 coroutine 動作很慢的話，會不會卡到後面的？

我在 local 跑起了一個 server，向它發 GET request 會等五秒才回應，接著寫了一個非 suspend function

```kotlin
// 這個 function 會花五秒才 return
private fun getRemoteServer(param: String = "?") {
    val url = URL("http://localhost:8000/")
    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    try {
        BufferedInputStream(urlConnection.inputStream).read()
    } finally {
        urlConnection.disconnect()
    }
    println("done($param)")
}

@Test
fun traceRemoteCall() {
    runBlocking {
        println("1")
        launch {      // coroutine a
            println("4")
            getRemoteServer("a")
            println("5")
        }
        println("2")
        launch {      // coroutine b
            println("6")
            getRemoteServer("b")
            println("7")
        }
        println("3")
    }
}
```

最後印出的結果是 `1 2 3 4 done(a) 5 6 done(b) 7` 耗時 10 秒。雖然 3 很快地被印出來，但是 a 的動作很慢，一定要跑完 coroutine a 才有機會跑到 6

如果把 getRemoteServer 換成 suspend function 並且跑在別的 dispatcher 底下，情況就變得更複雜了。以後有空再寫吧
