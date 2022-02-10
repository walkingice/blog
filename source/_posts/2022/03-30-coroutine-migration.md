title: kotlinx-coroutines-test migrate to 1.6 紀錄
date: 2022-03-30 21:07:18
categories: geek
tags:
    - geek
    - android
    - kotlin
---

之前因為工作的需要，把 kotlin 從 `1.5.2` 升級到 `1.6.0`，同時修了許多 unit test 相關的問題，在這邊紀錄一下過程，給需要幫助的朋友。

<!-- more -->

kotlin 升級到 1.6.0 的時候，有把許多元件標為 `Deprecated`。這在一般的小專案裡面不是什麼大問題，但我的公司有很多部門都會把程式送到同一個 repository 裡面，當然許多人都有寫 unit test 的習慣，而且我們也有打開 **-Werror(Warning as Error)** 的選項。於是必須在一個 PR 裡面，升級 coroutine 版本的同時，就把上百個 unit test 類別裡面的過時寫法一口氣更新。

這種改測試的任務，配分到各個跨國團隊底下，很容易大家就因為各自的時程壓力而搞到永遠作不完。跟同事討論之後，決定我跳下去動手改。除了改程式碼的苦工之外，還要跟不同的團隊合作避免把測試改壞，同時要保持 commit 的清晰才容易解決 merge conflict，追上最新的開發版。來來回回搞了一個月，終於送出一個增減超過五千行的巨大 PR，算是最近工作比較難忘的一件事。

這過程中也看到各種不同的使用 coroutine 的實作與單元測試寫法，我盡量把我記得的東西寫下來。

# 關於升級 kotlinx coroutine 1.6

首先要看官方的 [Migration Guide](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/MIGRATION.md)，列了不少步驟以及細節。說實在我也是來來回回看了好多遍，才搞懂大部分的意思。需要作 migration 的人可以認真看一下，一方面也是搞懂如何用比較簡潔的方式寫 coroutine unit test

大致上有幾個重點，在寫 unit test 的時候

1. 不要用 `runBlocking` 或是 `runBlockingTest`，改用 `runTest`
1. 不要用 `TestCoroutineScope`，改用 `TestScope`
1. 不要用 `TestCoroutineDispatcher`，改用 `StandardTestDispatcher`
1. 如果實作同時用到其他非同步的工具，好比 RxJava，可以試 `UnconfinedTestDispatcher` 看看有無奇效
1. 必要的時候用 `runCurrent` 確保 pending 的 coroutine 有跑完
1. 用到 `ViewModel.viewModelScope` 的話，用 `Dispatchers.setMain` 把 dispatcher 塞進去
1. 實作要是有用到 `delay`，可以用 `advanceTimeBy` 來控制時間進度

掌握上面的幾個重點，應該就能處理絕大多數的測試。

# 好處：使用一致的風格撰寫 coroutine 的測試

升級到 `1.6.0` 之後，我覺得寫 unit test 有比較簡潔一點，而且我把絕大多數的測試包進 `runTest` 之後，也不需要依賴 Mockito 提供的一些為了測試 coroutine 而增加的工具。整體來說能夠用比較一致的風格來寫單元測試，對於大團隊算是好事。

寫 coroutine unit test 的主要精神就是，產生一個 `TestDispatcher`，然後透過這個 `TestDispatcher` 生出 `TestScope`，然後注入這些測試專用的 Dispatcher 或是 Scope 到實作的程式裡面。因為這些測試用的物件提供了我們上下其手的空間，所以我們就能預期被測試的實作會如何被執行，以此來進行單元測試。

Coroutine 的測試最後大概都能寫成這樣，以下是我覺得不錯的風格。(當然一個測試檔案只應該測試一個對象，我一次塞進 `foo`, `bar`, `foobar`, `foobarViewModel` 只是為了節省空間)

