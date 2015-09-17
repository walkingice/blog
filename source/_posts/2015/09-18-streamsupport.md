title: streamsupport
date: 2015-09-18 01:16:11
tags:
    - java
    - geek
    - tools
    - android
categories: geek
---

從字面上來看 Javascrpit 跟 Java 的差異只是在於一個是 script language，另外一個不是 (超級大誤)，所以在 Javascript 做的事情當然都可以用在 Java 裡面。(胡說八道)

在 javascript 裡面經常用 [map](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map), [reduce](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/reduce) 來對資料做處理，好比說

```javascript
var num = ["a", "bb", "ccc", "dddd", "eeeee"].filter(function (str) {
    return (str.length > 2);
}).map(function (str) {
    return str.length;
}).reduce(function (prev, current) {
    return prev + current;
}, 0);
```

這支無用的程式就是依序把一個字串陣列做處理

1. 字元數小於 2 的字串拿掉
1. 把字串陣列轉成整數陣列，內容是該字串的長度
1. 加總每個數字

整個處理的過程就像一個串流(Stream)，Java8 多了 [Stream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html) 這個 utility class，提供了類似的功能，可以讓我們把一個 Array 或是 List 用類似的方法來處理。

但是 Java8 這麼新潮(唔)的東西，不能用在 Android 上面？就像 retrolambda 一樣，也有鄉民弄了 [streamsupport](http://sourceforge.net/projects/streamsupport/) 這個 library，讓我們在 Java6 runtime 玩 stream util

<!-- more -->

# Installation

用 gralde 安裝 streamsupport 相當簡單，只要在 build.gradle 的 dependency 加上一行就可以了

```groovy
...
dependencies {
    ...
    compile 'net.sourceforge.streamsupport:streamsupport:1.1.3'
    ...
}
...
```

撰文當下最新的 stable 版本就是 1.1.3。不是用 gradle build system 的人，可以到 [sourceforge](http://sourceforge.net/projects/streamsupport/files/1.3.1-stable/) 上面下載 jar 檔，別忘了順便抓 doc 檔下來參考，才知道會哪些 methods 可用。

# Try it

安裝完了當然要試用一下。

要用 Stream 的功能，要先拿到 Stream 這個物件，Java8 的 Arrays 提供了相關的 methods 來轉化，但是 Java6 runtime 裡面的 Arrays class 沒這東西可用，所以要用 streamsuuport.jar 替代的 J8Arrays class 相同的 API 介面。

這邊寫一個簡單的 sample，假設有個 Foo class，有個 method getName 可以讓我們取得 instance name。對一串Foo 物件，想要轉成 String[] names 的時候可以這麼寫

```java
public static String[] getNames(Foo[] array) {
    return J8Arrays.stream(array).map(new Function<Foo, String>() {
        public String apply(Foo bar) {
            return bar.getName();
        }
    }).toArray(new IntFunction<String[]>() {
        public String[] apply(int length) {
            return new String[length];
        }
    });
}
```

一堆角括號看起來真是地獄啊！這時候就覺得 lambda expression 太重要了 XD

