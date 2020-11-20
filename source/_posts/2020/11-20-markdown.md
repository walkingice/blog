title: 用 Python 轉換 markdown 至 html
date: 2020-11-20 23:02:01
categories: geek
tags:
    - python
---

我經常使用 markdown 格式做紀錄，包括我自己的 local [gitit](http://walkingice.blogspot.com/2011/11/gitit-git-based-wiki.html) 或是這個 Blog。

除了紀錄之外，有時候需要將 Markdown 格式的文件轉成其他格式的檔案來閱讀。

<!-- more -->

譬如說寫完了文件，可以轉出簡單的 html 檔案傳給同事，或是公司要求的報告，先用 markdown 寫，轉成 html 之後再印成 pdf 檔。

除了要轉出 html 之外，總希望能有客制 css style，這樣生出來的 html 檔自己看起來比較爽，用起來也比較開心。稍微試了一下 `brew` 安裝的 markdown 工具，或是 node 上面的一些套件，總覺得有點不順手，或是一個超簡單的需求卻搞到很複雜。

最後決定快速地寫個很簡單的 python script 來處理這個問題，反正也不是第一次寫了，就順便貼在這裡

```python
#!/usr/bin/env python3

"""
The MIT License (MIT)

Copyright (c) 2020 Julian Chu

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
"""

import os.path
import sys

# pip3 install markdown
import markdown

template = '''
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <style>
            @media screen {
                 body {
                    color: #EEE;
                    background-color: #111;
                    line-height: 1.5rem;
                    font-family: sans-serif;
                    font-weight: 300;
                    margin: auto;
                    max-width: 80rem;
                    line-height: 1.5rem;
                }
                h1, h2, h3, h4, h5 {
                    line-height: 1.6rem;
                    margin-top: 3rem;
                }
                li {
                    line-height: 1.6rem;
                }
                a {
                    color: #AAF;
                }
            }

            @media print {
                body {
                    background-color: white;
                    font-size: 12pt;
                }

                a:link, a:visited, a {
                    color: grey;
                    font-weight: bold;
                    text-decoration: underline;
                    word-wrap: break-word;
                }
                thead {
                    display: table-header-group;
                }
            }

        </style>
    </head>
    <body>
    %s
    </body>
</html>
'''


def as_html(content):
    return template % content


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: %s file" % os.path.basename(sys.argv[0]))
        sys.exit()

    encoding = 'utf-8'

    with open(sys.argv[1], mode="r", encoding=encoding) as file:
        file_input = file.read()
        file.close()

    converted = markdown.markdown(file_input)
    sys.stdout.write(as_html(converted))
    sys.exit(0)
```

一看就知道是在幹什麼
* 讀進指定的 markdown，轉成 html content
* 把 content 塞進寫好的 template string
* 成果寫到 stdout

```bash
$ pip3 install markdown
# save python file as md.py, add to $PATH
$ md.py INPUT.md > output.html
```