```kotlin
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class FoobarTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope: TestScope = TestScope(testDispatcher)

    private val foo: Foo = Foo(testScope) // inject Scope to implementation Foo
    private val bar: Bar = Bar(testDispatcher) // inject Dispatcher to implementation Bar
    private val foobarViewModel: FooBarViewModel = FooBarViewModel()
    private val foobar: Foobar = Foobar()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // for testing foobarViewModel
    }

    @Test
    fun testFoo() = runTest {
        val returnedValue = foo.suspendableFunc()
        assertEquals(9527, returnedValue)
    }

    @Test
    fun testBar() = runTest(testDispatcher) {
        val returnedValue = bar.suspendableFunc()
        assertEquals(9527, returnedValue)
    }

    @Test
    fun testFooBar() = runTest {
        foobar.suspendableFunc()

        runCurrent() // ensure foobar completed its job
        assertEquals(5987, foobar.fetchedResult)
    }
}
```

從上方的範例可以看到

1. 我用 `StandardTestDispatcher` 產生一個 `TestDispatcher`
1. 並且用 `TestDispatcher` 產生一個 `TestScope`
1. 這些 Dispatcher 跟 Scope 被注入進 `Foo`, `Bar`...等

接著列出一些常見的例子，解釋該怎麼寫測試比較好。**注意：下方範例裡面的類別，即使同名，介面可能跟上面完全不同，請不要訝異上面下面對不起來。**

# 如何測試 suspend function

測試一個單純的 `suspend` function 最簡單了，因為我們可以直接掛在 testScope 底下執行該 function，過程輕鬆簡單

```kotlin
class Bar {

    var currentValue = 0

    suspend fun suspendableFetch() {
        currentValue = fetchRemoteWithDelay(currentValue)
    }

    private suspend fun fetchRemoteWithDelay(input: Int): Int {
        delay(5000)
        return input + 1
    }
}

private val bar: Bar = Bar()
@Test
fun testBar() = runTest {
    assertEquals(0, bar.currentValue)
    bar.suspendableFetch()
    assertEquals(1, bar.currentValue)
}
```

`runTest` 會產生一個 `TestScope`，所以在它的 block 裡面，我們可以直接執行 `suspend` function 而不用擔心太多。

# 以注入的 Scope 來執行 suspend function 的程式，該如何測試

以下的範例有個看起來很普通的函式 `asyncRead`，其實裡面會透過注入的 coroutine scope 執行 suspend function。`asyncRead` 會直接返回，但我們又想要測試 `currentValue` 是否有按照預期地被更動

```kotlin
class Foo(private val injectedScope: CoroutineScope) {
    var currentValue = 0

    fun asyncRead() {
        injectedScope.launch {
            currentValue = fetchRemote(currentValue)
        }
    }

    private fun fetchRemote(input: Int): Int {
        try {
            Thread.sleep(2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return input + 1
    }
}
```

以上面的例子來說，`asyncRead` 是一個在任何地方都可以呼叫的普通函式，射後不理的特徵讓我們不知道該怎麼測試。但是它是掛在注入的 `injectedScope` 下來執行，其實很好解決。既然已經注入了 scope，那麼只要在 assert 之前確保 scope 把該做的事情都做完就好。要確保這件事情，就是出動 `runCurrent`

```kotlin
private val testDispatcher: TestDispatcher = StandardTestDispatcher()
private val testScope: TestScope = TestScope(testDispatcher)
private val foo: Foo = Foo(testScope)

@Test
fun testFoo() = runTest {
    assertEquals(0, foo.currentValue)
    foo.asyncRead()
    assertEquals(0, foo.currentValue)

    testScope.runCurrent()
    assertEquals(1, foo.currentValue)
}
```

除此之外還有其他寫法，譬如說底下的作法也會通

```kotlin
private val testDispatcher: TestDispatcher = StandardTestDispatcher()
private val testScope: TestScope = TestScope(testDispatcher)
private val foo: Foo = Foo(testScope)

@Test
fun testFoo() = runTest(testDispatcher) {  // 指定了 dispatcher
    assertEquals(0, foo.currentValue)
    foo.asyncRead()
    assertEquals(0, foo.currentValue)

    runCurrent()  // 不需指定 scope
    assertEquals(1, foo.currentValue)
}
```

