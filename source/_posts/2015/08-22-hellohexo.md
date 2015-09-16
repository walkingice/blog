title: Hello Hexo
s: hellohexo
date: 2015-08-22 03:23:11
tags:
    - geek
    - tools
categories: geek
comments: true
---

初次使用 Hexo，先來篇文章試試看

# Why

原本因為 blogspot 的編輯介面太難用，想要改用 wordpress。可是 wordpress 免費的 theme 在中文字型上幾乎都不好看，自訂 css 又是付費版功能。

想來想去，又被 [John 跟小安老師](https://twitter.com/walkingice/status/633698699504427008) 開導之後，想說乾脆就弄一個 generated blog 了，好處有

1. 文章用 markdown 來寫，即使不透過 browser 也能看得懂
1. git based，天生就備份好了

<!-- more -->

# How

要開新文章也滿簡單的

``` bash
$ hexo -s slug "My New Post Title"
```

一行就搞定了，直接用 markdown 編寫挺快樂的。要 deploy 也很簡單

```bash
$ hexo generate
$ hexo deploy
```

generate 的東西會放在 <proj>/deploy_*/ 底下，我是不太在乎 generated 的 history，應該偶爾會用 git rebase 把東西壓成一個 commit 吧。

# Domain name

安裝 [hexo-generator-cname](https://www.npmjs.com/package/hexo-generator-cname) 之後，我參考了 [GitHub Pages Custom Domains](http://michaelhsu.tw/2014/06/20/github-pages-custom-domains/) by Michael Hsu 的文章來設定 domain。

另外可參考

* [Stackoverflow: How to setup Github Pages to redirect DNS requests from subdomain to top-level domain?](http://stackoverflow.com/a/23375423)
* [Stackoverflow: Github Pages: a custom subdomain vs a custom apex domain](http://stackoverflow.com/questions/25801245/github-pages-a-custom-subdomain-vs-a-custom-apex-domain)

