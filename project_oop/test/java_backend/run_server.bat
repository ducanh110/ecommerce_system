@echo off
echo Đang chạy Server Java...
cd /d %~dp0
mvn clean compile assembly:single
java -jar target\product-search-1.0-SNAPSHOT-jar-with-dependencies.jar ..\product_texts.json
pause
