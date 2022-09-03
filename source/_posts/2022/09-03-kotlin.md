title: Kotlin 雙冒號 ::someMethod 的觀察
date: 2022-09-03 23:09:44
categories: geek
tags:
    - kotlin
    - java
    - geek
---

寫 kotlin 的時候經常會看到 `::myMethod` 的出現，本文觀察編譯器的結果去猜測可能的過程。並非真正地去閱讀規格或源碼。若有寫不正確的地方，還請鄉親指正

<!-- more -->

# 問題

有時候在實作中，我們會自製一些可被觀察的物件 Observable，以及觀察者 Observer。然後在 Fragment 結束的時候拔掉 Observer。Observer 的實作就用 lambda 解決

```kotlin
fun onStart() {
    myDataOwner.addObserver {
        doSomething()
    }
}

fun onStop() {
    myDataOwner.removeObserver {
        doSomething()
    }
}

private fun doSomething() {...}
```

上面的片段一看就知道很有問題，因為新增跟刪除傳遞進去的東西很明顯不是同一個物件實體。通常會生出一個 private field 來指向同一個物件實體。但如果寫成這樣呢？能夠順利地移除掉 Observer 嗎？

```kotlin
fun onStart() {
    myDataOwner.addObserver(::doSomething)
}

fun onStop() {
    myDataOwner.removeObserver(::doSomething)
}

private fun doSomething() {...}
```

先說結論：**運氣好的話，可以。**

這邊引出幾個問題

* 為什麼可以？在哪些情況下可以？
* 究竟 `::doSomething` 這段做了什麼事？
* 或著問，`::doSomething` 總是回傳同樣的東西嗎？(Singleton?)

在那之前，我先岔題談一下 Lambda

# Lambda 是如何傳遞的

