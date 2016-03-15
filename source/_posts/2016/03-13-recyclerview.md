title: 使用 RecyclerView
s: recyclerview
date: 2016-03-13 09:17:26
tags:
    - java
    - geek
    - android
categories: geek
---

[RecyclerView](http://android-developers.blogspot.tw/2015/10/android-support-library-231.html) 是個在 Google I/O 2014 出現的 ViewGroup，放在 [v7 support libraries](http://developer.android.com/intl/zh-tw/tools/support-library/features.html#v7)，Android 2.1(API Level 7) 以上的版本都可以用，以現在普及率來看，幾乎是每一支手機都能用。

<div style="max-width: 800px; margin: auto;">{% asset_img 3layout.png %}</div>

RecyclerView 預設就提供了三種 [LayoutManager](http://developer.android.com/reference/android/support/v7/widget/RecyclerView.LayoutManager.html)，如上圖，由左而右依序是 ListLayoutManager, GridLayoutManager 與 StaggeredGridLayoutManager。都放在 v7 support libraries 裡面。


看起來 List 與 Grid 已經重複了，為何 Android 還要多提供這個 Widget？

<!-- more -->

我自己的猜測是：後來發現 ListView 彈性不夠。ListView 與 GridView 從 API level 1 就已經出現，聽說 Android 開發之初並未針對 Smart Phone？無論如何在 API Level 1 的年代完全無法想像手機會變成現在的光景。當初實作 ListView 應該是努力想著如何讓人們少寫一點 code，多一點已經寫好的東西在 Framework 裡面。怎知後來大家在用 ListView 的時候，反而有完全不同的需求，好比讓每個 row 可能有完全不同的呈現。既然如此，就提供一個更有彈性的 ViewGroup。當然這是我個人的想法。

既然之前寫了一篇 [ListView 的實作方法](/2015/09/26-implement-listview.html)，現在就順便寫一下 RecyclerView 的簡單教學。

RecyclerView 的用法跟 ListView 不太一樣，後者是一個 AdapterView，用法是把**資料**跟 **Layout** 給 [bind](http://developer.android.com/guide/topics/ui/binding.html) 起來，render 的時候 ListView 翻箱倒櫃找出資料，接著用指定的 Layout 把該筆資料「畫」出來，此時由 ListView 幫你處理 View 重用的細節。(重新 Create View instance 是很花資源的事情)，所以才會衍生出，想要兩個 row 的外觀長得不一樣就會不好處理的問題。

要使用 RecyclerView，先在 gradle 裡面加入 v7 support libraries。(23.2.0 是我正在使用的版本，依自己的喜好改變吧)

```groovy
dependencies {
    compile 'com.android.support:appcompat-v7:23.2.0'
    compile 'com.android.support:recyclerview-v7:23.2.0'
}
```

{% asset_img hierarchy.png %}

從繼承關係明顯得知，RecyclerView 僅是一個 ViewGroup，而非是 AdapterView。RecyclerView 也有自己的 [RecyclerView.Adapter](http://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html)，但它的 Adapter 用法不同於 ListView 的 Adapter。以下翻譯自官方文件的術語

* Adapter - 負責把 Dataset 裡面的資料，轉成 view 給 RecyclerView 顯示
* Position - 在 Adapter 裡面的 data item 的位置
* Index - 已掛上 RecyclerView 裡面的 child-view 的位置，用在 <code>getChildAt(int)</code>
* Binding - 把資料變成可視的 view 的過程
* Recycle(view) - 之前曾經被拿來顯示過的 view，現在放在 cache 裡面留待下次使用
* Dirty(view) - 需要重新自資料更新外觀的 view
* Scrap(view) - 在 Layout 過程被拿下來但是尚未完全 detached。(從原始碼來看，<code>mAttachScrap</code> 放在 class Recycler 底下。

# Basic Implementation

照上面的截圖以最陽春的 List 形式來實作一個簡單的範例。LayoutManager 負責把每個 child-view 安排在畫面中，而 Adapter 負責生出 child-view。要做的事情如下

* 類似 [ViewHolder pattern](http://developer.android.com/intl/zh-tw/training/improving-layouts/smooth-scrolling.html) 做一個 ViewHolder 來放 View
* 自己做一個 Adapter 來使用該 ViewHolder
* 把 Adapter 跟 RecyclerView 連結起來，並且使用不同的 Layout

## ViewHolder

Adapter 需要一個 ViewHolder，原本就有一個把所有基本東西都做完的抽象類別 [RecyclerView.ViewHolder](http://developer.android.com/intl/zh-tw/reference/android/support/v7/widget/RecyclerView.ViewHolder.html)，我們只要實作它的 constructor 就好

```java
class DummyViewHolder extends RecyclerView.ViewHolder {
    public DummyViewHolder(View itemView) {
        super(itemView);
    }
}
```
保存起來的 view 會放在 <code>itemView</code> 裡面

## Adapter

RecyclerView.Adapter 負責提供 child-view 給 RecyclerView 使用，同時也負責把資料跟 child-view 綁在一起。實際上並不是直接對上 View，而是對上 ViewHolder。從設計上來看就是強迫使用 ViewHolder pattern 來增加效率。此抽象類別需要實作這三個 method

* <code>getItemCount()</code> - Adapter 自己才知道如何儲存資料，所以它自己也才知道該怎麼數有多少資料
* <code>onCreateViewHolder(ViewGroup, int)</code> - 現有的 ViewHolder 不夠用，要求 Adapter 產生一個新的
* <code>onBindViewHolder(ViewHolder, int)</code> - 重用之前產生的 ViewHolder，把特定位置的資料連結上去準備顯示

原則上就是負責 1) 向上呈報數目 2) 若不夠用就產生新的 3) 需要重用就幫忙上色。

```java
class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Integer> iList; // 放在 ArrayList 裡面

    MyAdapter() {
        iList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Button view = new Button(parent.getContext());
        // ViewHolder 可以放更多複雜的 View，不過我們這邊直接用自己的 Dummy
        return new DummyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Integer size = iList.get(position);
        Button btn = (Button) holder.itemView;
        btn.setText("size: " + size);
    }

    @Override
    public int getItemCount() {
        return iList.size();
    }
}
```

## LayoutManager

這邊直接使用 LinearLayoutManager 來畫出類似 ListView 的功能。可以另外指定 GridLayouManager 或是 StaggeredGridLayoutManager 來畫出不同的外觀，非常簡單。從這邊來看，RecyclerView 也統一了 List 跟 Grid 兩種 Layout。

```java
RecyclerView list = (RecyclerView) findViewById(R.id.main_list);
MyAdapter adapter = new MyAdapter();
list.setAdapter(adapter);
list.setLayoutManager(new LinearLayoutManager(this));
//list.setLayoutManager(new GridLayoutManager(this, 2));
//list.setLayoutManager(new StaggeredGridLayoutManager(4,
//                          StaggeredGridLayoutManager.VERTICAL));
```

完整的原始檔在：{% asset_link BasicActivity.java %}

# Advance Implementation

前面提到的是最簡單 RecyclerView 實作。如果我們想要實作得複雜一點，讓 child-view 有各個不同的外觀，該如何？以前 ListView 的作法可能會是產生一個比較大的 View，根據狀態顯示不同的部份。[v17 Support Libraries](http://developer.android.com/intl/zh-tw/tools/support-library/features.html#v17-leanback) 提供了額外的 class 可供我們使用，如果你要支援的 device 在 Android 4.2 以上，那麼可以直接拿來用。

```groovy
dependencies {
    compile 'com.android.support:leanback-v17:+'
}
```

<div style="max-width: 800px; margin: auto;">{% asset_img selector.png %}</div>

ItemBridgeAdapte 把先前 Adapter 的兩樣工作 1) 儲存資料 2) 產生 ViewHolder 給分配到 **ObjectAdapter** 與 **PresenterSelector** 兩個地方。

ObjectAdapter 負責儲存資料，已經實作了 Array 與 SparseArray(稀疏矩陣) 兩種，通常已經夠用。

Presenter 負責產生與連結 ViewHolder，中間多了一層 PresenterSelector，負責根據資料的型別選妃，挑出對應的 Presenter。

以下的簡單實作範例會是

1. 先做一個簡單的 Data structure
1. 實作幾個 Presenter
1. 實作一個 PresenterSelector
1. 用 ItemBridgeAdapter 取代原來的 MyAdapter，並使用用 ArrayObjectAdapter


## Data Structure

用來存放要被 render 的資料，把它寫得蠢一點吧！

```java
public class MyData {
    public final static int TYPE_A = 1;
    public final static int TYPE_B = 2;

    public int mType;
    public String mContent;

    public MyData(int type, String content) {
        mType = type;
        mContent = content;
    }
}
```

寫成這樣，是為了要針對不同 type 的 MyData 來選用不同的 selector。ObjectAdapter 並非只能吃一種資料結構(看它的 [add(Object)](http://developer.android.com/intl/zh-tw/reference/android/support/v17/leanback/widget/ArrayObjectAdapter.html#add(java.lang.Object) 根本就直接吃 Object)，其實你可放進各種資料結構，然後直接用 [ClassPresenterSelector](http://developer.android.com/intl/zh-tw/reference/android/support/v17/leanback/widget/ClassPresenterSelector.html) 來針對型別選用 Selector。

## Presenter


先寫兩個 Presenter，先讓它們做的事情大同小異，差別在讀進的 xml resource 檔

```java
public class TypeAPresenter extends Presenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        // 從 xml 檔產生外觀
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.type_1, parent, false);

        // 把 View 放進去 ViewHolder，就像之前做的一樣
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        MyData data = (MyData) item;
        MyViewHolder myHolder = (MyViewHolder) viewHolder;
        myHolder.iText.setText(data.mContent);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        // 如果要做任何資源釋放的事情，go here
    }

    class MyViewHolder extends ViewHolder {
        public TextView iText;
        MyViewHolder(View view) {
            super(view);
            iText = (TextView view);
        }
    }
}

public class TypeBPresenter extends TypeAPresenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        // 從 xml 檔產生外觀
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.type_2, parent, false);

        return new MyViewHolder(v);
    }
}
```

很閒的話，可以多寫一個 Fallback 的 Presenter，遇到無法理解的資料就拿出來用

```java
public class FallbackPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView v = new TextView(parent.getContext());
        v.setBackgroundColor(Color.RED); // be aware of me, please!
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        TextView textView = (TextView) viewHolder.view;
        textView.setText("Invalid data: " + item.toString());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        // I am simple!
    }
}

```

## PresenterSelector

準備好了三個 Presenters，接著就是 Selector，拿資料的內容來決定該用哪個 Presenter。因為邏輯
很簡單，所以直接用 Array 來存放各個 presenters

```java
public class MyPresenterSelector extends PresenterSelector {

    /* No complex logic here, so use Array should be fastest. */
    private Presenter[] mPresenters;
    private final static int INVALID = 0;
    private final static int TYPE_A = 1;
    private final static int TYPE_B = 2;

    public TwitterPresenterSelector() {
        mPresenters = new Presenter[3];
        mPresenters[INVALID] = new FallbackPresenter();
        mPresenters[TYPE_A] = new TypeAPresenter();
        mPresenters[TYPE_B] = new TypeBPresenter();
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (!(item instanceof MyData)) {
        //
            return mPresenters[INVALID];
        }
        MyData data = (MyData) item;
        if (data.mType == MyData.TYPE_A) {
            return mPresenters[TYPE_A];
        } else {
            return mPresenters[TYPE_B];
        }
    }

    @Override
    public Presenter[] getPresenters() {
        return mPresenters;
    }
}
```

## Integration

把資料都放進 dataAdapter 裡面，讓它使用 MyPresenterSelector 來選擇該怎麼呈現資料。對於 RecyclerView 來說，它從來都不知道底層發生了什麼事。

```java
ArrayObjectAdapter dataAdapter = new ArrayObjectAdapter(new MyPresenterSelector());

RecyclerView list = (RecyclerView)findViewById(R.id.list);
list.setLayoutManager(new LinearLayoutManager(this));
list.setAdapter(new ItemBridgeAdapter(dataAdapter));

// 加入資料
dataAdapter.add(new MyData(MyData.TYPE_A, "Hello"));
dataAdapter.add(new MyData(MyData.TYPE_B, "World"));
dataAdapter.add("I will use fallback");
```
