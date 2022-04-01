title: TransitionDrawable 用兩張以上的圖
s: drawable
date: 2017-09-04 21:55:51
tags:
    - android
categories: geek
---

當我們需要一張圖片，慢慢交錯顯示出另外一張圖片的時候，經常使用 TransitionDrawable。繼承自 LayerDrawable 的 TransitionDrawble 的實作，則是在 [onDraw](https://android.googlesource.com/platform/frameworks/base/+/oreo-r6-release/graphics/java/android/graphics/drawable/TransitionDrawable.java#205) 的時候只拿前兩張來交換顯示。

實務上使用時，需要交換顯示的圖片往往不只兩張，這時候就需要一點取巧的做法。

<!-- more -->

基本精神其實也有點土炮，就是在 transition 發生之前，手動更改 Layer 裡面下一張要顯示的圖。我的使用情境是 App 第一次啟動的時候，顯示的 first-run 畫面有個 ViewPager，ViewPager 更換不同頁面的時候，想要連背景圖一併交錯變換。

要更換 LayerDrawable 裡面的圖片，需要為前兩個 Layer 設定一個名字(id)，因為只需要改兩個，姑且取名為奇數與偶數

修改 **res/values/ids.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <item name="bg_even" type="id" />
    <item name="bg_odd" type="id" />
</resources>
```

接著設定 TransitionDrawable，檔名為 **layered_bg.xml**，把前面兩層加上 id

```xml
<?xml version="1.0" encoding="utf-8"?>
<transition xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@id/bg_even"
        android:drawable="@drawable/gradient_0" />
    <item
        android:id="@id/bg_odd"
        android:drawable="@drawable/gradient_1" />
    <item android:drawable="@drawable/gradient_2" />
    <item android:drawable="@drawable/gradient_3" />
</transition>
```

接著把這個 LayerDrawable 裡面的幾個 drawable 都找出來放進 array。因為我的使用情境是在 First-run，鮮少會被使用，不想要產生一堆背景圖的 resource 檔案，也順便附上了如何手動產生 Drawable 的寫法

```java
// initialize background drawable from xml
TransitionDrawable bgDrawable = (TransitionDrawable) ContextCompat.getDrawable(getContext(), R.drawable.animated_background);
Drawable[] layerDrawables = new Drawable[bgDrawable.getNumberOfLayers()];
for (int i = 0; i < layerDrawables.length; i++) {
    layerDrawables[i] = bgDrawable.getDrawable(i);
}

// if you want to create background drawable manually
final GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TR_BL;
layerDrawables = new Drawable[]{
        new GradientDrawable(orientation, new int[]{0xFFFF0000, 0xFF00FF00}), // red to green
        new GradientDrawable(orientation, new int[]{0xFF0000FF, 0xFF00FF00}),
        new GradientDrawable(orientation, new int[]{0xFF0000FF, 0xFFFFFF00}),
        new GradientDrawable(orientation, new int[]{0xFFFF00FF, 0xFFFFFF00}),
};
bgDrawable = new TransitionDrawable(layerDrawables);
bgDrawable.setId(0, R.id.bg_even);
bgDrawable.setId(1, R.id.bg_odd);

bgDrawable.setCrossFadeEnabled(true);
```

接著就是給 TransitionDrawble 抓交替的邏輯，因為我的使用情境是 ViewPager，所以把邏輯寫在 `OnPageChangeListener` 裡面。當下一張應該要是偶數時，就把下一筆 Drawable 放在 id 為 `bg_even` 的圖層上。因為一開始顯示的是 0，偶數。所以從偶到奇是 `startTransition`，從奇數回到偶數就是 `reverseTransition`

```java
@Override
public void onPageSelected(int newIdx) {
    final int duration = 1000;
    final Drawable nextDrawable = layerDrawables[newIdx % bgDrawables.length];

    if ((newIdx % 2) == 0) {
        // next page is even number
        bgDrawable.setDrawableByLayerId(R.id.bg_even, nextDrawable);
        bgDrawable.reverseTransition(duration); // odd -> even
    } else {
        // next page is odd number
        bgDrawable.setDrawableByLayerId(R.id.bg_odd, nextDrawable);
        bgDrawable.startTransition(duration); // even -> odd
    }
}
```
