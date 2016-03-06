title: Android Studio 使用 RetroLambda
s: android-retrolambda
date: 2015-08-24 00:08:18
tags:
    - geek
    - tools
    - android
categories: geek
---

一直以來都習慣用 vim + ant 來開發 Android Application/System。<del>因為這樣不夠潮</del>前一陣子給朋友推坑後，下定決心要學習用 IDE，既然要寫 Android，當然就選了 Android Studio。

# Android Studio

{% asset_img android_studio-splash.png [Android Studio] %}

[Android Studio](https://href.li/?http://developer.android.com/tools/studio/index.html) 由 [IntelliJ community](https://href.li/?https://www.jetbrains.com/idea/download/) 版本修改而來，看起來主要是針對 Android 開發做了調整，好比可以點開 resource 的 layout xml 就有 preview 畫面可以看，或是，你可以很輕易地打開一個 Android Project，卻找不到選項打開一個「普通的 Java Project」

沒錯，我找不到開普通 Java 專案的選項，想想也算合理。Android Studio 目的要提供一個「寫 Android app 的工具」，而不是「一個 IDE，可以寫各種語言包括 Android」。從公司的角度來看我能理解這樣降低維護成本的決定。

但從一個使用者來看，花時間投資在一個工具上，只能寫 Android 不能寫 Java 是有點浪費。

<!-- more -->

Android Studio 一定不可能大改，所以鄉民還是找出了方法[在 Android Studio 寫一般的 Java project](https://href.li/?http://stackoverflow.com/a/26196451)。我對這個 IDE 還不是很了解，不知道會不會有什麼副作用，所以我選擇再裝一個 IDEA Intellij Community 版本，反正才 100+ MB，便宜啦。

```bash
$ ln -sf ~/.AndroidStudio1.3 ~/.IdeaIC14
```

我透過 symbolic link 讓兩邊共享同一份設定檔，也許會有潛在的問題，不過目前的使用方法，idea 用來開 normal java project，接著再打開 android studio 用 open existed project 的方法，應該是不會遇上什麼問題。

## 安裝 IdeaVim

安裝 Android Studio 就像一般應用程式一樣，沒啥好說的。如果你也慣用 VIM，可以試著裝這個 plugin 滿足一下手指肌肉。

* 依序點 File / Settings / Plugins
* Browse repositories / 輸入 IdeaVim

就像 **~/.vimrc**，IdeaVim 也可以寫一個 **~/.ideavimrc** 來放設定檔

{% asset_img install-ideavim.png [IdeaVim] %}


## 備份

千辛萬苦把 IDE 調教好之後，記得要時時備份自己的設定檔

* File / Export Settings

# Retrolambda

Javascript 寫了一段時間之後，開始對 Functional Programming 感興趣。[小安老師](http://sayuan.github.io/)說 Java8 加入了很多新特性跟 FP 有關，值得一試。

目前我在 Java 的主要用途集中在 Android 開發，Android 頂多也在 [4.4 之後才支援 Java 7](http://tools.android.com/recent/androidstudio032released)，就連我的日常用機也不過是 Android 4.0，對 Java 8 語法可說看得到吃不到，幸好有 Retrolambda。

[Retrolambda](https://github.com/orfjackal/retrolambda) 的網站說它會把 java8 編出的 bytecode，轉成舊的 Java runtime 能執行的格式。讓鄉民也能在舊機器上面用潮到出水的新語法，包括

* [Lambda expression](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)
* [Method references](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html)
* [Try-with-resources statements](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
* [Default Methods](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html)

## Install JDK6 on Debian

因為我希望能夠編譯出 JDK6 的 bytecode，所以要先安裝 JDK6。不過 Debian testing 裡面找不到 sun 版本的 jdk6，應該是因為授權的關係，無法直接釋出可以使用的 binary 檔。

要安裝 JDK6 可以參考 Deiban 的教學頁面[Oracle/Sun Java](https://wiki.debian.org/Java/Sun)，透過 **java-package** 這個工具，把 jdk6 binary 打包成 deb 檔讓我們統一使用 apt 來管理。

先從 Oracle 網站把 jdk6 抓下來，好比放在 **/tmp/jdk-6u45-linux-x64.bin**。如果你跟我一樣把 /tmp 掛成 tmpfs，記得要 resize 成足夠大的容量來製作 deb 檔

```cmd
$ sudo aptitude install java-package
$ make-jpkg /tmp/jdk-6u45-linux-x64.bin
$ sudo dpkg -i oracle-java6-jdk_6u45_amd64.deb
```

安裝完 deb 檔之後，接著設定一些環境變數給 Android Studio 的 gradle 使用，我在 **~/.bashrc** 裡面加入了這幾行，預設使用 java8。

```bash
export JAVA6_HOME=/usr/lib/jvm/jdk-6-oracle-x64
export JAVA7_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export JAVA8_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

## build.gradle

在 Android 上面要用 [gradle-retrolambda](https://github.com/evant/gradle-retrolambda)，網站上面已經把設定檔寫得很詳細了，這邊列出我的[修改](https://github.com/walkingice/momome/commit/fa1e5746b7e276d2e7c933fa3dc8d0257651d6e0)


```scala
buildscript {
    .....
    dependencies {
        classpath 'me.tatarka:gradle-retrolambda:3.2.0'
    }
}

.....

apply plugin: 'me.tatarka.retrolambda'

.....

android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

retrolambda {
    jdk System.getenv("JAVA8_HOME")
    oldJdk System.getenv("JAVA6_HOME")
    javaVersion JavaVersion.VERSION_1_6
    defaultMethods true
    incremental true
}
```

範例中可以看見我使用了 **JAVA8_HOME** 與 **JAVA6_HOME** 這個環境變數，也就是前一步的設定，意思就是我要用 java8 的 compiler，想要編譯出 java6 的 bytecode。

## 動手玩

既然在 build.gradle 裡面打開了 defaultMethods，就以這個最簡單的功能來測試一下 retrolambda 會不會動

```java
public interface Foo {
    default void sayHi() {
        android.util.Log.d("Foo", "Hi");
    }
}
```

接著隨便拿個 MainActivity 來用

```java
class MainActivity extends Activity implements Foo {
    ...
    protected void onCreate(Bundle saved) {
        sayHi();
    }
}
```

```bash
$ ./gradlew --daemon installDebug
```

安裝並執行程式，應該就能在 logcat 看到 "Hi"。成功就代表 command line 的設定已經完成了。Happy hacking!....啊，還有 IDE 要搞定

## Config Android-Studio Project

到這邊理論上已經裝好了 jdk6 / jdk8 / retrolambda。接著要在 Android Studio 裡面也能順利工作。首先把 jdk 1.8 的 sdk 版本加進去 Android Studio 裡面

1. 選 File
1. Project Structure
1. Platform Settings - SDKs
1. 按下 + 號 新增一個 JDK
1. 找到 jdk1.8 的安裝位置，我的是在 /usr/lib/jvm/java-8-openjdk-amd64

到這邊設定好之後，Android Studio 就認得 jdk1.8 了，我的結果大概如下

{% asset_img jdk1_8.png [Android Studio set JDK] %}

接著設定 project，就在同一個 popup window 的 Project 那一頁

1. 指定 Project SDK 用我們剛剛產生的 1.8
1. Project language level 用 8 - Lambdas

{% asset_img project_config.png [Android Studio set Project] %}

到這邊應該就設定完成，可以編一下程式來玩玩看。同時摸索 IntelliJ 以及 gradle 這兩個我不是很熟的東西，以上的步驟很可能會有少做 (我忘記要紀錄) 或是多做 (阿宅最討厭的多餘動作) 的地方，還望鄉親多指教。(鞋櫃可能很懶得弄留言板，鄉親不管是直接寫信、在 social network 或是直接發篇 blog 文來吐嘈都很歡迎啦～)

Happy Hacking!

