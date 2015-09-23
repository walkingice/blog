title: SimpleAdapter and ViewBinder
s: simpleadapter-viewbinder
date: 2015-09-23 23:14:59
tags:
    - java
    - geek
    - android
categories: geek
---

這是一篇以前寫過的[老文章](http://walkingice.blogspot.tw/2013/06/simpleadapter-and-viewbinder.html)了，以前的 syntax highlight 弄得不好，想說貼到這邊排版比較好看，順便備份一下。

# SimpleAdapter should be enough

ListView 是 Android 一個極為常用的元件，經常看見為了「用自己定義的 xml 來產生 ListView row」而自己實作了一個 BaseAdapter。這樣的作法也不能說不對，我一開始也是這樣。為了烤蛋糕自己做了一個烤箱的事情，幾次之後還是覺得有點怪其實絕大多數的時候我們都不需要額外實作一個 Adapter，直接拿 SimpleAdapter 來用即可。

<!-- more -->

SimpleAdapter 需要一堆資料，還要有資料對應到 View 的表格。首先看一筆資料，每一筆資料都是一個 Map，從 JSON 的格式來看就像是

```javascript
var julian = {
    "name": "Julian Chu",
    "avatar": R.drawable.julian,
    "location": "Taiwan",
    "prefer": "blue"
}
```

在 Java 裡面用 Map 產生等價的 Julian 的資料就會是以下的作法

```java
Map<String, Object> julian = new HashMap<String, Object>();
julian.put("name", "Julian Chu");
julian.put("avatar", R.drawable.julian);
julian.put("location", "Taiwan");
julian.put("prefer", new Integer(Color.BLUE));
```

既然要放進 ListView，當然不會只有一個 User。於是可以寫一個 method，從 User 資料產生 Map 並放進 List 裡面

```java
private void appendData(List<Map<String, Object>> records, User user) {
    Map<String, Object> record = new HashMap<String, Object>();
    record.put("name", user.getName());
    record.put("avatar", user.getAvatar());
    record.put("location", user.getLocation());
    record.put("prefer", user.getPreferColor());
    records.add(record);
}
```

接著看 SimpleAdapter 的建構子用法，把我們剛剛存放資料的 records 放進 SimpleAdapter

```java
SimpleAdapter(Context context,
        List<? extends Map<String,?>> records,
        int resource,
        String[] from,
        int[] to);
```

白話文解釋就是

1. 我要建立一個 SimpleAdapter，先給我 context
1. 給我一串資料，放在 List 裡面
1. List 裡面的每一筆資料格式都是 <code>Map&lt;String, ?&gt;</code>，也就是 **key=String, value=任意資料結構**
1. 給我一個 layout xml 檔(resource)，產生 view 的時候會拿它來當 template
1. 對於 Map 裡面的資料，我要拿哪些欄位來用(from)
1. 取出的欄位，要對應到 xml 檔裡面的哪些 view (to)

假設我們的 layout row.xml 如下，它有一個 TextView 與 ImageView

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@id/row_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    <TextView
            android:id="@id/row_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
    <ImageView
            android:id="@id/row_avatar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
</LinearLayout>
```

接下來告訴 SimpleAdapter 對應的關係(from -> to)，把名字對應到 TextView，大頭照對應到 ImageView

```java
String[] from = {"name", "avatar"};
int[] to = {R.id.row_name, R.id.row_avatar};

SimpleAdapter adapter = new SimpleAdapter(
        context,
        records,
        R.layout.row,
        from,
        to);
```

如此一來，SimpleAdapter 就會透過 <code>Map.get(from[0])</code> 取出名字，然後再 <code>findViewById(to[0])</code> 找到 TextView，接著就 <code>TextView.setText();</code> 設定名字

也因此只要設定好對應關係就可以產生我們要的 view 了，其他生成 view 的事情就交給 SimpleAdapter 去做，我們不用再插手，也不用自己再實作一份。

通常至此會有疑問 **我們僅僅只是對 string key 跟 view id 建立關聯，但是 SimpleAdapter 怎麼知道要 setText？**

更進一步的問題就會是：**我們想要對 View 做更多客製化，好比「根據每個 User 的 Prefer Color 設定該 row 的背景顏色」，該怎麼做？**，也就是說 setText 無法滿足<del>只出一張嘴的</del>老闆的需求

# ViewBinder

第一個問題的解答是，SimpleAdapter 對 TextView, ImageView, Checkable 有作預設的處理。findViewById 之後會用 <code>if (v instanceof TextView)</code>來判斷是不是 TextView，找到 TextView 就把 data 拿來 <code>TextView.setText(data.toString())</code>

針對第二個問題，SimpleAdapter 有個 inner class 叫 View Binder(跟底層的 Binder 無關)，我們可以對它實作

```java
class MyViewBinder implements SimpleAdapter.ViewBinder {
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if (data instanceof Integer) {
            Integer color = (Integer)data;
            view.setBackgroundColor(color.intValue());
        }
    }
}
```

接著把 from, to 多做一組 mapping 即可

```java
String[] from = {"name", "avatar", "prefer"};
int[] to = {R.id.row_name, R.id.row_avatar, R.id.container};
adapter.setViewBinder(new MyViewBinder());
```

這樣子我們就多教了 SimpleAdapter 一件事：**看到 Integer 就把它當成背景顏色來設定**

其實這個例子寫得滿蠢的，希望能夠這樣有保留到最重要的精神
