title: 實作 Android ListView
s: implement-listview
date: 2015-09-26 01:24:46
tags:
    - java
    - geek
    - android
categories: geek
---

最近在改別人留下的 Android code。

好一段時間沒寫 Android 了，尤其我的記憶力又特差，經常要翻一下以前寫的程式才能回想起來某些 widget 怎麼用，ListView 就是其中一個。雖說一個功能可以有多種不同實作方式，各人又有各自常用的套招，即便 ListView 沒特別難，還是經常可以看見各種不同的寫法。

原本只是要貼一些程式碼進自己的筆記 wiki 裡面，想說反正都寫一些了，不如多寫一點貼出來給有緣人當參考，雖然更多時候會是我自己一直回頭來看。

用 ListView 的時機通常就是這幾種

1. 把一堆字串顯示在 ListView 當中
1. 把客製化的 View 顯示在 ListView 當中
1. 提供單選 single choice 的選單
1. 提供多選 multiple choices 的選單
1. 實作客製化的 multiple choices

接下來針對以上常見的需求來介紹，同時也會處理 Click 事件

<!-- more -->

先從單純地顯示字串開始

# Single row list item

這是最簡單的 ListView，在 Android API Demo 裡面就有各式各樣的作法。只要產生 ArrayAdapter 的時候塞一個 String array 再丟給 ListView 用就行了。指定使用 Android 預設的 layout resource <code>android.R.simple_list_item_1</code> ，沒特別原因不要自己再手動做一個 Layout，這樣外觀才會跟系統一致。

<div class="img-row">{% asset_img one-row.png single row list item %}</div>

(那個 Hello world 的 TextView 是我隨手亂塞懶得移除的，請忽略它的存在)

程式實際寫起來很簡單，buildData 只是我一個產生測試資料的 method。

```java
private String[] buildData(int length, String name) {
    String[] array = new String[length];
    for (int i = 0; i < length; i++) {
        array[i] = name + ":" + i;
    }
    return array;
}

private void initViews() {
    ListView mList = (ListView) findViewById(R.id.listView);
    String[] mNames = buildData(30, "Name");

    ListAdapter mAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1,
            mNames);
    mList.setAdapter(mAdapter);
}
```

有時候會想要另外一種有兩行，文字一大一小的 list item，也就是 <code>android.R.layout.simple_list_item_2</code> 的外觀。這個 Android 預設提供的 Layout 有兩個 TextView，分別都有固定的 Android 預設 id 可以用

* android.R.id.text1 - 字比較大，上方的 title
* android.R.id.text2 - 字比較小，下方的 description

```java
ListAdapter mAdapter =
        new ArrayAdapter<Struct>(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                mNames);
```

這樣的意思就是說，拿 simple_list_item_2 來 layout，每一行 row 都是把 mNames 的物件做 toString() 的結果，放到 id 為 **text1** 的 view，也就是 title。

如果把建構子的第三個參數換成 <code>android.R.id.text2</code>，就是把 toString() 的結果放在第二行比較小的 TextView 上面。

# Two rows list item

承上繼續使用 <code>android.R.layout.simple_list_item_2</code>。如果要同時顯示 Title 跟 Description 呢？最常見的方法就是實作一個 ListAdapter

<div class="img-row">{% asset_img two-rows.png two rows list item %}</div>

(唔，請繼續忽略 hello world textview)

```java
class Struct {
    public String iName;
    public String iDesc;

    Struct(String name, String desc) {
        iName = name;
        iDesc = desc;
    }
}

private Struct[] buildData(int length, String name, String desc) {
    Struct[] array = new Struct[length];
    for (int i = 0; i < length; i++) {
        array[i] = new Struct(name + ":" + i, desc + "," + i);
    }
    return array;
}

private void initViews() {
    ListView mList = (ListView) findViewById(R.id.listView);
    Struct[] mItems = buildData(30, "Name", "Desc");
    ListAdapter mAdapter =
            new ArrayAdapter<Struct>(this,
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    mItems) {
                @Override
                public View getView(int pos, View convert, ViewGroup group) {
                    View v = super.getView(pos, convert, group);
                    TextView t1 = (TextView) v.findViewById(android.R.id.text1);
                    TextView t2 = (TextView) v.findViewById(android.R.id.text2);
                    t1.setText(getItem(pos).iName);
                    t2.setText(getItem(pos).iDesc);
                    return v;
                }
            };
    mList.setAdapter(mAdapter);
}
```

