title: Java 的 Nested Class
s: nested-class
date: 2018-02-08 22:23:34
tags:
    - java
    - geek
categories: geek
---

最近公司做的產品，收到來自外面鄉親貢獻的 [PR](https://github.com/mozilla-tw/Rocket/pull/1342)，想起了這個被自己遺忘很久的東西，趁著再次補起記憶的時候，順便做一點紀錄。

簡單來說就是 Java 的 Nested class 盡量寫成 static 比較好。至於為什麼比較好，也許就要多花點功夫來了解。

<!-- more -->

# 優先考慮靜態類別

在 [Effective Java](https://en.wikipedia.org/wiki/Joshua_Bloch) 裡面有提到一個原則：「優先考慮靜態類別」(Favor static classes over nonstatic)。意思是說，在底下的範例裡面，若情況允許(Nested 類別不需要存取外層的 instance)，盡量採取 `StaticNested` 的寫法，而非 `NonStaticNested`，兩者的直觀差別是 `static` 關鍵字。

```java
class OuterClass {
    class NonStaticNested {
    }

    static class StaticNested {
    }
}
```

書中提到的原因是，非 static 的 nested class，會有一個 Reference 指向外層的物件。於是來觀察一下 bytecode，利用 `javap -c Foo.class` 把 bytecode 匯出

```java
Compiled from "OuterClass.java"
class OuterClass {
  OuterClass();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return
}
```

```java
Compiled from "OuterClass.java"
class OuterClass$StaticNested {
  OuterClass$StaticNested();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return
}
```

```java
Compiled from "OuterClass.java"
class OuterClass$NonStaticNested {
  final OuterClass this$0;                  // 這裡！

  OuterClass$NonStaticNested(OuterClass);
    Code:
       0: aload_0
       1: aload_1
       2: putfield      #1                  // Field this$0:LOuterClass;
       5: aload_0
       6: invokespecial #2                  // Method java/lang/Object."<init>":()V
       9: return
}
```

從 bytecode 可以看見，非 static nested class 的確多了指向外層實體的 reference。看到這裡大概懂了書上講的東西，理解 **「Inner class 的 instance，總是連著一個 Enclosing instance」** 是什麼意思了。

附帶一提，要從其他類別中產生一個 Non-static nested class 實體的方法就是

```java
OuterClass outer = new OuterClass();
OuterClass.InnerClass inner = outer.new InnerClass(); // inner 才知道它的 enclosing instance 是誰
```

# 更進一步了解 Nested Class

知道 non-static nested class 多一個 reference，於是有另一個問題發生了：為什麼會有這個 reference？此時也意識到自己對 nested class 的認識不夠，所以再多看一點文件。從 [Java Tutorial](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html)來看，先上一點基本的名詞解釋

* enclosing class: 外層的 class，以上面的範例來說就是 `OuterClass`
* nested class: 包在裡面的 class，就是 `StaticNested` 與 `NonStaticNested` 兩個，nested class 又能分為**static nested class** 與 **inner class** 兩種
* static nested class: 有加上 static 修識字
* inner class: 非 static nested class 的其他種類

## Static Nested Class

先講 **static nested class**，它其實跟 enclosing class，或說跟一般的 class 沒什麼不同。只是恰好寫在另外一個 class 裡面而已。

就像 Java 的 package 的用途，只是為了給 class 有個方便的分類而已。

## Inner Class

inner class，則還能細分出 local class 與 anonymous class。前者是上面例子中的 `NonStaticNested`，後者就是實作時經常放在 callback 裡面或當成參數的 class

Inner class 有底下幾樣特性

* Inner class 可以在任何一個 [block](https://docs.oracle.com/javase/tutorial/java/javaOO/localclasses.html) 裡面宣告。如果這個 block 恰好是在 method 呼叫的參數裡面，那它就是 anonymous class，如同上面的範例
* 根據倒出來的 bytecode，因為 inner class 有個 reference 指向 enclosing 的 instance，所以會有人說 **「Inner class 的 instance，總是連著一個 Enclosing instance」**
* 也因此，Inner class 可以使用 Enclosing instance 的 member，也就是我們常用的 `OuterClass.this.fooBarMemeber`
* 如果 Inner class 宣告在 method 裡面(往往是 anonymous class)，嘗試存取該 method 裡面的 local variable 必須宣告為 `final` (或是等價於 final 的變數，也就是賦值之後不曾變動過)

最後一點比較難解釋，老闆來個飯粒

```java
import java.awt.Rectangle;

class Enclosing {

    int member = 3;

    Enclosing() {
    }

    public void say() {
        System.out.println("Enclosing");

        final Rectangle r = new Rectangle();

        class Inner {
            public void say() {
                String o = r.toString();
                System.out.println(o);
                System.out.println(member); // access member
            }
        }

        Inner i = new Inner();
        i.say();
        r.translate(3, 3); // modify rectangle
        i.say();
    }

    public static void main(String[] args) {
        Enclosing e = new Enclosing();
        e.say();
    }

}
```

上述的範例中，宣告了一個外層的 class **Enclosing**，在它的 `say()` method 裡面，宣告了一個 Inner class **Inner**，並且產生一個 local variable `Rectangle r`，`Inner.say()` 不但用了 Enclosing 的 member，也用了 local variable。雖然 r 有加上 final 宣告字，我還是很故意地讓它 translate。

把這個 **Inner** 的 bytecode 倒出來看

```java

Compiled from "Enclosing.java"
class Enclosing$1Inner {
  final java.awt.Rectangle val$r;

  final Enclosing this$0;

  // ..... 略
}
```

這個 Inner class 不但有個 `this$0` 指向上層的 instance，還有個 `final java.awt.Rectangle val$r` 的 reference 指向 method 裡面宣告的 local variable。換句話說，在這個 class 裡面已經產生了一個 **Closure 閉包**，`r` 這個變數被 Inner 給 **capture** 了。在 Inner 裡面既然會是 `final`，那麼外面的 local variable 自然也該是 final。

話說回來，Closure 裡面的變數，也不一定要是 final/immutable，好比 JavaScript 裡面就沒有這個限制。因此 **Inner 裡面的 r 必須是 final** 這件事情，我想應該是個語言設計的選擇，我還不知道會不會是其他的限制。(沒有辦法像 C 一樣用指標指向 int 等基本型別，而 Java 總是 pass by value？)

### Anonymous Class

Anonymous class 其實就是一個擺的位置比較特別的 local class，所以剛好沒有名字而已，bytecode 看起來沒什麼變化，而且是 compiler 自動給個流水號當名字而已。

舉例來說，底下就是一個 Runnable 的 anonymous class

```java
(new Thread(new Runnable() {
    public void run() {
        System.out.println("This class is an anonymous class");
    }
})).run();
```

有趣的一點是，在 static 語境底下宣告的 anonymous 跟 instance 語境底下宣告的 anonymous class，產生的 bytecode 是不一樣的。

Anonymous class，但是放在 static 裡面跟 放在 ThreadTest 裡面，產生出來的 bytecode 不一樣。

```java
class ThreadTest {

    ThreadTest() {
        (new Thread(new Runnable() {
            public void run() {
                System.out.println("This class is anonymous class in instance");
            }
        })).run();
    }

    public static void main(String[] args){
        (new Thread(new Runnable() {
            public void run() {
                System.out.println("This class is anonymous class in static");
            }
        })).run();
    }
}
```

上面的範例，一個在 ThreadTest 的實體裡面宣告了匿名類別，也在 static main method 裡面宣告了同樣實作的匿名類別。把兩個匿名類別的 bytecode 倒出來之後，理所當然前者的 bytecode 會多了指向 ThreadTest instance 的 `this` reference，明白了 `static` 的差異，在這邊也沒什麼好奇怪的。

# Inner classes cannot have static declarations

**Inner classes cannot have static declarations** 這個警告，應該很多人都看過了。Static nested class 可以，但是 Non-static inner class 裡面不能使用 `static` 變數，究竟是為什麼呢？

```java
class OuterClass {
    class NonStaticNested {
        static int foo = 3;
    }

    static class StaticNested {
        static int bar = 3;
    }
}
```

編譯的時候就會遇到以下的問題

```bash
$ javac OuterClass.java
OuterClass.java:3: error: Illegal static declaration in inner class OuterClass.NonStaticNested
        static int foo = 3;
                   ^
  modifier 'static' is only allowed in constant variable declarations
1 error
```

**而且你把 foo 加上 final 就可以編譯了！**

我並沒有查到官方說明，頂多只有說這個行為沒有說原因。我看到最好的解釋是 [Why does Java prohibit static fields in inner classes?](https://stackoverflow.com/questions/1953530/) 裡面 [saif](https://stackoverflow.com/users/1354334/saif) 的解釋

> class Initialization sequence is a critical reason.

要產生一個 Instance 之前，要先讓 JVM 載入該 Class，才能用 Class 產生 Object instance。[static field](https://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.3.1.1) 就是綁在 Class 上面，而不是 Instance 上面。

[12.4.1. When Initialization Occurs](https://docs.oracle.com/javase/specs/jls/se7/html/jls-12.html#jls-12.4.1) 說了什麼時候會 initialize 一個 Class

* T 是一個 class，而且要產生一個 T 的 instance `new T()`
* T 是一個 class，而且要呼叫 T 的 static method
* 要賦值給 T 的一個 static field
* 要使用 T 的一個 static field 而且那個 field 不是 [constant variable](https://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.12.4)
* T 是 Top-class 而且接下來會用到 T 的 nested class

回憶一下前面講的東西，**Non-static nested class 的實體，總是會綁著 Enclosing class 的一個實體**，也就是 bytecode 裡面的 `final OuterClass this$0`。嘗試在其他的程式碼裡面使用到 `NonStaticNested.foo` 的時候，觸發了前面的三個條件，於是嘗試讀進 T。

如果 T 現在是 Inner class，它裡面有個 `this$0` 要連向 OuterClass 的實體，卻在 static 的語境下找不到能用的實體。因此若允許在 Non-static inner class 裡面使用 static variable 或 method，就會發生這個奇怪的結果。

若我們給它加上 final 則可以因為第四點而繞過 T 的初始化，所以能夠通過編譯。

以上是我讀了文件的見解，沒有參與過 JVM 的開發所以不敢非常確定。若你有確定的想法並且寫出來，我會很感激！

