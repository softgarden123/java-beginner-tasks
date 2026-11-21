@echo off
setlocal EnableExtensions

set FLEXMARK_VERSION=0.64.8
set FLEXMARK_JAR=flexmark-all-%FLEXMARK_VERSION%-lib.jar
set LIB_DIR=lib
set FLEXMARK_URL=https://repo1.maven.org/maven2/com/vladsch/flexmark/flexmark-all/%FLEXMARK_VERSION%/%FLEXMARK_JAR%

if not exist %LIB_DIR% mkdir %LIB_DIR%
if not exist %LIB_DIR%\%FLEXMARK_JAR% (
	echo Downloading %FLEXMARK_JAR% ...
	powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%FLEXMARK_URL%' -OutFile '%LIB_DIR%\%FLEXMARK_JAR%'"
	if errorlevel 1 goto :error
)

if exist out rd /s /q out
mkdir out
mkdir out\classes
if exist out\sources.txt del /q out\sources.txt
for /r src %%f in (*.java) do @echo %%f>>out\sources.txt
javac -cp "%LIB_DIR%\*" -d out\classes @out\sources.txt
if errorlevel 1 goto :error
if exist out\sources.txt del /q out\sources.txt
copy /Y src\readme.txt out\classes\readme.txt >nul
if errorlevel 1 goto :error
if not exist out\classes\img mkdir out\classes\img
for /r src %%f in (*.png) do copy /Y "%%f" out\classes\img\ >nul
if errorlevel 1 goto :error
jar --create --file out\JavaBeginnerLauncher.jar --main-class launcher.Launcher -C out\classes .
if errorlevel 1 goto :error
copy /Y %LIB_DIR%\%FLEXMARK_JAR% out\%FLEXMARK_JAR% >nul
if errorlevel 1 goto :error
echo Build succeeded.
exit /b 0
:error
echo Build failed.
exit /b 1
