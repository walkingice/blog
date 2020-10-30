title: Gralde build error
date: 2020-10-30 16:34:37
categories: geek
tags:
    - android
    - kotlin
    - gradle
---

在 Android 的 app moudle 裡面，嘗試把一些獨立的類別搬到新增加的一個 library module 的時候，遇到了類似這樣的錯誤

```
> The consumer was configured to find an API of a component, as well as attribute 'com.android.build.api.attributes.BuildTypeAttr' with value 'debug', attribute 'org.jetbrains.kotlin.platform.type' with value 'androidJvm'. However we cannot choose between the following variants of project :common-libs:
```

<!-- more -->


```
 Library    +-------------+
 module     | common-libs |
            +-------------+
                ^
                |
                |
  Base      +-------+
  module    |  App  |
            +-------+
```

上圖，一開始只有 app depends on common-libs 這個 library module

```
            +-------------+
            | common-libs |
            +-------------+
 Library        ^
 module         |           +--------+
                |           |my-utils|
                |           +--------+
                |              ^
                |              |
  Base      +-------+----------+
  module    |  App  |
            +-------+
```

後來把 app 裡面的類別拉出來，放到 my-utils 裡面，這樣也沒問題

```
            +-------------+
            | common-libs |<-----+
            +-------------+      |
 Library        ^                |
 module         |           +--------+
                |           |my-utils|
                |           +--------+
                |              ^
                |              |
  Base      +-------+----------+
  module    |  App  |
            +-------+
```

但是 my-utils 開始用到 common-libs 裡面的東西，增加 `implementation project(':common-libs')` 的時候就開始出現類似下方的錯誤訊息

```
== BUILD FAILED ==

FAILURE: Build failed with an exception.

* What went wrong:
Could not determine the dependencies of task ':my-utils:compileDebugKotlin'.
> Could not resolve all task dependencies for configuration ':my-utils:debugCompileClasspath'.
   > Could not resolve project :common-libs.
     Required by:
         project :my-utils
      > The consumer was configured to find an API of a component, as well as attribute 'com.android.build.api.attributes.BuildTypeAttr' with value 'debug', attribute 'org.jetbrains.kotlin.platform.type' with value 'androidJvm'. However we cannot choose between the following variants of project :common-libs:
          - betaDebugAndroidTestCompile
          - betaDebugAndroidTestRuntime
          - betaReleaseUnitTestRuntime
          .....(skip).....
          - developDebugAndroidTestCompile
          .....(skip).....
          - productionReleaseRuntime
          .....(skip).....
        All of them match the consumer attributes:
          - Variant 'betaDebugAndroidTestCompile' capability My_App:common-libs:unspecified declares a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'androidJvm':
              - Unmatched attributes:
                  - Doesn't say anything about com.android.build.api.attributes.BuildTypeAttr (required 'debug')
                  - Doesn't say anything about its usage (required an API)
                  - Provides attribute 'org.jetbrains.kotlin.localToProject' with value 'local to :common-libs' but the consumer didn't ask for it
          - Variant 'betaDebugAndroidTestRuntime' capability My_App:common-libs:unspecified declares a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'androidJvm':
              - Unmatched attributes:
                  - Doesn't say anything about com.android.build.api.attributes.BuildTypeAttr (required 'debug')
                  - Doesn't say anything about its usage (required an API)
                  - Provides attribute 'org.jetbrains.kotlin.localToProject' with value 'local to :common-libs' but the consumer didn't ask for it
          - Variant 'betaDebugApiElements' capability My_App:common-libs:unspecified declares an API of a component, as well as attribute 'com.android.build.api.attributes.BuildTypeAttr' with value 'debug', attribute 'org.jetbrains.kotlin.platform.type' with value 'androidJvm':
              - Unmatched attributes:
                  - Provides attribute 'com.android.build.api.attributes.VariantAttr' with value 'betaDebug' but the consumer didn't ask for it
                  - Provides attribute 'default' with value 'beta' but the consumer didn't ask for it
          - Variant 'betaDebugRuntime' capability My_App:common-libs:unspecified declares a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'androidJvm':
              - Unmatched attributes:
                  - Doesn't say anything about com.android.build.api.attributes.BuildTypeAttr (required 'debug')
                  - Doesn't say anything about its usage (required an API)
                  - Provides attribute 'org.jetbrains.kotlin.localToProject' with value 'local to :common-libs' but the consumer didn't ask for it
          - Variant 'betaDebugRuntimeElements' capability My_App:common-libs:unspecified declares a runtime of a component, as well as attribute 'com.android.build.api.attributes.BuildTypeAttr' with value 'debug', attribute 'org.jetbrains.kotlin.platform.type' with value 'androidJvm':
              - Unmatched attributes:
                  - Provides attribute 'com.android.build.api.attributes.VariantAttr' with value 'betaDebug' but the consumer didn't ask for it
                  - Provides attribute 'default' with value 'beta' but the consumer didn't ask for it

.....(skip).....
```

訊息不是很容易懂，大致上原因為 `App` base module 裡面定義了一些 `productFlavors`，但是處理 `my-utils` module 時找不到對應的 flavor 就爆炸了。因為我有加上 `develop`, `beta`, `production` 等等的 flavor，所以在 `my-utils` 的 build.gradle 補上這一段就可以了

```java
 android {
     flavorDimensions "default"
+
+    productFlavors {
+        register("develop")
+        register("beta")
+        register("production")
+    }
 }
```
