@echo off
echo Cleaning build directories...
gradlew clean
echo Deleting build cache...
rmdir /S /Q .gradle
rmdir /S /Q app\build
echo Build cleaned. Please run your build command again.
