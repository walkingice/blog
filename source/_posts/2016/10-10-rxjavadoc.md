title: RxJava Javadoc
s: rxjavadoc
date: 2016-10-10 12:20:02
tags:
    - java
    - tools
    - android

categories: geek
---

*2018/04/02 Update: add 2.1.12 doc download point*

ReactiveX is cool, as well as RxJava. If you are developing app and using RxJava, of course you often refer [RxJava documentations](http://reactivex.io/RxJava/javadoc/). I would like to refer its javadoc from my local host, but cannot find out a download point. Therefore I make a copy and upload it.

* 1.2.0
  * [Download RxJava 1.2.0 javadoc from Dropbox](https://dl.dropboxusercontent.com/u/9824121/rxjava-javadoc.zip)
  * [Download RxJava 1.2.0 javadoc from GoogleDrive](https://drive.google.com/open?id=0Bwg-OI96Zt_BU2VMY0xmWHJ0OFE)
  * About 40MB, includes all images
* 2.1.12
  * [Download RxJava 2.1.12 javadoc from Dropbox](https://www.dropbox.com/s/diwj6hgioggjnho/rxjava-2.1.12-javadoc.zip)
  * [Download RxJava 2.1.12 javadoc from GoogleDrive](https://drive.google.com/open?id=1H2PNtGKaWW1fsXL95rRt3kJBATpBqu4g)
  * About 50MB, includes all images

<!-- more -->

RxJava put its images on github, I don't want to disturb github everytime when I read Observable page. So, download all images to archive. I also modified css file a bit, since 1024px provides me better reading experience, although I have wide screen monitor.

How I generate it?

1. use wget

    ```bash
    $ wget -r -erobots=off --no-parent http://reactivex.io/RxJava/javadoc/
    find . -name 'index.html*' |xargs -i rm {} && echo 'Index.html clean'
    ```

1. Get images and replace path

    ```bash
    $ cd /RxJava/javadoc
    $ ./GET_IMG.sh
    ```

1. copy my own customized css. I prefer 1024px width

GET_IMG.sh is a simple, dirty script, which is written in short time

```
## GET_IMG.sh

_dirs=`find . -type d`
_grep=`which grep`

function getImg {
  urls=`${_grep} 'github.com.*/images/.*' *.html |sed 's|.*\(https.*png\).*|\1|' |sort |uniq`
  for url in $urls; do
    if [ "z$url" != "z" ]; then
      wget $url
    fi
  done
}

function replacePath {
  sed -ie s,https://.*.github.*/,./, *.html
}

for _dir in $_dirs; do
  pushd $_dir
  getImg $_dir
  replacePath
  popd
done
```

Here you go, you can build your own RxJava javadoc archive.

