@echo off
if not exist out\JavaBeginnerLauncher.jar (
  echo Please run build.bat first.
  exit /b 1
)

if exist out\portable rd /s /q out\portable
if exist out\portable-input rd /s /q out\portable-input
mkdir out\portable-input
if errorlevel 1 exit /b 1

for %%f in (out\*.jar) do copy /Y "%%f" out\portable-input\ >nul
if errorlevel 1 exit /b 1

jpackage --type app-image --name JavaBeginnerLauncher --input out\portable-input --main-jar JavaBeginnerLauncher.jar --main-class launcher.Launcher --dest out\portable --app-version 1.0 --add-modules jdk.compiler --jlink-options "--strip-debug --no-man-pages --no-header-files"
if errorlevel 1 (
  echo Portable packaging failed.
  exit /b 1
)

rd /s /q out\portable-input
echo Portable app created at out\portable\JavaBeginnerLauncher.