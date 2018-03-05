title: 使用 ffmpeg 編輯影片
s: ffmpeg
date: 2018-02-27 23:25:41
tags:
    - geek
    - tools
    - linux
categories: geek
---

長話短說，可以用 ffmpeg 做到一些簡單的影片編輯功能，好比說

把 input.mp4 裡面 00:24:32 到 00:29:34 這段時間的影像擷取出來

```bash
$ ffmpeg -i input.mp4 -ss 00:24:32 -to 00:29:34 -threads 2 -y clip001.mp4
```

從一張圖片產生一個長度 15 秒的影片，同時把尺寸轉為 1280x720

```bash
$ ffmpeg -loop 1 -i image.png -t 15 -vf scale=1280:720 -pix_fmt yuv420p -y image.mp4
```

將一個完全沒有音軌的影片，塞入 15 秒的無聲音軌，指定頻率為 48000 Hz。

```bash
$ ffmpeg -f lavfi -i anullsrc=r=48000 -i image.mp4 -t 15 -c:v copy -c:a aac  -y silent.mp4
```

接著把幾個 mp4 檔接成一個檔案，先把它們都轉成 ts 檔，最後再接回 mp4 檔

```bash
$ ffmpeg -i clip0.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts -y clip0.ts
$ ffmpeg -i clip1.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts -y clip1.ts
$ ffmpeg -i clip2.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts -y clip2.ts

$ ffmpeg -i "concat:clip0.ts|clip1.ts|clip2.ts" -c copy -bsf:a aac_adtstoasc -y output.mp4

$ rm clip0.ts clip1.ts clip2.ts
```

<!-- more -->

# 為什麼會有需求

主要是因為幫強者我朋友打逐字稿：類似國會的做法，把他在頭份代表會裡面的所有質詢片段都剪輯出來放上網路。但是代表會的人員不允許我們把所有代表的開會影像都放上網路，我們只好自己來。每一次會議都要重複這樣的動作

* 自行準備硬碟，到代表會複製影像紀錄
* 在所有的影像中，剪出強者我同學發言的部分
* 將剪輯的片段排序後，合併成一部影片，並插入適當提示(譬如說，這是哪一天的會議內容)
* 將影片內容打成逐字稿

一年有兩次定期會，每次約一週；八次不等的臨時會，代表的任期是四年，同樣的事件重複幾十次真的很痛苦。原本用 Youtube 上面的影像編輯功能去做，後來這個功能收了，嘗試用 Openshot 卻一直 crash。乾脆直接寫簡單的 script

# Scripts

在這邊附上我所使用得 scripts，需要的人可以撿去用，同時也是給自己當作備份。因為在製作影片的過程中常常因為各種奇怪的原因突然被中斷，所以我習慣生成一整排的指令來執行，這樣可以很方便的暫停或是跳過之前已經處理過的。

scripts 寫得比較隨性些，不要太在意啦 :P

## 把影片剪出一部份

首先是把一個比較長的影片，擷取出指定時間內的片段。以底下的範例來說，就是 `full_01.mp4 的 8 分 55 秒到 15 分 40 秒 切出一個片段`，以及 `full_02.mp4 的 0 秒到 1 分 15 秒一個片段， 7 分 31 秒到 9 分 51 秒一個片段，11 分 15 秒到 12 分 8 秒一個片段`

把這些資訊寫到一個檔案裡面，然後執行以下的 script 就會生出需要的指令

```bash
#!/bin/sh

# Usage:
# echo "full_01.mp4,00:08:55-00:15:40" >> input_file
# echo "full_02.mp4,00:00:00-00:01:15 00:07:31-00:09:51 00:11:15-00:12:08" >> input_file
# sh gencmd_clip.sh input_file > cmds_for_clip
# sh cmds_for_clip

echo "# To parse file: $1"

[ ! -e "$1" ] && echo "File not exist: $1" && exit

# sanity
while IFS=, read -r inputfile period
do
	[ ! -f "$inputfile" ] && echo "input file not exist: $inputfile" && exit
	[ "$period" == "" ] && echo "no period in: $inputfile" && exit
done < $1

while IFS=, read -r inputfile period
do
	_fn=$(echo $inputfile | cut -f 1 -d '.')
	echo "# $inputfile create clips"
  cnt=1
  for t in $period
  do
    from=`echo $t | cut -d - -f 1`
    to=`echo $t | cut -d - -f 2`
    postfix=$(printf "clip%03d" $cnt)
    echo "ffmpeg -i $inputfile -ss $from -to $to -threads 2 -y $_fn-$postfix.mp4"
    ((cnt++))
  done
	echo ""
done < $1

echo "# Done, commands generated"
```

