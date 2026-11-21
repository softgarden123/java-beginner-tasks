# Java Beginner Launcher

このディレクトリは、各課題をまとめた統合版アプリです。

## ビルド手順

1. `app` フォルダを開きます。
2. `build.bat` を実行します。

これにより `out\JavaBeginnerLauncher.jar` が生成されます。

- 初回ビルド時は `flexmark-all-0.64.8-lib.jar` を `lib` に自動ダウンロードします。

## 実行手順

- `run.bat` を実行すると、ランチャーが起動します。
- 実行時は `lib\*.jar` をクラスパスに含めて起動します。

## exe 生成

- `package.bat` を実行すると、`jpackage` を使って `out\installer` に exe を生成します。
- Java 21 以降の JDK が必要です。

## インストール不要版

- 先に `build.bat` を実行し、その後 `package-portable.bat` を実行します。
- `out\portable\JavaBeginnerLauncher` に Java 実行環境とコンパイラーを含むアプリフォルダーが生成されます。利用側で Java を別途インストールする必要はありません。
- `JavaBeginnerLauncher.exe` を実行して起動します。配布時はフォルダー全体を ZIP にしてください。
- この形式ではインストーラーを作らないため、WiX Toolset は不要です。

## 1つのアプリへの統合

- `launcher.Launcher` が入口です。
- 各課題は `task01` から `task09` のパッケージに整理されました。
- 既存の `*.exe` 方式から、同一 JVM 内で各課題を起動する構成に変更しています。