首先定義出用來存放資料的結構 Struct，產生一組陣列之後交給自製的 ArrayAdapter。這個 ArrayAdapter 覆寫了 getView，手動把資料取出，找出 Title 與 Description 的 TextView 來設定字串。

自己實作 Adapter 是常見的作法，但我個人不愛這一招。

另外比較建議用 <code>getItem()</code> 與被綁定的資料陣列互動，而不建議直接拿 mItems 來用。對這個 Adapter 來說「它不應該知道 mItems 的存在」，應該遵循 API 介面所暗示的無知狀態，產生 ArrayAdapter 的時候就指定了它的 generic type(在此例中就是 Struct)，再利用 getItem 取得感興趣的物件。

寫成這樣的好處之一，就是這個 Anonymous class 可以寫成外部的具名 Class。(唔，好像也沒特別好)

# Handle click event

其實也不是什麼難事，直接在 ListView 上面掛個 OnItemClickListener 就行了

```java
mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView,
            View view,
            int position,
            long l) {

        Adapter adapter = adapterView.getAdapter();
        Struct clicked = (Struct) adapter.getItem(position);
    }
});
```

有些人的習慣是拿 position 去外部的 adapter 找出該位置的 item 來做後續處理，如果該 Adapter 有用上泛型，getItem 的結果就不需要轉型。如同前面所述，我的習慣是盡量不假設外部的 adapter 是哪一個，從 AdapterView 裡面找出綁定的 adapter，再從該 adapter 取出 item。

比較討厭的是 <code>getItem()</code> 之後要補一個強制轉型的 cast。目前的 API 設計就只能這樣，要嘛就是在 Listener 裡面用某一個**寫死變數名稱，有指定內容類型的 Adapter** 來取用資料，要嘛就是在 Listener 裡面**取出綁定但是不知道內容類型的 Adapter **，自己手動 cast。兩者取其一，各有優缺。

總覺得這樣很可惜，強制轉型其實就是浪費了強型別語言的優勢。


# Custom list item in ListView

前面提到了自己實作 Adapter 的方法。通常接到來自上級的「可不可以稍微自訂一下 List item 的外觀」這種需求的時候，都會手工再打造一個 Adapter。

如果不想要自己實作一個新的 Adapter 呢？可以用 SimpleAdapter 與 ViewBinder 來做。

用 SimpleAdapter 也可以同時實作出「在 ListView 裡面使用客製化的 ListItem」的功能。底下的程式碼僅僅是簡單地把 text1, text2 的 id 跟 name, desc 連結起來，演示了 SimpleAdapter 的 mapping 用法。更複雜一點的 ViewBinder 用法可以參考另外一篇 [SimpleAdapter and ViewBiner](/2015/09/23-simpleadapter-viewbinder.html)

```java
private List<Map<String, Object>> buildData(int length, String name, String desc) {
    List<Map<String, Object>> list = new ArrayList<>();
    for (int i = 0; i < length; i++) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name + ":" + i);
        map.put("desc", desc + ":" + i);
        list.add(map);
    }
    return  list;
}

private void initViews() {
    ListView mList = (ListView) findViewById(R.id.listView);
    List<Map<String, Object>> mItems = buildData(30, "Name", "Desc");
    ListAdapter mAdapter =
            new SimpleAdapter(this,
                    mItems,
                    android.R.layout.simple_list_item_2,
                    new String[] {"name", "desc"},
                    new int[] {android.R.id.text1, android.R.id.text2});
    mList.setAdapter(mAdapter);
}

```

接下來講**可選的 ListView**

# Single choice ListView

