@echo off
echo Dang khoi dong server Java...
cd /d %~dp0
java -jar target\product-search-1.0-SNAPSHOT-jar-with-dependencies.jar ..\product_texts.json
pause
