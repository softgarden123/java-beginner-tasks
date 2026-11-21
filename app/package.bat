@echo off
if not exist out\JavaBeginnerLauncher.jar (
  echo Please run build.bat first.
  exit /b 1
)
if exist out\installer rd /s /q out\installer
jpackage --type exe --name JavaBeginnerLauncher --input out --main-jar JavaBeginnerLauncher.jar --main-class launcher.Launcher --dest out\installer --app-version 1.0
if errorlevel 1 (
  echo jpackage failed.
  exit /b 1
)
echo Packaging succeeded. The exe will be in out\installer.