繼續從前面的 ListView sample code 改過來，簡單達到兩個重點就可以了

1. 對 ListView 設定 mode 為 <code>AbsListView.CHOICE_MODE_SINGLE</code>
1. resource 指定用 <code>simple_list_item_single_choice</code>

<div class="img-row">{% asset_img single-choice.png 單選列表 %}</div>

如圖所示，Android 提供的 single choice list item 只有一個 TextView，所以指定的 resource id 一定是 <code>android.R.id.text1</code>

```java
ListAdapter mAdapter =
        new ArrayAdapter<Struct>(this,
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1,
                mItems);
mList.setAdapter(mAdapter);

mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        AbsListView list = (AbsListView)adapterView;
        int idx = list.getCheckedItemPosition();
        Struct checked = (Struct)adapterView.getAdapter().getItem(idx);
    }
});
```

顯示很簡單，上面的 code 處理了多數人比較在意的 Click 事件。Click Listener 裡頭先把 adapterView 轉型成 AbsListview 之後用 <code>getCheckedItemPosition()</code> 就能知道是哪一行被選取了。既然知道是哪一行被選了，去跟 Adapter 拿資料就可以做更進一步的處理。

另外，我看過一種我不太贊成的作法：從 View 裡面拿資訊。

好比說透過 id 從 View 裡面找出預期的 TextView，再用 <code>TextView.getText()</code> 來找出使用者到底按下哪一欄，更精確的來說，拿到了 User 到底按了什麼字串。如果你是在做電話簿，很可能有兩個同樣叫做小明的人，這時候就會出問題。

畫面上看到的 View 是拿原始資料 render 完的結果，原始資料通常含有比較多的資訊，不會因為 render 的過程丟失。使用 Adapter 提供的原始資料，某種程度上也是跟 View 那一層鬆綁，減少程式對外觀的假設。

# Multiple choices ListView

Multiple choices 跟 Single choice 很像。不過要找出被選擇的 item 比較麻煩一點，Listener 那邊要費比較多功夫跟 SparceBooleanArray 打交道

1. 對 ListView 設定 mode 為 <code>AbsListView.CHOICE_MODE_MULTIPLE</code>
1. resource 指定用 <code>simple_list_item_multiple_choice</code>

<div class="img-row">{% asset_img multiple-choice.png 多選列表 %}</div>

```java
ListAdapter mAdapter =
        new ArrayAdapter<Struct>(this,
                android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1,
                mItems);
mList.setAdapter(mAdapter);

mList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        AbsListView list = (AbsListView)adapterView;
        Adapter adapter = list.getAdapter();
        SparseBooleanArray array = list.getCheckedItemPositions();
        List<Struct> checked = new ArrayList<>(list.getCheckedItemCount());
        for (int i = 0; i < array.size(); i++) {
            int key = array.keyAt(i);
            if (array.get(key)) {
                checked.add((Struct)adapter.getItem(key));
            }
        }
    }
});

```

SparceBooleanArray 就是存放很多 Boolean 數值的資料結構，但它不是緊密的 Array，也就是資料結構課本裡面提過的 Sparce matrix (稀疏矩陣)的概念。一個 ListView 可能有 100 行以上的元件，使用者真正選擇的只有少數幾個，用完整的 Array 去存太浪費了，如果行數爆增到一萬行以上怎麼辦？(雖說不應該有 UI 愚蠢到提供一萬行以上 ListView 給 user 才對)

SparceBooleanArray 比較像是一個 Map，給它一個 int 讓它回答 true or false。當使用者在 ListView 上面按了第 1, 3, 5, 9, 20 行之後，ListView 就會在 SparceBooleanArray 裡面放入 5 個元素，對應到剛剛按過那幾行，取出來的 boolean value 則對應著該行是否被 checked。

如果使用者接下來又勾選了第 7 行，接著又把第 7 行取消呢？SparceBooleanArray 的元素會變成 6 個，而 key 為 7 的元素內容為 false。