要看懂上面這個寫法，就要先弄懂 `runTest` 做了什麼事。`runTest` 可以接受一個 CoroutineContext，用它生出一個新的 TestScope。而 `runCurrent` 雖然是 `TestScope` 的 extension，實際上拿 `TestScope` 裡面的 scheduler 來用。因為我們把 `testDispatcher` 塞給了 `runTest`，同時又把跟 `testDispatcher` 綁在一起的 `testScope` 注入進去 `foo`。所以 `runCurrent()` 用的 scheduler 是同一個。

結論就像下方的 pseudo code 演示的概念

```kotlin
/** Just Psuedo Code **/
private val testDispatcher: TestDispatcher = StandardTestDispatcher()
private val testScope: TestScope = TestScope(testDispatcher)
private val foo: Foo = Foo(testScope)

private val theScheduler = testDispatcher.testScheduler

@Test
fun testFoo() = runTest(theScheduler) { theScheduler ->
    foo.asyncRead()
    theScheduler.runCurrent()
}
```

所以什麼時候會用 `runTest(testDispatcher)` 呢？**想要確保 `runTest() {....}` 的 block 裡面是用哪個 scheduler 的時候**。(另外也能寫成 `testScop.runTest {...}`，但我覺得寫 coroutine 的單元測試，控制流程進度應該從 Dispatcher 切入，而非 Scope，所以我都會避免這樣的寫法)

# ViewModel 如何測試

**androidx.lifecycle.lifecycle-viewmodel-ktx** 提供了一個 extension `ViewModel.viewModelScope`，讓你在 ViewModel 裡面可以莫名其妙就拿到一個 Coroutine Scope 來用。其實實作很簡單，就是拿 `Dispatchers.Main` 來生出一個 scope 而已。所以在寫單元測試的時候，只要把 Main Dispatcher 換成我們自己的 TestDispatcher 就搞定了

```kotlin
class FooBarViewModel : ViewModel() {

    var currentValue: Int = -1

    fun testFunc() {
        viewModelScope.launch {
            updateValue()
        }
    }

    private suspend fun updateValue() = withContext(viewModelScope.coroutineContext) {
        currentValue = 9527
    }
}

class FoobarTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope: TestScope = TestScope(testDispatcher)

    private val foobarViewModel: FooBarViewModel = FooBarViewModel()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // for testing foobarViewModel
    }

    @Test
    fun testFoobarViewModel() = runTest {
        assertEquals(-1, foobarViewModel.currentValue)
        foobarViewModel.testFunc()

        assertEquals(-1, foobarViewModel.currentValue)
        runCurrent()
        assertEquals(9527, foobarViewModel.currentValue)
    }
}
```

如果跳進去看實作，就會看到 `StandardTestDispatcher` 跟 `UnconfinedTestDispatcher` 在建立的時候，如果沒有指定 Scheduler，就會拿同一個 Scheduler 來用，而且是同一個。而且執行 `Dispatcher.setMain` 的時候把這個 Dispatchers 指定給 Main，`TestSceop` 也說建購的時期如果沒有指定 Dispatcher，也會拿 Main Dispatcher 來用。所以到最後，這幾個背後都是同一個 Scheduler，也因此 `runCurrent` 幾乎是隨便亂用都會動(嘖嘖)。

# StandardTestDispatcher 跟 UnconfinedTestDispatcher 差別在哪

官方 Guide 會看到這句話

> In these cases, UnconfinedTestDispatcher() should be used. We ensured that, when run with an UnconfinedTestDispatcher, runTest also eagerly enters launch and async blocks.

當一個 coroutine 由 `UnconfinedTestDispatcher` 所啟動 (launch)，它會積極地在 caller 的 thread 執行。也就是說當你用這個 Dispatcher 跑起了一個 coroutine，它裡面又產生了一個 coroutine，那麼 `UnconfinedTestDispatcher` 會盡量去執行這個新的 coroutine。舉例來說

