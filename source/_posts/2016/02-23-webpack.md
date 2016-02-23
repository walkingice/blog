title: Gulp and Webpack-dev-server options
s: webpack
date: 2016-02-23 17:18:43
tags:
    - geek
    - webdev
categories: geek
---

通常以直接下指令的方式使用 **webpack-dev-server**，可能會配合一些參數。當我們更動其中一個檔案的時候，webpack 會在 console 顯示剛剛重編的那唯一一個檔案。

如果你想用 **gulp + webpack**，照著官方的[文件](https://webpack.github.io/docs/usage-with-gulp.html)做下去，你會發現結果有些不同

Usually we invoke command directly to launch **webpack-dev-server**, maybe adding some parameters. Once we modified one of files, webpack rebuild it and displays related message to that one only.

If you follow official [tutorial](https://webpack.github.io/docs/usage-with-gulp.html) to use **gulp + webpack**, then you find something different.

```bash
# run webpack-dev-server directly
$ webpack-dev-server --content-base _tmp --hot --inline
```

Update file by <code>touch</code>, webpack just rebuild it and print clean messages.

<div style="max-width: 800px; margin: auto;">{% asset_img expected.png %}</div>

<!-- more -->

使用 **gulp + webpack**，僅僅只是更動一個檔案，重編之後畫面會顯示所有的檔案，包括沒有重編的。在開發期相當惱人，chunks 全部加起來會有上百個，整個畫面都被洗。

To use **gulp + webpack**. Although just modified ONE file, after rebuild the console displays each of chunks includes immutable ones. There could be hundreds of chunks and it is pretty annoying. The build message flood your console.

<div style="max-width: 700px; margin: auto;">{% asset_img actually.png %}</div>

# Solution

解法其實很簡單，只要把 gulp 餵給 webpack-dev-server 的選項，多補上 <code>stats.cached: true</code> 即可。

Actually it is easy to resolve. In gulp file, just to add <code>stats.cached: true</code> to the options which be provided to webpack-dev-server.

```javascript
    new WebpackDevServer(compiler, {
        progress: true,
        hot: true,
        stats: {
            cached: false,    // HERE!
            colors: true
        }
    }).listen(PORT, "localhost", function(err) {
        if(err) throw new gutil.PluginError("webpack-dev-server", err);
    });
```

# Why

至少在 v2.0.0-beta 的版本，直接呼叫 **webpack-dev-server** 的時候它會[預設](https://github.com/webpack/webpack-dev-server/blob/v2.0.0-beta/bin/webpack-dev-server.js#L175)幫你補上 <code>options.stats.cached : false </code>，但是在 gulp 裡面以 module 的方式使用時，options 是[從零開始](https://github.com/webpack/webpack-dev-server/blob/v2.0.0-beta/lib/Server.js#L16)。

At least in version v2.0.0-beta, invoke command **webpack-dev-server** will append <code>options.stats.cached: false</code> to options [by default](https://github.com/webpack/webpack-dev-server/blob/v2.0.0-beta/bin/webpack-dev-server.js#L175). However, if we require webpack-dev-server as a module in gulp, the options is a [empty object](https://github.com/webpack/webpack/blob/v2.1.0-beta.3/lib/Stats.js#L41). The behavior is inconsistent.

```javascript
if(!options.stats) {
    options.stats = {
        cached: false,
        cachedAssets: false
    };
}
```

# Stats

在 webpack 的 [/lib/Stats.js](https://github.com/webpack/webpack/blob/v2.1.0-beta.3/lib/Stats.js#L41) 裡面你可以看到更多選項，控制編譯時的訊息輸出。如何知道這些文件都沒寫到的東西呢？當然是苦命地翻原始碼啊 (淚)

You could find more options in [/lib/Stats.js](https://github.com/webpack/webpack/blob/v2.1.0-beta.3/lib/Stats.js#L41) to control building message output. How do we know these undocumented stuffs? Of course source code. Use the source, Luke!

```javascript
var showHash = d(options.hash, true);
var showVersion = d(options.version, true);
var showTimings = d(options.timings, true);
var showAssets = d(options.assets, true);
var showChunks = d(options.chunks, true);
var showChunkModules = d(options.chunkModules, !!forToString);
var showChunkOrigins = d(options.chunkOrigins, !forToString);
var showModules = d(options.modules, !forToString);
var showCachedModules = d(options.cached, true);
var showCachedAssets = d(options.cachedAssets, true);
var showReasons = d(options.reasons, !forToString);
var showChildren = d(options.children, true);
var showSource = d(options.source, !forToString);
var showErrors = d(options.errors, true);
var showErrorDetails = d(options.errorDetails, !forToString);
var showWarnings = d(options.warnings, true);
var showPublicPath = d(options.publicPath, !forToString);
```
