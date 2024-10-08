title: 金庸小說年代表
date: 2024-08-16 22:30:20
tags:
---

一直以來都不是很能分清楚，金庸小說的年代順序，於是找到別人整理好的表

<!-- more -->

玩上古卷軸之類的遊戲，或是冰與火之歌這種背景複雜的小說，經常有人會整理出哪一年發生了什麼事。

我很喜歡看這類的資料，想起金庸小說的年代順序，才找到這篇 [PTT: 金庸小說編年史、年表、遊戲](https://www.ptt.cc/bbs/JinYong/M.1723143529.A.D3A.html)

把 Excel 檔匯出之後，套用網路上隨便撿來的 css/html，再稍微修改一下。

原本想找能夠直接畫出 timeline 並且匯出 svg 檔案的程式，但是意外地稀少，而且鮮少有直排格式的 timeline，只好將就點直接寫成 html 檔。

而且寫完才覺得，橫排的 timeline 看起來比較有感覺，不知道為什麼。


本資料由「金庸茶館」的「多明戈」整理，且由 PTT 「nbmi」彙整成 excel 表格。避免原文在 PTT 消失，這邊附上 nbmi 網友的[原文](https://www.ptt.cc/bbs/JinYong/M.1723143529.A.D3A.html)


作者nbmi (到處亂飄的葉子)
看板JinYong
標題[討教]金庸小說編年史、年表、遊戲
時間Fri Aug  9 02:58:37 2024

請教一下大家，金庸武俠有沒有類似太閣立志傳5(或說類似三國志系列)
以遊戲時間或行動(進行任何活動)連結到時間軸
再以時間軸連結發生的事件的遊戲

可以列出幾個主要進入遊戲的時間點，甚至可以更自由地選擇任何年份開始遊戲

想舉太閣5為例的原因，除了時間軸以及可以扮演任何原故事內角色也可自創角色之外，
還有存在不同大小職業別，可以達成不同結局或目標的設定。
雖然金庸相關的遊戲都只以武俠為主軸，但其世界觀來說也是各行各業都有。
或許也是可以做出類似的設計。

但首先想到的就是年表
因此好奇找了一下網路上

找到金庸茶館2011~2013有位多明戈整理了編年史
用這位網友的資料先彙整成一份excel

目前還有 連城訣、白馬嘯西風、鴛鴦刀 還沒有列進去

金庸小說編年史、年表
https://bit.ly/JY_Chronology

金庸茶館
http://jinyong.ylib.com/snowtalk/
取自 金庸茶館(2011~2013)
http://jinyong.ylib.com.tw/snowtalk/list.asp?ch=genuine&DaysPrune=2011
http://jinyong.ylib.com.tw/snowtalk/list.asp?ch=genuine&DaysPrune=2012
http://jinyong.ylib.com.tw/snowtalk/list.asp?ch=genuine&DaysPrune=2013

出新招者：多明戈
新修版金庸編年史之1 － 越女劍
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67164&ch=genuine
新修版金庸編年史之2 － 天龍八部
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67165&ch=genuine
新修版金庸編年史之4 － 射鵰英雄傳
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67200&ch=genuine
新修版金庸編年史之3 － 神鵰俠侶
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67192&ch=genuine
新修版金庸編年史之五——《倚天屠龍記》
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67215&ch=genuine
新修版金庸編年史之6 － ?
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67221&ch=genuine
新修版金庸編年史之7 － 笑、俠
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67248&ch=genuine
新修版金庸編年史之8 － 碧血劍
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67252&ch=genuine
新修版金庸編年史之9 － 鹿鼎記
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67253&ch=genuine
新修版金庸編年史之10 － 書／飛／雪
http://jinyong.ylib.com.tw/snowtalk/show.asp?no=67907&ch=genuine



<style>
    .timeline {
        position: relative;
        margin: 0 auto;
        width: 800px;
        font-size: 14px !important;
    }

    .point {
        min-width: 20px;
        height: 20px;
        background-color: #333333;
        border-radius: 100%;
        z-index: 2;
        position: relative;
        left: 1px;
    }

    h3 {
        color: black !important;
        margin-top: 1px;
        font-size: 12px !important;
    }

    .timeline ul li {
        margin-bottom: 0px;
        list-style-type: none;
        display: flex;
        flex-direction: row;
        align-items: center;
    }


    .timeline ul li.v01 .content h3,
    .timeline ul li.v01 .date h4 {
        background-color: lightpink;
    }

    .timeline ul li.v02 .content h3,
    .timeline ul li.v02 .date h4 {
        background-color: peachpuff;
    }

    .timeline ul li.v03 .content h3,
    .timeline ul li.v03 .date h4 {
        background-color: lavender;
    }

    .timeline ul li.v04 .content h3,
    .timeline ul li.v04 .date h4 {
        background-color: lightsteelblue;
    }

    .timeline ul li.v05 .content h3,
    .timeline ul li.v05 .date h4 {
        background-color: silver;
    }

    .timeline ul li.v06 .content h3,
    .timeline ul li.v06 .date h4 {
        background-color: wheat;
    }

    .timeline ul li.v07 .content h3,
    .timeline ul li.v07 .date h4 {
        background-color: thistle;
    }

    .timeline ul li.v08 .content h3,
    .timeline ul li.v08 .date h4 {
        background-color: mistyrose;
    }

    .timeline ul li.v09 .content h3,
    .timeline ul li.v09 .date h4 {
        background-color: khaki;
    }

    .timeline ul li.v10 .content h3,
    .timeline ul li.v10 .date h4 {
        background-color: lightcyan;
    }

    .timeline ul li.v11 .content h3,
    .timeline ul li.v11 .date h4 {
        background-color: beige;
    }

    .timeline ul li.v12 .content h3,
    .timeline ul li.v12 .date h4 {
        background-color: honeydew;
    }

    .timeline ul li.v13 .content h3,
    .timeline ul li.v13 .date h4 {
        background-color: whitesmoke;
    }

    .timeline ul li.v14 .content h3,
    .timeline ul li.v14 .date h4 {
        background-color: seashell;
    }

    .timeline ul li .content {
        width: 50%;
        padding: 0 20px;
    }

    .timeline ul li.v01 .content,
    .timeline ul li.v03 .content,
    .timeline ul li.v05 .content,
    .timeline ul li.v07 .content,
    .timeline ul li.v09 .content,
    .timeline ul li.v11 .content,
    .timeline ul li.v13 .content {
        padding-left: 0;
    }

    .timeline ul li.v01 .date,
    .timeline ul li.v03 .date,
    .timeline ul li.v05 .date,
    .timeline ul li.v07 .date,
    .timeline ul li.v09 .date,
    .timeline ul li.v11 .date,
    .timeline ul li.v13 .date {
        padding-right: 0;
    }

    .timeline ul li.v02 .content,
    .timeline ul li.v04 .content,
    .timeline ul li.v06 .content,
    .timeline ul li.v08 .content,
    .timeline ul li.v10 .content,
    .timeline ul li.v12 .content,
    .timeline ul li.v14 .content {
        padding-right: 0;
    }

    .timeline ul li.v02 .date,
    .timeline ul li.v04 .date,
    .timeline ul li.v06 .date,
    .timeline ul li.v08 .date,
    .timeline ul li.v10 .date,
    .timeline ul li.v12 .date,
    .timeline ul li.v14 .date {
        padding-left: 0;
    }

    .timeline ul li.v02 .date h4,
    .timeline ul li.v04 .date h4,
    .timeline ul li.v06 .date h4,
    .timeline ul li.v08 .date h4,
    .timeline ul li.v10 .date h4,
    .timeline ul li.v12 .date h4,
    .timeline ul li.v14 .date h4 {
        float: right;
    }

    .timeline ul li.v02,
    .timeline ul li.v04,
    .timeline ul li.v06,
    .timeline ul li.v08,
    .timeline ul li.v10,
    .timeline ul li.v12,
    .timeline ul li.v14 {
        flex-direction: row-reverse;
    }

    .timeline ul li .date {
        width: 50%;
        padding: 0 20px;
        font-weight: normal;
    }
    .timeline ul li .date h4 {
        background-color: #e1ccec;
        width: 100px;
        text-align: center;
        padding: 5px 10px;
        border-radius: 10px;
    }
    .timeline ul li .content h3 {
        padding: 2px 20px;
        background-color: #be9fe1;
        margin-bottom: 0;
        text-align: center;
        border-top-left-radius: 10px;
        border-top-right-radius: 10px;
    }
    .timeline ul li .content p {
        padding: 10px 20px;
        margin-top: 0;
        margin-bottom: 1px;
        border-bottom-left-radius: 10px;
        border-bottom-right-radius: 10px;
        border: solid 1px #EEEEEE;
    }

    .timeline::before {
        content: "";
        position: absolute;
        height: 100%;
        width: 3px;
        left: 420px;
        background-color: #333333;
    }

</style>
<div class="timeline">
    <ul id="container">
        <li class="v01" style="margin-top: 0px;">
            <div class="content">
                <h3>越女劍</h3>
                <p>前476年 故事開始, 范蠡遇阿青, 並邀其傳授士兵劍法</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>前476</h4></div>
        </li>
        <li class="v01" style="margin-top: 15px;">
            <div class="content">
                <h3>越女劍</h3>
                <p>前473年 越滅吳, 故事結束</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>前473</h4></div>
        </li>
        <li class="v02" style="margin-top:300px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>997年 天山童姥出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>997</h4></div>
        </li>
        <li class="v02" style="margin-top:0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1000年 無崖子出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1000</h4></div>
        </li>
        <li class="v02" style="margin-top: 50px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1005年 徐沖霄及李秋水出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1005</h4></div>
        </li>
        <li class="v02" style="margin-top: 180px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1023年 天山童姥練功走火入魔</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1023</h4></div>
        </li>
        <li class="v02" style="margin-top: 150px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1038年 薜慕華出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1038</h4></div>
        </li>
        <li class="v02" style="margin-top: 80px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1046年 黃眉僧遇慕容博母子</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1046</h4></div>
        </li>
        <li class="v02" style="margin-top: 40px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1050年 葉二娘出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1050</h4></div>
        </li>
        <li class="v02" style="margin-top: 30px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1053年 耶律洪基出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1053</h4></div>
        </li>
        <li class="v02" style="margin-top: 30px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1056年 甘寶寶出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1056</h4></div>
        </li>
        <li class="v02" style="margin-top: 50px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1061年 蕭峰出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1061</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1062年 9月 雁門關之役</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1062</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1063年 丁春秋暗算無崖子, 慕容博入寺盜經</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1063</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1064年 慕容復出生, 慕容博遇蕭遠山</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1064</h4></div>
        </li>
        <li class="v02" style="margin-top: 20px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1066年 宗贊王子出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1066</h4></div>
        </li>
        <li class="v02" style="margin-top: 30px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1069年 虛竹出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1069</h4></div>
        </li>
        <li class="v02" style="margin-top: 30px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>
                    1072年 2月 段延慶遭圍攻重傷<br />
                    1072年11月23日 段譽出生
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1072</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1073年 游坦之及木婉清出生, 崔百泉遇慕容博夫婦</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1073</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1074年 王語嫣出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1074</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1075年12月 5日 鐘靈出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1075</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1076年 梅劍、蘭劍、菊劍、竹劍及李清露出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1076</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1077年 蕭峰拜汪劍通為師</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1077</h4></div>
        </li>
        <li class="v02" style="margin-top:60px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>
                    1083年 5月 7日 蕭峰接任丐幫幫主<br />
                    1083年 神龍幫歸順靈鷲宮
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1083</h4></div>
        </li>
        <li class="v02" style="margin-top: 50px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1088年 包不靚出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1088</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1089年 洛陽城百花會</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1089</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>
                    1090年 5月 5日 康敏拆閱汪劍通遺書<br />
                    1090年 8月15日 馬大元被殺<br />
                    1090年11月 司馬衛被殺
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1090</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>
                    1091年 1月 故事開始, 段譽上無量山<br />
                    1091年 1月28日 玄悲被殺<br />
                    1091年 1月29日 段譽與木婉清被擄入萬劫谷<br />
                    1091年 2月 3日 過彥之及慧觀慧真到訪<br />
                    1091年 4月 杏子林丐幫大會<br />
                    1091年 4月 喬三槐夫婦及玄苦被殺<br />
                    1091年 7月 4日 徐沖霄被殺<br />
                    1091年 7月 7日 蕭峰阿朱抵衛輝, 趙錢孫, 譚公及譚婆被殺
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1091</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1092年 蕭峰與耶律洪基結拜</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1092</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>
                    1093年 3月 阿紫得冰蠶<br />
                    1093年 6月15日 擂鼓山棋會, 虛竹任逍遙派掌門<br />
                    1093年11月10日 少室山英雄大會
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1093</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>
                    1094年 3月 7日 段譽等人落入枯井<br />
                    1094年 3月 8日 銀川公主招親<br />
                    1094年 5月 段正淳及眾情婦身亡<br />
                    1094年冬 蕭峰下獄<br />
                    1094年 蕭峰營救戰
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1094</h4></div>
        </li>
        <li class="v02" style="margin-top: 0px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>109?年 再遇慕容復, 故事結束</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>109?</h4></div>
        </li>
        <li class="v02" style="margin-top: 300px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1134年 段譽出家退位</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1134</h4></div>
        </li>
        <li class="v02" style="margin-top: 250px;">
            <div class="content">
                <h3>天龍八部</h3>
                <p>1159年 段正興出家退位(一燈繼位)</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1159</h4></div>
        </li>
        <li class="v03" style="margin-top: 110px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1170年 曲靈風出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1170</h4></div>
        </li>
        <li class="v03" style="margin-top: 130px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1183年 陳玄風出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1183</h4></div>
        </li>
        <li class="v03" style="margin-top: 20px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1185年 梅超風出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1185</h4></div>
        </li>
        <li class="v03" style="margin-top: 20px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1187年 陸乘風出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1187</h4></div>
        </li>
        <li class="v03" style="margin-top: 90px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1196年 梅超風入蔣家</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1196</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1197年 梅超風拜師</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1197</h4></div>
        </li>
        <li class="v03" style="margin-top: 30px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1200年 第一次華山論劍, 同年曲靈風向梅超風提及華山論劍結果</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1200</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1201年 郭楊移居牛家村, 王重陽訪大理</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1201</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1202年 拖雷出生, 瑛姑產子</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1202</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1203年 曲靈風被逐, 移居牛家村</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1203</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>
                    1204年 尹志平出生<br />
                    1204年 8月 故事開始, 張十五於牛家村說書
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1204</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>
                    1205年 曲靈風死亡<br />
                    1205年 丘處機路過牛家村<br />
                    1205年 段天德率兵襲牛家村<br />
                    1205年 穆念慈出生<br />
                    1205年 3月24日 丘處機與七怪訂賭約<br />
                    1205年 8月15日 陳梅盜經<br />
                    1205年10月 郭靖出生<br />
                    1205年11月 楊康出生
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1205</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1206年 包惜弱下嫁完顏雄烈</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1206</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1207年 黃蓉及華箏出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1207</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1208年 周伯通被囚</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1208</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1209年 黑風雙煞初練九陰白骨爪, 柯辟邪被殺</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1209</h4></div>
        </li>
        <li class="v03" style="margin-top: 20px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>
                    1211年 哲別歸降、郭靖投軍<br />
                    1211年 七俠尋獲郭靖
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1211</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1212年 丘處機尋獲楊康</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1212</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1213年 楊康拜師</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1213</h4></div>
        </li>
        <li class="v03" style="margin-top: 50px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1218年 穆念慈遇洪七公</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1218</h4></div>
        </li>
        <li class="v04" style="margin-top: 20px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1220年 小龍女出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1220</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1221年 尹志平到訪, 同年馬鈺授郭靖輕功及內功</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1221</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>
                    1223年 1月 黃蓉逃出桃花島<br />
                    1223年 成吉思汗統一蒙古、郭靖南下中原<br />
                    1223年 2月 比武招親<br />
                    1223年 4月 遇洪七公<br />
                    1223年 5月30日 太湖群盜擒楊康<br />
                    1223年 6月 5日 郭靖楊康結義<br />
                    1223年 6月 8日 洪七公收郭黃為徒<br />
                    1223年 6月 9日 遇拖雷哲別一行<br />
                    1223年 6月28日 抵牛家村曲三酒館<br />
                    1223年 6月29日 群豪大鬧禁宮<br />
                    1223年 7月 2日 陸冠英程遙迦成親<br />
                    1223年 7月 3日 歐陽克被殺<br />
                    1223年 7月 4日 七子抵牛家村<br />
                    1223年 7月 7日 譚處端及梅超風被殺<br />
                    1223年 7月15日 君山丐幫大會<br />
                    1223年 7月 裘千丈身亡<br />
                    1223年 8月 四怪被殺<br />
                    1223年 8月13日 郭黃抵桃花島, 南希仁毒發身亡<br />
                    1223年 8月14日 抵醉仙樓<br />
                    1223年 8月15日 醉仙樓之會<br />
                    1223年 8月16日 鐵楊廟楊康身亡<br />
                    1223年 8月18日 郭靖重遇柯鎮惡
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1223</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>1224年 楊過出生, 郭靖隨軍攻花剌子模</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1224</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1224年 楊過、完顏萍及公孫綠萼出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1224</h4></div>
        </li>
        <li class="v03" style="margin-top: 0px;">
            <div class="content">
                <h3>射鵰英雄傳</h3>
                <p>
                    1225年 郭靖離開蒙古<br />
                    1225年 二次華山論劍<br />
                    1225年 青州之戰, 故事結束
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1225</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1225年 武敦儒出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1225</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1226年 武修文出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1226</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1227年 陸展元何沅君成親</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1227</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1228年 程英、陸無雙及郭芙出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1228</h4></div>
        </li>
        <li class="v04" style="margin-top: 60px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1234年 陸展元及何沅君逝世</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1234</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1235年 穆念慈病死</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1235</h4></div>
        </li>
        <li class="v04" style="margin-top: 20px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1237年 8月 故事開始, 陸家滅門</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1237</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>
                    1238年 投師終南<br />
                    1238年12月 反出全真教, 入古墓
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1238</h4></div>
        </li>
        <li class="v04" style="margin-top: 30px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1241年 小龍女練功受傷, 李莫愁師徒闖墓, 斷龍石下</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1241</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>1242年 小龍女失身, 楊過闖江湖遇陸無雙、完顏萍等人</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1242</h4></div>
        </li>
        <li class="v04" style="margin-top: 0px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>
                    1243年 8月 大勝關英雄大會<br />
                    1243年 9月12日 楊過國師一行入絕情谷<br />
                    1243年 9月13日 墮鱷魚潭、遇裘千尺、出洞戰公孫止<br />
                    1243年 9月14日 離開絕情谷<br />
                    1243年 9月24日 郭襄及郭破虜出生<br />
                    1243年12月 1日 楊龍遇一燈慈恩<br />
                    1243年12月 6日 眾人入絕情谷求丹<br />
                    1243年12月 7日 小龍女留字跳崖
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1243</h4></div>
        </li>
        <li class="v05" style="margin-top: 30px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1246年 4月 9日 張三豐出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1246</h4></div>
        </li>
        <li class="v04" style="margin-top:70px;">
            <div class="content">
                <h3>神鵰俠侶</h3>
                <p>
                    1259年 3月 風陸渡郭襄隨大頭鬼會楊過<br />
                    1259年 9月13日 魯有腳被殺<br />
                    1259年 9月15日 襄陽城英雄大會開始<br />
                    1259年 9月24日 丐幫重選幫主, 郭襄16歲生辰<br />
                    1259年12月 3日 楊過抵絕情谷
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1259</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1262年 瀟湘子及尹克西互鬥而死</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1262</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1263年 故事開始, 小東邪大鬧少室山</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1263</h4></div>
        </li>
        <li class="v05" style="margin-top: 100px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1273年 襄陽城破</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1273</h4></div>
        </li>
        <li class="v05" style="margin-top: 100px;">
            <div class="content" style="margin-top:50px;">
                <h3>倚天屠龍記</h3>
                <p>1283年 郭襄出家、峨眉創派</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1283</h4></div>
        </li>
        <li class="v05" style="margin-top: 120px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1295年 謝遜出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1295</h4></div>
        </li>
        <li class="v05" style="margin-top: 220px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1317年 殷梨亭出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1317</h4></div>
        </li>
        <li class="v05" style="margin-top: 30px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1320年 7月15日 謝無忌出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1320</h4></div>
        </li>
        <li class="v05" style="margin-top: 20px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1322年 陽頂天練功走火死亡</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1322</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1323年 謝遜一家被殺</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1323</h4></div>
        </li>
        <li class="v05" style="margin-top: 30px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1326年 謝遜第一次尋成崑報仇</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1326</h4></div>
        </li>
        <li class="v05" style="margin-top: 20px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1328年 謝遜第二次尋成崑報仇</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1328</h4></div>
        </li>
        <li class="v05" style="margin-top: 80px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>
                    1336年 3月24日 俞岱巖中天鷹教暗算<br />
                    1336年 3月28日 殷素素托鏢<br />
                    1336年 4月 9日 張三丰九十歲壽辰, 俞岱巖殘廢<br />
                    1336年 4月30日 龍門鏢局滅門
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1336</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1337年 張無忌出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1337</h4></div>
        </li>
        <li class="v05" style="margin-top: 20px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1339年 殷離出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1339</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1340年 楊不悔出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1340</h4></div>
        </li>
        <li class="v05" style="margin-top: 50px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1345年11月 張翠山一家回歸中土</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1345</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1346年 4月 9日 張三丰百歲壽辰, 張翠山夫婦自殺</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1346</h4></div>
        </li>
        <li class="v05" style="margin-top: 20px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1348年 往蝴蝶谷求醫</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1348</h4></div>
        </li>
        <li class="v05" style="margin-top: 20px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1350年 胡青牛夫婦被殺, 張無忌攜楊不悔尋父, 同年入連環莊</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1350</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1351年 2月 入崑崙仙境</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1351</h4></div>
        </li>
        <li class="v05" style="margin-top: 50px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>1356年 離開崑崙仙境</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1356</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>
                    1357年 1月 六大派圍攻光明頂<br />
                    1357年 3月 趙敏率眾攻武當<br />
                    1357年 8月15日 蝴蝶谷明教大會<br />
                    1357年10月 6日 史火龍被殺<br />
                    1357年12月 丐幫盧龍聚會
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1357</h4></div>
        </li>
        <li class="v05" style="margin-top: 0px;">
            <div class="content">
                <h3>倚天屠龍記</h3>
                <p>
                    1358年 1月 2日 大都大遊皇城<br />
                    1358年 6月15日 張無忌周芷若原訂成親之日<br />
                    1358年 8月 張無忌首次與渡厄三僧交手<br />
                    1358年 9月 9日 屠獅英雄會(首日)<br />
                    1358年 9月10日 屠獅英雄會(次日), 謝遜師徒決戰<br />
                    1358年 故事結束
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1358</h4></div>
        </li>
        <li class="v06" style="margin-top: 400px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1436年 日月教襲武當</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1436</h4></div>
        </li>
        <li class="v06" style="margin-top:300px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1482年 林夫人出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1482</h4></div>
        </li>
        <li class="v06" style="margin-top: 140px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1496年 華山派劍宗氣宗玉女峰比劍</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1496</h4></div>
        </li>
        <li class="v06" style="margin-top: 20px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1498年 令狐冲出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1498</h4></div>
        </li>
        <li class="v06" style="margin-top: 40px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1502年 林平之出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1502</h4></div>
        </li>
        <li class="v06" style="margin-top: 0px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1503年 岳靈冲及任盈盈出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1503</h4></div>
        </li>
        <li class="v06" style="margin-top: 30px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1506年 劉芹出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1506</h4></div>
        </li>
        <li class="v06" style="margin-top: 40px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1510年 東方不敗奪日月教主位</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1510</h4></div>
        </li>
        <li class="v06" style="margin-top: 100px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1520年 盈盈移居洛陽</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1520</h4></div>
        </li>
        <li class="v06" style="margin-top: 0px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>
                    1521年 (春) 故事開始, 福威鏢局滅門<br />
                    1521年 (秋冬) 令狐冲上思過崖面璧
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1521</h4></div>
        </li>
        <li class="v06" style="margin-top: 0px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>
                    1522年 (春) 藥王廟之戰, 洛陽城訪金刀王家<br />
                    1522年 (初夏) 訪梅莊, 被囚西湖底<br />
                    1522年 7月 4 與黑白子調包脫困<br />
                    1522年 12月15 群豪上少室山迎聖姑
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1522</h4></div>
        </li>
        <li class="v06" style="margin-top: 0px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>
                    1523年 2月16 令狐冲接任恆山掌門<br />
                    1523年 3月15 五岳派併派大會
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1523</h4></div>
        </li>
        <li class="v06" style="margin-top: 30px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1526年 令狐冲與盈盈成親</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1526</h4></div>
        </li>
        <li class="v06" style="margin-top: 0px;">
            <div class="content">
                <h3>笑傲江湖</h3>
                <p>1527年 重回華山, 故事結束</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1527</h4></div>
        </li>
        <li class="v07" style="margin-top: 180px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>1545年 龍木島主訂交</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1545</h4></div>
        </li>
        <li class="v07" style="margin-top: 0px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>1546年 梅芳姑出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1546</h4></div>
        </li>
        <li class="v07" style="margin-top: 90px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>1555年 臘八之會(第1次)</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1555</h4></div>
        </li>
        <li class="v07" style="margin-top: 100px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>1565年 臘八之會(第2次)</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1565</h4></div>
        </li>
        <li class="v07" style="margin-top: 0px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>1566年 石中玉出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1566</h4></div>
        </li>
        <li class="v07" style="margin-top: 0px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>1567年 石破天(石中堅)出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1567</h4></div>
        </li>
        <li class="v07" style="margin-top: 80px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>1575年 臘八之會(第3次)</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1575</h4></div>
        </li>
        <li class="v07" style="margin-top: 20px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>
                    1579年 3月 9 孫萬年及褚萬春被殺<br />
                    1579年 3月12 故事開始, 侯監集眾人爭奪玄鐵令
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1579</h4></div>
        </li>
        <li class="v07" style="margin-top: 0px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>
                    1582年 石中玉加入長樂幫<br />
                    1582年 3月13 司徒橫被逐, 石中玉任長樂幫主
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1582</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1583年 孟伯飛出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1583</h4></div>
        </li>
        <li class="v07" style="margin-top: 0px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>
                    1585年 8月 長樂幫往摩天崖迎石破天<br />
                    1585年 10月 俠客島二使往長樂幫請客, 石中玉現身<br />
                    1585年 12月 5 石破天出發往俠客島<br />
                    1585年 12月 8 臘八之會(第4次)
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1585</h4></div>
        </li>
        <li class="v07" style="margin-top: 0px;">
            <div class="content">
                <h3>俠客行</h3>
                <p>
                    1586年 2月 石破天練太玄神功<br />
                    1586年 3月 8 眾掌門回歸中土<br />
                    1586年 梅芳姑自殺, 故事結束
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1586</h4></div>
        </li>
        <li class="v08" style="margin-top: 90px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1597年 溫南揚出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1597</h4></div>
        </li>
        <li class="v08" style="margin-top: 100px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1607年 溫儀出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1607</h4></div>
        </li>
        <li class="v08" style="margin-top: 40px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1611年 張朝唐出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1611</h4></div>
        </li>
        <li class="v08" style="margin-top: 90px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1620年 何惕守出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1620</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1621年 馮不破出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1621</h4></div>
        </li>
        <li class="v08" style="margin-top: 20px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1623年 馮不摧及袁承志出生, 溫南揚被捕, 為夏雪宜所救</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1623</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1624年 9月 3 溫天霸被殺, 溫儀被擄</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1624</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1625年 青青出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1625</h4></div>
        </li>
        <li class="v08" style="margin-top: 40px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1629年 袁崇煥下獄</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1629</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1630年 袁崇煥被處死</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1630</h4></div>
        </li>
        <li class="v08" style="margin-top: 30px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>
                    1633年 8月 故事開始, 張朝唐主僕結識楊舉鵬<br />
                    1633年 8月 16 山宗大會<br />
                    1633年 袁承志拜師
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1633</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1636年 袁承志始練劍, 木桑訪華山</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1636</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1637年 發現金蛇郎君山洞</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1637</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1637年 順治出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1637</h4></div>
        </li>
        <li class="v08" style="margin-top: 50px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1642年 焦公禮拜訪史家兄弟</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1642</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>
                    1643年 袁承志下山初闖江湖<br />
                    1643年 7月20 七省群豪泰山大會<br />
                    1643年 8月 皇太極被殺<br />
                    1643年 11月 2 焦公禮被殺
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1643</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>
                    1644年 3月17 袁承志闖宮見阿九, 何惕守拜師<br />
                    1644年 3月18 北京城破, 崇禎自殺<br />
                    1644年 4月15 華山派大會, 黃真接掌門戶
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1644</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>
                    1645年 4月15 清兵攻揚州<br />
                    1645年 4月25 揚州城破
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1645</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>164?年 故事結束</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>164?</h4></div>
        </li>
        <li class="v08" style="margin-top: 0px;">
            <div class="content">
                <h3>碧血劍</h3>
                <p>1648年 袁承志及阿九三年之約</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1648</h4></div>
        </li>
        <li class="v09" style="margin-top: 30px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1651年 西奧圖三世出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1651</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1652年 郭懷一被殺</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1652</h4></div>
        </li>
        <li class="v09" style="margin-top: 20px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1654年 康熙出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1654</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1655年 建寧公主出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1655</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1656年 曾柔、沐劍屏、韋小寶及伊凡出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1656</h4></div>
        </li>
        <li class="v09" style="margin-top: 50px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1661年 鄭成功攻台灣, 順治出家</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1661</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1662年 彼得出生, 鄭成功逝世, 吳三桂絞死永曆皇帝</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1662</h4></div>
        </li>
        <li class="v09" style="margin-top: 20px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1664年 湯若望下獄, 韋小寶禪知寺採花受辱</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1664</h4></div>
        </li>
        <li class="v09" style="margin-top: 20px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1666年 尹香主被殺</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1666</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>
                    1668年 3月28 故事開始, 韋小寶遇茅十八<br />
                    1668年 3月29 史松被殺<br />
                    1668年 韋小寶入宮 ~ 擒鰲拜
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1668</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1669年 海大富被殺 ~ 韋小寶被騙往神龍島</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1669</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1670年 王屋派賭命 ~ 雲南賜婚</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1670</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1671年 炮轟神龍教 ~ 往羅剎</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1671</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>
                    1672年 4月 西奧圖三世病死<br />
                    1672年 5月 羅剎火槍營作亂
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1672</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1673年 三藩上奏撤藩 ~ 攻王屋派</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1673</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1674年 吳之榮被殺 ~ 荒島上三個孩子出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1674</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>1675年 8月12 康熙立太子, 韋小寶升二等伯</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1675</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>
                    1678年 1月28 鄭經病死<br />
                    1678年 6月 4 施琅征台<br />
                    1678年 6月16 施琅與劉國軒水師會戰<br />
                    1678年 8月 施琅訪通吃島<br />
                    1678年 征羅剎
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1678</h4></div>
        </li>
        <li class="v09" style="margin-top: 0px;">
            <div class="content">
                <h3>鹿鼎記</h3>
                <p>
                    1679年 8月 簽訂尼布楚條約<br />
                    1679年 馮錫範被殺, 故事結束
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1679</h4></div>
        </li>
        <li class="v10" style="margin-top: 140px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1693年 于萬亭出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1693</h4></div>
        </li>
        <li class="v10" style="margin-top: 240px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1717年 8月13日 乾隆出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1717</h4></div>
        </li>
        <li class="v10" style="margin-top: 170px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1734年 陳家洛出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1734</h4></div>
        </li>
        <li class="v10" style="margin-top: 30px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1736年 駱冰出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1736</h4></div>
        </li>
        <li class="v10" style="margin-top: 30px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1739年 李沅芷出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1739</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1740年 周綺出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1740</h4></div>
        </li>
        <li class="v10" style="margin-top: 30px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1743年 屠龍幫瓦解</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1743</h4></div>
        </li>
        <li class="v10" style="margin-top: 10px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>
                    1748年 周英傑出生<br />
                    1748年 商劍鳴殺苗人鳳家人
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1748</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>
                    1749年 陳家洛中舉人<br />
                    1749年 陳家洛離家
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1749</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>
                    1752年12月20日 胡斐出生<br />
                    1752年12月22日 商劍鳴被殺<br />
                    1752年12月26日 胡一刀及胡夫人身亡
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1752</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>
                    1753年 6月 ＜書＞故事開始<br />
                    1753年 8月 李沅芷拜師
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1753</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1755年 李可秀調任甘肅安西鎮總兵</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1755</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1756年 雨詩及進忠死亡</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1756</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>
                    1758年 于萬亭逝世<br />
                    1758年 (秋) 陸菲青返中原<br />
                    1758年 8月17日 陳家洛湖上會乾隆<br />
                    1758年 8月18日 乾隆海寧祭祖<br />
                    1758年 8月20日 陳家洛探獄<br />
                    1758年 8月21日 張召重王維揚比武，文泰來獲救<br />
                    1758年 9月 余魚同被擒
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1758</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1759年 8月16日 火燒雍和宮，＜書＞故事結束</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1759</h4></div>
        </li>
        <li class="v10" style="margin-top: 0px;">
            <div class="content">
                <h3>書劍恩仇錄</h3>
                <p>1761年 王維揚逝世</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1761</h4></div>
        </li>
        <li class="v11" style="margin-top: 0px;">
            <div class="content">
                <h3>飛狐外傳</h3>
                <p>1762年 南仁通被殺</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1762</h4></div>
        </li>
        <li class="v11" style="margin-top: 0px;">
            <div class="content">
                <h3>飛狐外傳</h3>
                <p>1763年 苗若蘭出生</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1763</h4></div>
        </li>
        <li class="v11" style="margin-top: 0px;">
            <div class="content">
                <h3>飛狐外傳</h3>
                <p>
                    1765年 ＜飛＞故事開始，閻基劫鏢<br />
                    1765年 鐵廳烈火
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1765</h4></div>
        </li>
        <li class="v11" style="margin-top: 20px;">
            <div class="content">
                <h3>飛狐外傳</h3>
                <p>
                    1769年 6月 血印石慘案<br />
                    1769年 8月15日 天下掌門人大會, ＜飛＞故事結束
                </p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1769</h4></div>
        </li>
        <li class="v12" style="margin-top: 100px;">
            <div class="content">
                <h3>雪山飛狐</h3>
                <p>1780年 3月15日 ＜雪＞全書內容</p>
            </div>
            <div class="point"></div>
            <div class="date"><h4>1780</h4></div>
        </li>
    </ul>
</div>