## 把一堆圖片轉成 mp4 檔

因為影片的片段眾多，中間總是要插入一些過場的文字提示，所以會有一些 png 檔要變成有一定長度的 mp4 檔。必須要注意的是 **影片要插入一個靜音的音軌**，如果沒有音軌的話，接下來 concat 所有檔案的時候，就會發生影像長度比聲音長度還要長的情況，最後的結果就是影音不同步。

同樣也是要先把資訊寫進一個檔案裡面，以下的範例就是 `file_1.png 要產生 3 秒長的影片`, `file_2.png 要產生 5 秒長的影片`....

```bash
#!/bin/sh

# Usage:
# echo "file_1.png,3" >> input_file
# echo "file_2.png,5" >> input_file
# echo "file_3.png,5" >> input_file
# sh gencmd_png2mp4.sh input_file > cmds_for_png
# sh cmds_for_png

echo "# To parse file: $1"

[ ! -e "$1" ] && echo "File not exist: $1" && exit

# sanity
while IFS=, read -r png period
do
	[ ! -f "$png" ] && echo "png not exist: $png" && exit
	[ "$period" == "" ] && echo "no period in: $png" && exit
done < $1

while IFS=, read -r png period
do
	_fn=$(echo $png | cut -f 1 -d '.')
	_tmp=nosound-$_fn.mp4
	echo "# $png -> no sound mp4, $period seconds"
	echo "ffmpeg -loop 1 -i $_fn.png -t $period -c:v libx264 -pix_fmt yuv420p -y $_tmp"
	echo "# frequency=48000 silent sound track"
	echo "ffmpeg -f lavfi -i anullsrc=r=48000 -i $_tmp -t $period -c:v copy -c:a aac  -y $_fn.mp4"
	echo "rm $_tmp"
	echo ""
done < $1

echo "# Done, commands generated"
```

## 把影片合併成一個

最後就是把所以生成的影片合併成一個，所有檔案依照順序放進一個列表裡面即可。它會先轉成 ts 檔，接在一起之後再生出 mp4 檔

```bash
#!/bin/sh

# Usage:
# echo "file_1.mp4" >> input_file
# echo "file_2.mp4" >> input_file
# echo "file_3.mp4" >> input_file
# sh gencmd_concat_mp4.sh input_file > cmds_for_concat
# sh cmds_for_concat

echo "# To parse file: $1"

[ ! -e "$1" ] && echo "File not exist: $1" && exit

# sanity
while IFS=, read -r mp4
do
	[ ! -f "$mp4" ] && echo "mp4 not exist: $mp4" && exit
done < $1

_tmplist=""
_list=""
while IFS=, read -r mp4
do
  _fn=$(echo $mp4 | cut -f 1 -d '.')
  _tmp=$_fn.ts
  _tmplist="$_tmplist $_tmp"
  if [ -z $_list ];then
    _list=$_tmp
  else
    _list=$_list"|"$_tmp
  fi
  echo ffmpeg -i $mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts -y $_tmp
done < $1

echo ""

_output=$(echo $1 | cut -f 1 -d '.')
echo ffmpeg -i \"concat:$_list\" -c copy -bsf:a aac_adtstoasc "$_output""-concat.mp4"

echo ""

echo rm $_tmplist
```

# References

* [Creating an mp4 with black screen and silent audio (with ffmpeg)](https://yeupou.wordpress.com/2016/01/21/create-an-mp4-with-black-screen-and-silent-audio-with-ffmpeg/)
* [Concatenating mp4 videos (with ffmpeg)](https://yeupou.wordpress.com/2016/01/13/concatenate-mp4-videos-with-ffmpeg/)


