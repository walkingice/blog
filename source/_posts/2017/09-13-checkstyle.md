title: 使用 Checkstyle/Ktlint 檢查 Java/Kotlin 語法
s: checkstyle
date: 2017-09-13 00:14:12
tags:
    - Android
    - tools
    - git
categories: geek
---

多人開發的場合，大家寫程式的習慣用法往往不一樣。除了實作上的思維不同，在所難免，至少在程式碼風格上可以一致。

不同的語言有不同的 coding style，在 Java 裡面我通常就是參考(以前是 Sun Microsystems 現在是 Oracle) [Code Conventions for the Java TM Programming Language](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html)。隨著開發時間的拉長，遞交程式的數量變多之後，總是會有幾次忘記加上空白之類的小瑕疵，這種檢查理當交給程式去做。

雖然寫程式沒有 freestyle，但是我們有 [checkstyle](http://checkstyle.sourceforge.net/)

<!-- more -->

# checkstyle for Java

Checkstyle 是一個 Java 開發常見的工具，顧名思義，協助檢查程式碼的風格，從 [wiki](https://en.wikipedia.org/wiki/Checkstyle) 的介紹來看，2001 年就開始了，算是相當成熟的工具，擁有許多細部調整的空間。

Android 開發現在多以 gradle 為建構工具，要在 gradle 裡面用 checkstyle 很簡單，以我自己的[小玩具](https://github.com/walkingice/MomoDict/commit/1b3c31af7a8ce36198d8efb027f08ce899651def)為例，修改 `app/build.gradle`

```java
 apply plugin: 'realm-android'
 apply plugin: "kotlin-android"
+apply plugin: 'checkstyle'
+
+check.dependsOn 'checkstyle'

 android {
 ......
 }

+task checkstyle(type: Checkstyle) {
+    configFile file("${project.rootDir}/quality/square_picasso_checkstyle.xml")
+    source 'src'
+    include '**/*.java'
+    exclude '**/gen/**'
+
+    classpath = files()
+}
+
```

帶有「+」就是我新增的行，相當直觀。載入 **checkstyle** plugin，新增一個 task 叫 `checkstyle` 並且把它加進 `check` 這個 task 的相依性裡面。以 `./gradlew checkstyle` 就可以測試了。

你一定有注意到那個 configFile，用來設定 checkstyle 要以哪些條件來檢查程式碼的語法。我把 Square Picasso 用的 [設定檔](https://github.com/square/picasso/blob/master/checkstyle.xml) 拿來擺在 *quality/square_picasso_checkstyle.xml*。

內容也相當直觀，好比底下這一段就是限制每行不得超過 100 個字元，避免過長造成閱讀不便。想要有更多設定，可以參閱 [Standard Checks](http://checkstyle.sourceforge.net/checks.html)
```xml
<!-- Checks for Size Violations.                    -->
<!-- See http://checkstyle.sf.net/config_sizes.html -->
<module name="LineLength">
<property name="max" value="100"/>
</module>
```

# ktlint for Kotlin

自從 Android 宣布 Kotlin 成為官方語言之後，原本就很受歡迎的 Kotlin 更如黃河氾濫一發不可收拾。很不幸地，checkstyle 只能檢查 Java 語法，並不適用於 Kotlin。而且 Kotlin 還在快速開發中，語法應該還會持續改變。我自己也還沒看到類似 Java 的 Code Conventions.

如果你還是很想要加入語法檢查，可以跟我一樣用 [ktlint](https://github.com/shyiko/ktlint)，目前最新的版本是 0.9.2，gradle 的設定方法類似 checkstyle

```java
 check.dependsOn 'checkstyle'
+check.dependsOn 'ktlint'
+
+configurations {
+    ktlint
+}

dependencies {
     ........
+
+    ktlint 'com.github.shyiko:ktlint:0.9.2'
+}
+
+task ktlint(type: JavaExec) {
+    main = "com.github.shyiko.ktlint.Main"
+    classpath = configurations.ktlint
+    args "src/**/*.kt"
+    // prepend "--reporter=plain?group_by_file" arg to change the reporter
+    // see https://github.com/shyiko/ktlint
 }
```

設定後執行 `./gradlew ktlint` 就行了。如果有些檔案不想被檢查，好比 Test 結尾的檔案，可以修改成 `args "src/**/*.kt !src/**/*Test.kt"`

ktlint 說自己沒有 configuration，而是可以[延展](https://github.com/shyiko/ktlint#creating-a-ruleset)，藉以加入更多功能("no configuration" doesn't mean "no extensibility")。但我現在還沒研究好該怎麼做，等未來有需求再回來更新這裡。

# add git hook

需要手動執行的事情總是容易忘記，我們能夠利用 git hook 在 `git push` 執行一次語法檢查，檢查不過就中斷送出，底下是一個簡單不保證在每個環境上都能完美執行的 script，把它存成 **.git/hooks/pre-push** 即可

```bash
#!/bin/sh

PROJ_DIR=$(git rev-parse --show-toplevel)

${PROJ_DIR}/gradlew checkstyle

if [ $? -eq 0 ]; then
    echo "checkstyle OK"
else
    echo "checkstyle fail\n"
    echo "please ensure coding style is acceptable by running"
    echo "\n\t./gradlew checkstyle\n"
    exit 1
fi

${PROJ_DIR}/gradlew ktlint

if [ $? -eq 0 ]; then
    echo "ktlint OK"
else
    echo "ktlint fail\n"
    echo "please ensure coding style is acceptable by running"
    echo "\n\t./gradlew ktlint\n"
    exit 1
fi
```

目前還沒看到有什麼好的方法，可以讓別人 git pull 專案的時候就自動把這些東西弄好。通常就是再寫個 bootstrap 的 script 來設定好 git-hook，當然寫在 README 裡面提醒其他人也行。如果有人送 Pull Request 到你的專案，如何確保對方的 style rule 跟你一樣？就是在 [CI](https://zh.wikipedia.org/wiki/%E6%8C%81%E7%BA%8C%E6%95%B4%E5%90%88) 裡面跑 checkstyle 囉。

# 雜感

我對程式碼風格算是挺要求的。不同於 Design Pattern 需要一點悟性，程式架構的設計需要經驗累積，，或各種語言的精髓需要磨練使用才能體悟，「風格」基本上就是照表操課。小括號後面的大括號需不需要加空白？大括號要放在原來的一行，或是在下一行對齊？

只要給一本操作手冊照著做就行了。不管天份高低，做出來就是差不多。

雖說程式碼排版得漂亮不見得代表品質好，但是連排版都不用心，很難說其他地方好，也連帶覺得撰寫程式碼的人不夠牢靠。一致的風格可以讓讀程式碼的時候不被各種寫法干擾，專注思考架構及演算法的設計。

曾經問過外國同事，遇到別人送來的 patch 風格不統一怎麼辦？他說，如果沒有在 CI 就擋下來，那麼頂多給建議就好。我覺得也很有道理，畢竟 reviewer 百百種，大家喜歡的也不一樣。要是有個 checkstyle 擋在中間，大家照同樣的遊戲規則，也是挺公平的。

寫程式不能 freestyle！
