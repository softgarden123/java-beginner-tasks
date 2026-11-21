#!/bin/bash
set -e

if [ ! -f out/JavaBeginnerLauncher.jar ]; then
    echo "Please run build.sh first."
    exit 1
fi

# Mac 用バンドル JRE を優先して使用する
JRE_DIR="jre_m"
if [ -d "$JRE_DIR" ]; then
    JPACKAGE="$JRE_DIR/bin/jpackage"
else
    JPACKAGE="jpackage"
fi

rm -rf out/installer

"$JPACKAGE" \
    --type dmg \
    --name JavaBeginnerLauncher \
    --input out \
    --main-jar JavaBeginnerLauncher.jar \
    --main-class launcher.Launcher \
    --dest out/installer \
    --app-version 1.0

echo "Packaging succeeded. The dmg will be in out/installer."