換句話說，使用者按過幾個 list item，SparceBooleanArray 就會有幾個元素，元素的值不一定都為 true。所以上面的 Clicke Handler 做的事情就是

1. 根據 SparceBooleanArray 的長度跑一遍，依序取出每個元素
1. 拿出每個元素的 key，依前面例子就會是 1, 3, 5, 9, 20, 7....不等
1. <code>app.get(key)</code> 再問 **key 為 1/3/5..的狀態是 true or false？**
1. true 就是被使用者點選的，加進去 List 裡面

# Customize Multiple Choice ListView

世界就是這樣運轉，你總是會遇上客製化選單的需求，如果我們想要用自己手工打造的 View 該怎麼做？假設我們的手工品叫做 MyListItem，我們要做這幾件事情

1. 幫 MyListItem 定義出 layout xml
1. 實作 MyListItem.java，重點是要 <code>implements Checkable</code>
1. 把 Resource 指到該 layout

<div class="img-row">{% asset_img custom-checkable.png 自製可選列表 %}</div>

這個醜到爆炸的東西，只是為了要展示客製化的 list item。它是一個 Multiple choices ListView，預設都是 **no**，按下去就會變成 **ok**，再按一下又會變回 **no**。

```xml
<zeroxlab.org.testlistview.MyListItem
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@android:id/text1"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="10dip"
    android:layout_marginLeft="10dip"
    android:layout_marginRight="10dip"
    android:textAppearance="?android:attr/textAppearanceListItem">
</zeroxlab.org.testlistview.MyListItem>
```

```java
package zeroxlab.org.testlistview;

public class MyListItem extends TextView implements Checkable {

    boolean mChecked = false;
    public MyListItem(Context context) {
        super(context);
    }
    public MyListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean c) {
        mChecked = c;
        if (mChecked) {
            setText("ok");
        } else {
            setText("no");
        }
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }
}
```

```java
ListAdapter mAdapter =
        new ArrayAdapter<Struct>(this,
                R.layout.sample_my_list_item,
                android.R.id.text1,
                mItems);
```

繼承了 TextView 之後，再實作 Checkable 的介面，依據情況用 setText 更動文字。如果你繼承的不是 TextView 而是更複雜的 ViewGroup Layout 呢？要小心 click event 被裡面的 child view 吃掉，使得自己的 view 完全不知道應該要更新狀態了。如果你有跑去看 CheckedTextView 的 source code，就會發現它並不是一個 ViewGroup，而且是自己手動把選勾畫出來的，就是要解決 event dispatch 這檔事(當然，也許也考慮了 ListView fling performance issue)

如果只是想單純地替換選勾符號的外觀，CheckedTextView 的 layout xml 裡面就能夠透過 <code>android:checkMark="@drawable/my_png"</code> 來更換，這樣其實就足夠應付多數情況了，沒事還是不要自己作手工藝品。

# Empty View

寫程式經常要處理一些邊界條件，好比 array 是空的怎麼辦？在 ListView 裡面也會有類似的情況，當 List 內容光溜溜的時候，ListView 也會光溜溜。偶爾會看見的寫法就是多放一個隱藏的 View 在旁邊，檢查 List 內容來決定顯示與否，當作是空無一物的 List 的 fallback。

雖然 Android Framework 好像沒有很好用，但是這麼常見的需求還是有提供一個簡單的 API <code>AdapterView.setEmptyView()</code>，只要把這個 View 丟進去 Layout 裡面就好。Empty View 與 ListView 之間的顯示與否，就交由 Framework 去幫我們做

```xml
<TextView
    android:id="@+id/empty_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:text="Oooops, I am EMPTY!"/>
<ListView
    android:id="@+id/listview"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    ....

```

```java
ListView mList = (ListView) findViewById(R.id.listView);
View empty = findViewById(R.id.empty_view);
mList.setEmptyView(empty);
```

# ListView 內部怎麼運作


到前面為止大概展示了 ListView 常見的實作需求，好奇寶寶可能會想多知道一點。<del>(每次腦中都會自然飄出「請長官再多說一點勉勵的話」這句)</del>