```kotlin
class Foobar(injectedDispatcher: CoroutineDispatcher) {
    private val scope = CoroutineScope(injectedDispatcher)

    fun printMsg() {
        scope.launch { // coroutine A
            funA()
        }
        scope.launch { // coroutine B
            funB()
        }
    }

    private fun funA() {
        println("> before funcA")
        scope.launch { // coroutine C
           funC()
        }
        println("< after funcA")
    }

    private fun funB() {
        println("> before funcB")
        scope.launch { // coroutine B
            funcD()
        }
        println("< after funcB")
    }

    private fun funC() {
        println("running funcC")
    }

    private fun funcD(){
        println("running funcD")
    }
}
```

`printMsg` 會產生兩個 coroutine，一個跑 `funA`，一個跑 `funB`。`funA` 執行的時候會 launch 一個 coroutine 來跑 `funC`，`funB` 也會 launch 一個 coroutine 來跑 `funD`

用 `StandardTestDispatcher` 的測試程式如下

```kotlin
private val theDispatcher: TestDispatcher = StandardTestDispatcher()
private val foobar: Foobar = Foobar(theDispatcher)
@Test
fun testFooBar() = runTest(theDispatcher) {
    foobar.printMsg()
    runCurrent()
    println("Done")
    assert(true)
}

/**
results:

> before funcA
< after funcA
> before funcB
< after funcB
running funcC
running funcD
Done

**/
```
如果把依序把 `funA` 產生的 coroutine 稱為 `coroutine A`，那麼執行的順序就是
1. coroutine A
1. coroutine B
1. coroutine C
1. coroutine D

接著改用 `UnconfinedTestDispatcher`

```kotlin
private val theDispatcher: TestDispatcher = UnconfinedTestDispatcher()
private val foobar: Foobar = Foobar(theDispatcher)
@Test
fun testFooBar() = runTest(theDispatcher) {
    foobar.printMsg()
    runCurrent()
    println("Done")
}

/**
results:

> before funcA
< after funcA
running funcC
> before funcB
< after funcB
running funcD
Done
**/
```

執行順序變成

1. coroutine A
1. coroutine C
1. coroutine B
1. coroutine D

A 排在 B 前面，而 A 裡面產生的 C 被 Dispatcher 積極執行，所以插隊在 B 前面，這就是 Guide 裡面說的 Eagerly

如果你的實作混用了其他 library 的非同步功能(ie: Rx)，有時候用 `UnconfinedTestDispatcher` 可以讓程式的執行順序跑得比較像一般函式呼叫的順序。但是長久來說，還是避免一堆工具混在一起作牛丸，盡量用單一工具來做非同步比較好。

# 何時使用 advanceTimeBy

有時候會用到 `delay` 或是其他函式對一個 coroutine 的執行時間作調整

```kotlin
class Foo(private val injectedScope: CoroutineScope) {
    var currentValue = -1

    fun asyncRead() {
        injectedScope.launch {
            delay(2000) // 白金之星！...好吧，它是 delay 而非暫停時間
            currentValue = 9527
        }
    }
}
```

使用 `advanceTimeBy` 可以把 dispatcher 裡面的虛擬時鐘調快，就像老闆的替身能力克里姆王的那樣刪除一段時間

```kotlin
@Test
fun testFoo() = runTest {
    foo.asyncRead()
    assertEquals(-1, foo.currentValue)

    testScope.advanceTimeBy(3000) // 老闆能力發動！
    assertEquals(9527, foo.currentValue)
}
```

# Troubleshooting: 用到兩個 TestDispatcher 會出錯

有時候會看到這個錯誤訊息

```
Detected use of different schedulers. If you need to use several test coroutine dispatchers, create one `TestCoroutineScheduler` and pass it to each of them.
java.lang.IllegalStateException: Detected use of different schedulers. If you need to use several test coroutine dispatchers, create one `TestCoroutineScheduler` and pass it to each of them.
```

