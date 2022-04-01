title: ViewPager 與 FragmentStatePagerAdapter
s: viewpager
date: 2017-09-02 22:24:34
tags:
    - android
categories: geek
---

在 Android 上面想要做兩三個 Tabs，並且希望透過左右滑動切換 Tab 的時候，通常就會用到 [ViewPager](https://developer.android.com/reference/android/support/v4/view/ViewPager.html)。若希望每個 Tab 都是一個獨立的 Fragment，我們能使用 [FragmentStatePagerAdapter](https://developer.android.com/reference/android/support/v4/app/FragmentStatePagerAdapter.html)。只是這個 Adapter 的使用方法跟 ListView 或 RecyclerView 的 Adapter 有點不一樣，一不小心就會踩到洞。

測試的方法也不難，只要進到系統設定裡面的開發者選項，把 **Don't keep activties** 打開便能測試。就我自己的經驗，在 MainActivity 用了 ViewPager 顯示幾個 Fragments，打開一個新的 Activity 觸發系統將 MainActivity 砍掉，接著回到 MainActivity 的時候就能發現，所有的 View 都是 null。

<!-- more -->

原本我自己的寫法，在 MainActivity.onCreate 裡面會重新產生 Adapter 並 `findViewById` 把所有的 View 都連結一遍，心想所有的東西都重新產生一遍，也沒有 static reference，應該不用擔心 leak 的問題。實際上卻在 **FragmentStatePagerAdapter** 的使用上踩到了地雷。

我繼承的 Adapter，保留了一份 List，指向系統正在使用的 Fragments，理論上應該要這樣。但實際上，`FragmentStatePagerAdapter` 內部也有維護一份 List，並且會對 `FragmentManager` 回復被砍掉的 Fragment。

當程式從其他 Activity 返回到 MainActivity 時，雖然我自己的程式邏輯順利地重新產生幾個 Fragments，但是 `FragmentStatePagerAdapter` 在之前 MainActivity 被移除之時，會被 `ViewPager` 呼叫 `saveState` 把狀態儲存下來，在 Activity 回來的時候 `ViewPager` 又會將狀態回復。此時 `FragmentStatePagerAdapter` 便把先前的幾個 Fragments 給重新產生，放到畫面上。

我在畫面上看到的 Fragment，並不是我自以為，在 MainActivity 裡面重新產生的

所以麻煩之處就在於，`FragmentStatePagerAdapter` 會很好心地幫你跟 `FragmentManager` 打交道，回復被砍掉的 Fragments。但實務上，我們經常需要取用正在顯示的 Fragment，很不巧 `FragmentStatePagerAdapter` 並沒有提供介面讓我們存取它內部維護的 Fragments List。

## 不一樣的 Adapter.getItem

若我們要自己維護一份 List，首先便要記得 `FragmentStatePagerAdapter.getItem` 的行為與平常的 ListView 的 `getItem()` 不一樣。

以前寫 ListView/RecyclerView 的 Adapter，經常就是內部產生一個 List，`getItem()` 的時候就翻找 List，找到就把東西丟回去 - 被當成 query 的 method 來使用。

但是 `FragmentStatePagerAdapter.getItem` 的行為更像是 **createItem**，用來產生一個新的 Fragment，口語化的講法就是 ViewPager 對 Adapter 說：「喂，第 N 頁現在沒有東西，產生一個 Fragment 讓我塞在那一頁吧」。`getItem` 這個方法定義在 `FragmentStatePagerAdapter` 而非 `PagerAdapter`，換句話說 `getItem` 的實作會被 `FragmentStatePagerAdapter` 拿去用，而 `PagerAdapter` 或 `ViewPager` 根本不會用到。

那 `FragmentStatePagerAdapter` 什麼時候會用到呢？在 `instantiateItem` 裡面，也就是要產生一個新的 Fragment instance。所以 `getItem` 在這裡不是 query-like method。

## 實作

理解差異之後，要實作就簡單多了。首先 `getItem` 就是用來產生新的 fragment instance。因為我們想要存取正在畫面上正在顯示的 Fragments，所以需要自己維護一份 fragments list。因為 `FragmentStatePagerAdapter` 會幫我們跟 `FragmentManager` 打交道，所以只要針對 `instantiateItem` 這個方法回傳的 Fragment 做更動即可。

示範的程式碼如下

```java
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private final List<CharSequence> mTitles = new ArrayList<>();
    private final SparseArray<Fragment> mFragments = new SparseArray<>();

    private final static int POSITION_FRAGMENT_A = 0;
    private final static int POSITION_FRAGMENT_B = 1;
    private final static int POSITION_FRAGMENT_C = 2;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        // 定義我們想要放三個 fragments as tab，以及他們的位置
        mTitles.add(POSITION_FRAGMENT_A, "Fragment A");
        mTitles.add(POSITION_FRAGMENT_B, "Fragment B");
        mTitles.add(POSITION_FRAGMENT_C, "Fragment C");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case POSITION_FRAGMENT_A:
                return FragmentA.newInstance();
            case POSITION_FRAGMENT_B:
                return FragmentB.newInstance();
            case POSITION_FRAGMENT_C:
                return FragmentC.newInstance();
        }
        throw new RuntimeException("Unknown type");
    }

    @Override
    public int getCount() {
        // 總共的 tabs 數就是 titles 的數目
        // 不能用 mFragments，因為它可能一堆 Fragments 都還沒產生
        return mTitles.size();
    }

    @Override
    public String getPageTitle(int position) {
        return mTitles.get(position).toString();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Fragment fragment =
                (Fragment) super.instantiateItem(container, position);
        // 這是會被拿去顯示的 Fragment，放進我們維護的 List 裡面
        mFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object obj) {
        super.destroyItem(container, position, obj);
        // 可能是 Activity 被砍掉了，總之這個 Fragment 現在沒被使用了
        mFragments.remove(position);
    }

    // 現在我們可以順利存取到畫面上有哪些 Fragments 了
    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }
}
```

