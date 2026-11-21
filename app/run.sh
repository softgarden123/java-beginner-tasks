#!/bin/bash

# Mac 用バンドル JRE を優先して使用する
JRE_DIR="jre_m"
if [ -d "$JRE_DIR" ]; then
    JAVA="$JRE_DIR/bin/java"
else
    JAVA="java"
fi

"$JAVA" -cp "out/JavaBeginnerLauncher.jar:lib/*" launcher.Launcher