那是因為不小心產生了兩個 TestDispatcher，好比以下這段程式，使用 `withContext` 的時候經常發生這個錯誤。

```kotlin
class Bar(val dispatcher: CoroutineDispatcher) {
    suspend fun suspendableFunc(): Int = withContext(dispatcher){ // 這裡用注入的 Dispatcher
        // ....
    }
}

class BarTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val bar: Bar = Bar(testDispatcher)

    @Test
    fun testBar() = runTest { // runTest 自己產生一個 Dispatcher，接著再以此生出 TestScope
        bar.suspendableFunc()
    }
}
```

`Bar.suspendableFunc()` 會用注入的 Dispathcer，`runTest` 如果不指定一個 `TestDispatcher`，它內部實作也會產生一個新的。

讓兩邊都用同一個 TestDispatcher 就可以解決問題。下面就是讓 runTest 使用同一個 Dispatcher

```kotlin
@Test
fun testBar() = runTest(testDispatcher) {
    bar.suspendableFunc()
}
```

## 如果有個函式用到 withContext(Dispatcher.IO)，該如何測試

有時候我們會看到以下這樣的作法。`fetchRemote` 是個跑在 `Dispatcher.IO` 上面的耗時函數，外部呼叫 `asyncRead` 之後就可以射後不理，等到 `returnValue` 更新了，再去更新 UI

```kotlin
class Foo(private val injectedScope: CoroutineScope) {
    var returnedValue = 0

    fun asyncRead() {
        injectedScope.launch {
            returnedValue = fetchRemote()
        }
    }

    private suspend fun fetchRemote(): Int = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext 9527
    }
}
```

要測試 `asyncRead` 就會變得很麻煩，雖然已經注入了一個 `injectedScope`，但是在 `fetchRemote` 跑在我們測試程式碼裡面完全碰不到的 `Dispatchers.IO`。

有個滿噁心的寫法：利用 `withContext` 來測試。

`withContext` 會拿當下的 coroutine context 跟透過參數指定進去的 context，兩個 merge 起來產生新的 context，接著用新的 context 裡面的 coroutine scope 執行 block 裡面的程式，直到該 scope 完成之後才離開 block。所以單元測試只要拿 `withContext` 產生的 scop 注入進去測試對象就可以了

```kotlin
@Test
fun testFoo() = runTest {
    var localFoo: Foo
    withContext(testDispatcher) {
        localFoo = Foo(this) // `this` is a coroutineScope that created by withContext
        localFoo.asyncRead()
        assertEquals(0, localFoo.returnedValue)
    }
    assertEquals(9527, localFoo.returnedValue)
}
```

但我覺得測試寫成這樣太過取巧，很可能遭天譴或是被人恥笑。有辦法修改的話，還是把類別改寫得比較容易測試才是正途。譬如說"inject Dispatcher" + "default value"

```kotlin
class Foo(
    private val coroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    ....
    private suspend fun fetchRemote(): Int = withContext(ioDispatcher) {
        ....
    }
}
```

scope, dispatcher, 球員，裁判都是我的，測試就會變得很好寫

```kotlin
private val testDispatcher: TestDispatcher = StandardTestDispatcher()
private val testScope: TestScope = TestScope(testDispatcher)
private val foo: Foo = Foo(testScope, testDispatcher)

@Test
fun testFoo() = runTest {
    assertEquals(0, foo.returnedValue)
    foo.asyncRead()

    runCurrent()
    assertEquals(9527, foo.returnedValue)
}
```

# 雜記

寫 coroutine 的單元測試，主要是要先搞懂當下在用的是哪個 Scope 或 Dispatcher，釐清它執行的順序，接著就是確保在 assert 之前把該做的事情做完。既然談到執行順序，又回到大家早就知道的概念，實作的時候要適當地注入 Dispatcher，這樣測試才會好寫。
