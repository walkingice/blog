title: Using Mockito
s: mockito
date: 2017-04-24 22:27:16
tags:
    - geek
    - test
categories: geek
---

[Mockito](https://static.javadoc.io/org.mockito/mockito-core/2.6.1/allclasses-noframe.html) 是一個常見的 testing framework，這邊稍微紀錄一下我用到的測試寫法，包括

1. change private variable
1. specify return value
1. to verify whether a method is invoked
1. to verify parameters of a method call

<!-- more -->

## 修改 private variable

假設要修改物件 `mObject` 的 private String `mFoo` 為 `foobar`。這邊會用到 [Whitebox](https://static.javadoc.io/org.mockito/mockito-core/1.10.19/org/mockito/internal/util/reflection/Whitebox.html)，也就是說會破壞物件的封裝，盡量不要過度使用

```java
Whitebox.setInternalState(mObject, "mFoo", "foobar");
```

## 修改物件的回傳值

舉例來說，想要測試的程式碼如果呼叫到 `mObject` 的 method `isInitialized()`，可以指定回傳值為 `true`。如果我們不需要真正會動的 mObject，這樣就不用花一堆時間在 mObject 的初始化上面

```java
doReturn(true).when(mObject).isInitialized();
```

## 測試 method call 有被執行

跑完一個測項之後，想要確認某一個 method 是不是有被執行，舉例來說，是不是真的有呼叫一次 [Service.startActivity](https://developer.android.com/reference/android/app/Service.html#startForeground(int,%20android.app.Notification)。底下的例子中，我們並不在乎啟動 Service 的參數內容，所以用 *Any*

```java
verify(mSpyService, times(1))
    .startForeground(Mockito.anyInt(), Mockito.<Notification>any());
```

如果想測試 method call 的物件參數是不是符合預期，好比 Notification 的顏色

```java
verify(mSpyService, times(1)).startForeground(
    Mockito.anyInt(),
    Mockito.argThat(new ArgumentMatcher<Notification>() {
        @Override
        public boolean matches(Object argument) {
            Notification notification = (Notification) argument;
            // put assertion here!
            Assert.assertEquals(notification.color, 0xFF0000);
            return true;
        }
    }));
```

如果只是想測試簡單的 primitive，好比 `Service.stopSelf(int)`

```java
verify(mSpyService, times(1)).stopSelf(eq(123));
```

剩下的，有想到啥再繼續從這篇裡面補完吧

