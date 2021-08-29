# ClojureScriptでカンバンアプリ (簡易trello) をつくるライブコーディング

## 概要

[Shibuya.lisp lispteatime #02](https://lisp.connpass.com/event/221217/)のライブコーディングパートで実装した、trell○っぽいカンバンアプリです。実装にはClojureScriptとその上のReactライブラリであるReagentを用いました。

-----

## 目標

以下の機能を実装する:

- [未着手] リスト (縦の塊) の追加・編集・削除
- [追加・削除実装] TODOの追加・編集・削除
- [未着手] TODOの移動 (リスト内の順序)
- [未着手] TODOの移動 (リスト間の移動)

## 事前に用意するもの

- npmコマンド
- JDK 16 (インストール済み)
- エディタ (ここではGNU Emacs with parinfer)

## ライブコーディング中にインスコするもの

実際には事前にやっておきました（時間がかかるため）

- shadow-cljs: ClojureScriptのビルドと実行環境。ライブリロードができる。
- ClojureScriptコンパイラ: shadow-cljsによってインストールされる

## 実装手順

### 1. Hello worldまで

#### 1. プロジェクトを用意

```
$ npx create-cljs-project kanban-app
```

[npmに登録されたプロジェクト](https://www.npmjs.com/package/create-cljs-project)らしい…

#### 2. shadow-cljsをインスコ

```
$ npx shadow-cljs
```

これでshadow-cljsの本体や、ClojureScriptコンパイラがmaven2とか経由して降ってくる。

#### 3. hello world

`src/main`がプログラムを置くディレクトリなのでここに`kanban/frontend/app.cljs`をつくって以下を書く。

```
(ns kanban.frontend.app)

(defn init[]
  (println "Hellow world"))
```

この関数をshadow-cljsのビルド設定である`shadow-cljs.edn`にエントリポイントとして登録する。

```
 ...
 :builds
 ;; frontendという名前のビルドを定義
 {:frontend  
  {:target :browser
   :modules {:main {:init-fn kanban.frontend.app/init}}}}}
```

そして実行してみると。

```
$ npx shadow-cljs watch frontend
```

ビルドに成功する。
ビルド結果は`public/js/`以下にできてる。

ClojureScriptはあくまでもJavaScript相当のものなので、実際にアプリにするにはHTML部分を書いてあげる必要がある。

まず`public/index.html`を以下のように書いて、

```
<html>
  <head>
    <title>kanban.cljs</title>
  </head>
  <body>
    <script src="/js/main.js"></script>
  </body>
</html>
```

`shadow-cljs.edn`を以下のように書き換える。

```
 ...
 :dev-http {8080 "public}   ;; ここが追加した行
 :builds
 {:frontend  
  {:target :browser
   :modules {:main {:init-fn kanban.frontend.app/init}}}}}
```

そしてページをリロードするとdev consoleに"hello world"の文字が！ (第一部完)

### 2. reagentなアプリへ

**以降はコードを見てね！**