[Lambda](https://kotlinlang.org/docs/lambdas.html) 鄉親都用得很爽，常聽到函式在 Kotlin 裡面是 First-class，就文件跟編譯的結果來看，Kotlin 是透過 `kotlin.jvm.functions.Function0` 或是類似的內部類別來實作，這東西就跟 Java 的 [Method](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html)差不多。

```kotlin
observable.addObserver {
    println("===Inside lambda")
}

val myFunc = object : Function0<Unit> {
    override fun invoke() {
        println("====Inside function")
    }
}
observable.addObserver(myFun)
```

這兩個寫法都可以只是上方的 Lambda 會產生一個匿名類別(anonymous class)，通常用數字取名 `$1`，底下會產生一個 `$myFunc`的類別。放編譯檔案的目錄 `app/build/...` 裡面就能找到

```
./tmp/kotlin-classes/debugUnitTest/foooo/baaar/MyObservableTest$testInstance$myFun$1.class
./tmp/kotlin-classes/debugUnitTest/foooo/baaar/MyObservableTest$testInstance$1.class
```

用 javap 去看，會發現兩個東西很像

```java
// Lambda
$ javap './tmp/kotlin-classes/debugUnitTest/foooo/baaar/MyObservableTest$testInstance$1.class'
Compiled from "MyObservableTest.kt"
final class foooo.baaar.MyObservableTest$testInstance$1 extends kotlin.jvm.internal.Lambda implements kotlin.jvm.functions.Function0<kotlin.Unit> {
  public static final foooo.baaar.MyObservableTest$testInstance$1 INSTANCE;
  foooo.baaar.MyObservableTest$testInstance$1();
  public final void invoke();
  public java.lang.Object invoke();
  static {};
}

// myFun
$ javap './tmp/kotlin-classes/debugUnitTest/foooo/baaar/MyObservableTest$testInstance$myFun$1.class'
Compiled from "MyObservableTest.kt"
public final class foooo.baaar.MyObservableTest$testInstance$myFun$1 implements kotlin.jvm.functions.Function0<kotlin.Unit> {
  foooo.baaar.MyObservableTest$testInstance$myFun$1();
  public void invoke();
  public java.lang.Object invoke();
}
```

兩個都實作了 `Function0`，這就有點像是 Runnable 的介面，[Kotlin Function Type](https://github.com/JetBrains/kotlin/blob/master/spec-docs/function-types.md) 是這麼寫的

* On JVM, introduce Function0..Function22, which are optimized in a certain way, and FunctionN for functions with 23+ parameters. When passing a lambda to Kotlin from Java, one will need to implement one of these interfaces.
* Also on JVM (under the hood) add abstract FunctionImpl which implements all of Function0..Function22 and FunctionN (throwing exceptions), and which knows its arity. Kotlin lambdas are translated to subclasses of this abstract class, passing the correct arity to the super constructor.

也可以看一下 JVM 的實作[FunctionImpl.java](https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/jvm/runtime/kotlin/jvm/internal/FunctionImpl.java)

小結：使用 Lambda 的時候，Kotlin 會自動幫你實作一個匿名類別，你傳遞過去的東西其實是這個匿名類別的實體化物件

# 雙冒號 ::class.java 在幹嘛？

這是 Kotlin 使用 [Reflection API](https://kotlinlang.org/docs/reflection.html) 的方法。Java 也有一套 Reflection API，讓你能做一些見不得光的事情，尤其測試的工具經常大量使用到 Reflection。在 Java 裡面通常會先拿到一個叫做 Class 的類別，然後用那個類別動態挖出一些 Method 來用。在 Kotlin 裡面，除了能用 Java 的 Class，做了很多對應的類別，好比 `KClass`，加個 K 就是 Kotlin 界的東西。

```kotlin
val j: Class<Foobar> = Foobar::class.java
val k: KClass<Foobar> = Foobar::class
```

這個範例加上了型別，所以就很清楚知道 `MyClass::class.java` 是使用 Reflection API 去拿到 Java 界的 `Class` 類別，而 `MyClass::class` 是拿到 Kotlin 界的 `KClass` 類別。

`::class.java` 就相當於以前的 `object.getClass`

```kotlin
val j: Class<Foobar> = Foobar::class.java
val obj = Foobar("")
val oldSchool: Class<Foobar> = obj.javaClass
println("${oldSchool === j}") // true
```

小結：`::class` 是使用 Reflection API 的方法，用來拿到 `KClass` 這個類別


# 雙冒號 ::someFunc 在幹嘛？

前面是用 `::class`，但如果到我們的主角，雙冒號後面接個 method 呢？雖然同樣用 `::` 都是 Reflection，但是此時的行為不一樣。

Kotlin 是個語言，跑在 JVM 上面只是這個語言的其中一種實作。只論 JVM 這部分的話，Kotlin 遇到 `::someFunc` 的處理方式，跟前面的 lambda 很像，都是產生一個匿名類別，然後傳遞出去。

而且 `類別::someFunc` 跟 `物件::someFunc` 雖然都是產生出 `Callable` 的物件，但是兩者有所不同。懂 JS 的人，想像一下 `Function.prototype.apply()`就知道了

```kotlin
val foobar = Foobar("Foobar")
val funSpeakA: KFunction<Unit> = foobar::speak
// 成功
funSpeakA.call()

val funSpeakB: KFunction<Unit> = Foobar::speak
// runTime 會爆出 IllegalArgumentException Callable expects 1 arguments, but 0 were provided.
funSpeakB.call()
// 塞一個 Instance 給它就可以了
funSpeakB.call(foobar)
```

在上面的例子，生出 funSpeakB 的時候根本不知道`this` 是誰，所以會生出一個需要傳入 Foobar 實例的函式物件。

既然知道這麼多「生出匿名類別與 Callable 物件」的技巧，可以看看這段程式碼會生出幾個匿名類別？

```kotlin
val foobar = Foobar("Foobar")
val instanceFromMyObservable = MyObservable()

val funSpeakA: KFunction<Unit> = Foobar::speak // 從 class 產生
val funSpeakB: KFunction<Unit> = Foobar::speak
val funSpeakC: KFunction<Unit> = foobar::speak // 從 instance 產生
val funSpeakD: () -> Unit = foobar::speak
val funSpeakE: () -> Unit = foobar::speak

instanceFromMyObservable.addObserver(funSpeakD)
instanceFromMyObservable.addObserver(foobar::speak)
instanceFromMyObservable.addObserver(foobar::speak)
instanceFromMyObservable.addObserver(foobar::speak)
```

**答案是「8個」**，abcde 各五個，以及 addObserver 那三行

```
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$funSpeakA$1.class
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$funSpeakB$1.class
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$funSpeakC$1.class
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$funSpeakD$1.class
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$funSpeakE$1.class
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$1.class
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$2.class
./tmp/kotlin-classes/debugUnitTest/investigate/reflection/mypkg/Tester$testReflection$3.class
```

而且看 Java 的 byte code，還真的都是拿不同的類別來做事

```java
 18: getstatic     #49  // Field investigate/reflection/mypkg/Tester$testReflection$funSpeakA$1.INSTANCE:Linvestigate/reflection/mypkg/Tester$testReflection$funSpeakA$1;
 21: checkcast     #19  // class kotlin/reflect/KFunction
 24: astore_3
 25: getstatic     #54  // Field investigate/reflection/mypkg/Tester$testReflection$funSpeakB$1.INSTANCE:Linvestigate/reflection/mypkg/Tester$testReflection$funSpeakB$1;
 28: checkcast     #19  // class kotlin/reflect/KFunction
 31: astore        4
 33: new           #56  // class investigate/reflection/mypkg/Tester$testReflection$funSpeakC$1
 36: dup
 37: aload_1
 38: invokespecial #57  // Method investigate/reflection/mypkg/Tester$testReflection$funSpeakC$1."<init>":(Ljava/lang/Object;)V
 41: checkcast     #19  // class kotlin/reflect/KFunction
 44: astore        5
 46: new           #59  // class investigate/reflection/mypkg/Tester$testReflection$funSpeakD$1
 49: dup
 50: aload_1
 51: invokespecial #60  // Method investigate/reflection/mypkg/Tester$testReflection$funSpeakD$1."<init>":(Ljava/lang/Object;)V
 54: checkcast     #62  // class kotlin/jvm/functions/Function0
 57: astore        6
 59: new           #64  // class investigate/reflection/mypkg/Tester$testReflection$funSpeakE$1
 62: dup
 63: aload_1
 64: invokespecial #65  // Method investigate/reflection/mypkg/Tester$testReflection$funSpeakE$1."<init>":(Ljava/lang/Object;)V
 67: checkcast     #62  // class kotlin/jvm/functions/Function0
 70: astore        7
 72: aload_2
 73: aload         6
 75: invokevirtual #69  // Method investigate/reflection/mypkg/MyObservable.addObserver:(Lkotlin/jvm/functions/Function0;)V
 78: aload_2
 79: new           #71  // class investigate/reflection/mypkg/Tester$testReflection$1
 82: dup
 83: aload_1
 84: invokespecial #72  // Method investigate/reflection/mypkg/Tester$testReflection$1."<init>":(Ljava/lang/Object;)V
 87: checkcast     #62  // class kotlin/jvm/functions/Function0
 90: invokevirtual #69  // Method investigate/reflection/mypkg/MyObservable.addObserver:(Lkotlin/jvm/functions/Function0;)V
 93: aload_2
 94: new           #74  // class investigate/reflection/mypkg/Tester$testReflection$2
 97: dup
 98: aload_1
 99: invokespecial #75  // Method investigate/reflection/mypkg/Tester$testReflection$2."<init>":(Ljava/lang/Object;)V
102: checkcast     #62  // class kotlin/jvm/functions/Function0
105: invokevirtual #69  // Method investigate/reflection/mypkg/MyObservable.addObserver:(Lkotlin/jvm/functions/Function0;)V
108: aload_2
109: new           #77  // class investigate/reflection/mypkg/Tester$testReflection$3
112: dup
113: aload_1
114: invokespecial #78  // Method investigate/reflection/mypkg/Tester$testReflection$3."<init>":(Ljava/lang/Object;)V
117: checkcast     #62  // class kotlin/jvm/functions/Function0
120: invokevirtual #69  // Method investigate/reflection/mypkg/MyObservable.addObserver:(Lkotlin/jvm/functions/Function0;)V
```

現在知道了 `::someFunc` 就像 lambda 一樣，會生出匿名類別，並產生 Instance 來用。而且，**如果 ::someFunc 有兩行，就會產生兩個**，知道這個問題的答案之後，也很容易回答另外一個問題。

既然都是用不同的類別來產生實體，當然不是回傳同樣的東西，絕對不是 Singleton

# 什麼時候可以讓 removeObserver 如預期般運作

那麼第一個問題，在什麼情況下可以這麼用呢？這跟 Observer 的實作有關。

```kotlin
class MyObservable {

    var list: MutableList<MyObserver> = mutableListOf()

    fun triggerObserver() {
        list.forEach {
            it.invoke()
        }
    }

    fun addObserver(observer: MyObserver) {
        list.add(observer)
    }

    fun removeObserver(observer: MyObserver) {
        val index = list.indexOfFirst { it == observer }
        if (index >= 0) {
            list.removeAt(index)
        }
    }
}
```

實作的關鍵點就在 `removeObserver` 那邊，如果找到同樣的 observer 就拿掉，所以用 lambda 的時候很自然地就會拿不掉

```kotlin
val instanceFromMyObservable = MyObservable()
println("Size A: ${instanceFromMyObservable.list.size}") // 0

instanceFromMyObservable.addObserver { someFunc() }
println("Size B: ${instanceFromMyObservable.list.size}") // 1

instanceFromMyObservable.removeObserver { someFunc() }
println("Size C: ${instanceFromMyObservable.list.size}") // 1
```

但是如果改用 `::someFunc` 竟然會動！

```kotlin
val instanceFromMyObservable = MyObservable()
println("Size A: ${instanceFromMyObservable.list.size}") // 0

instanceFromMyObservable.addObserver(::someFunc)
println("Size B: ${instanceFromMyObservable.list.size}") // 1

instanceFromMyObservable.removeObserver(::someFunc)
println("Size C: ${instanceFromMyObservable.list.size}") // 0 !!
```

明明是不同類別產生的物件，為什麼可以順利被對應到？鄉民們一定馬上就想到**因為 `==` 被改寫了！**

既然 Kotlin Relection 是產生 `KFunction` 介面的時候，那我們來看一下 JVM 平台的實作 [KFunctionImpl](https://github.com/JetBrains/kotlin/blob/b8b0b279ee2195ccbdce61e2365f123ee928532b/core/reflection.jvm/src/kotlin/reflect/jvm/internal/KFunctionImpl.kt#L176)

```kotlin
override fun equals(other: Any?): Boolean {
    val that = other.asKFunctionImpl() ?: return false
    return container == that.container && name == that.name && signature == that.signature && rawBoundReceiver == that.rawBoundReceiver
}
```

我沒有繼續深追下去確認細節，看起來是用相同 Reflection API 方法產生的類別都會在這邊被視為同一個東西。所以在上方的 Observer 實作就能用 `==` 找到「同一種東西」

我們可以用 `System.identityHashCode()` 找出物件的 hash code 來確認，其實它們真的不是同一個物件。也因此，如果在 `Observerable.removeObserver` 裡面用 `===` 當作比對的方法，就會發現原本可以 remove 的作法行不通了。這也就是我在一開始就說「運氣好的話，遇到用 `==` 來比對的實作，就可以這樣傳 `::someMethod` 進去」


