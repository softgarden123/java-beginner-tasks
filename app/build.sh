#!/bin/bash
set -e

FLEXMARK_VERSION=0.64.8
FLEXMARK_JAR="flexmark-all-${FLEXMARK_VERSION}-lib.jar"
LIB_DIR=lib
FLEXMARK_URL="https://repo1.maven.org/maven2/com/vladsch/flexmark/flexmark-all/${FLEXMARK_VERSION}/${FLEXMARK_JAR}"

# Mac 用バンドル JRE を優先して使用する
JRE_DIR="jre_m"
if [ -d "$JRE_DIR" ]; then
    JAVAC="$JRE_DIR/bin/javac"
    JAR="$JRE_DIR/bin/jar"
else
    JAVAC="javac"
    JAR="jar"
fi

mkdir -p "$LIB_DIR"

if [ ! -f "$LIB_DIR/$FLEXMARK_JAR" ]; then
    echo "Downloading $FLEXMARK_JAR ..."
    curl -L -o "$LIB_DIR/$FLEXMARK_JAR" "$FLEXMARK_URL"
fi

rm -rf out
mkdir -p out/classes

find src -name "*.java" > out/sources.txt
"$JAVAC" -cp "$LIB_DIR/*" -d out/classes @out/sources.txt
rm -f out/sources.txt
cp src/readme.txt out/classes/readme.txt
"$JAR" --create --file out/JavaBeginnerLauncher.jar --main-class launcher.Launcher -C out/classes .
cp "$LIB_DIR/$FLEXMARK_JAR" "out/$FLEXMARK_JAR"
echo "Build succeeded."