前面提到了對 ListView 設定了 Single/Multiple 的模式之後，就能夠選用對應的 Layout resouce，並且詢問哪些 items 被選擇。我們不免想要知道裡面是怎麼運作的，這樣在自製 View 的時候比較有信心不會出錯。

雖說是 ListView，但大多數的實作都放在 AbsListView.java 裡面

設定 Mode 的時候會把數值儲存在內部的一個 <code>int mChoiceMode</code> 裡面，向 ListView 詢問哪些 item 被選擇的時候，就會檢查這個數值，看看當前的 ListView 處在何種模式之下，發出錯誤的問法就會被抱怨。對一個 Single mode 的 ListView 詢問單數的 <code>getCheckedItemPosition()</code> 會回傳正確的數值，詢問複數的 <code>getCheckedItemPositions()</code> 就會回傳 SparceBooleanArray(但裡面理論上只會有一個值)；Multiple mode ListView 的行為也是類似，只是無法取得單數的 position。

內部裡面都是把 Checked-Item-Position 儲存在 <code>SparceBooleanArray mCheckStates</code> 裡面，也就是 <code>getCheckedItemPositions()</code> 回傳的那個處理起來稍稍有點麻煩的資料結構。

既然現在知道 AbsListView 會幫我們維護 <code>getCheckedItemPosition</code> 的資料，我們不用管它。下一步就是想知道，鄉民自己手工打造的 View 要在什麼情況下更新自己的外觀？畢竟只有自己的程式碼才會知道該怎麼畫自己的外觀。

更新外觀的事件，會發生在兩條路徑上，一個是比較物理層面的， User 在介面上點擊了 ListView 觸發事件或是執行 <code>performItemClick(...)</code>，另一個是比較精神層面的在程式裡面用 <code>setItemChecked()</code>

第一條路徑，當 User 按了一下 ListView 會觸發在 AbsListView.java 裡面的 <code>onKeyUp()</code>，裡面又呼叫 <code>performItemClick()</code>，在這個 method 裡面經過計算之後去更新 mCheckStates 的數值，接著呼叫 <code>updateOnScreenCheckedViews()</code>，這裡面就會呼叫到由我們自己實作的 <code>CheckedTextView.setChecked()</code>

第二條路徑在執行 <code>setItemChecked()</code> 之後就更新了 mCheckStates，之後執行 requestLayout 要求畫面重繪。重繪的過程會在 Android 複雜的 Framework 繞一大圈，在 main thread 上一路 call 到 ListView 的 <code>LayoutChildren</code>，ListView 接著呼叫到由我們自己實作的 <code>CheckedTextView.setChecked()</code>

在 debugger 裡面看到的 Callstack 簡略如下

1. AbsListView.onLayout
1. ListView.LayoutChildren
1. ListView.fillSpecific
1. ListView.makeAndAddView
1. ListView.setupChild
1. CheckedTextView.setChecked

<div class="img-row">{% asset_img callstack.png setChecked call stack %}</div>

不論這兩條路徑任何一條，都會執行到像這樣一段程式碼，只是一個在 AbsListView，一個在 ListView。

```java
if (child instanceof Checkable) {
    ((Checkable) child).setChecked(mCheckStates.get(position));
}
```

也就是說，當 AbsListView 所維護的 mCheckStates 有更動之後，都會呼叫相對應的 List item 的 <code>setChecked()</code>，讓 List item 的實作(也就是我們寫的 code)能夠做出反應。就 ListView 的通常情況，就是叫 checkedTextView 決定要不要顯示選勾。而自己打造的手工品，一定要 <code>implements Checkable</code>，不然就會被忽略了

講了一大圈，其實結論也是簡單到大家都很清楚了：**客製化 ListView 的外觀，只要實作 List Item 就好，維護被點選項目這件事情交給 ListView 它自己(實際上是 AbsListView)。自己實作的 ListItem implements Checkable 則要記得在 <code>setChecked()</code> 裡面對最新的狀態做出反應**


