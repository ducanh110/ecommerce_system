@echo off
echo Đang chạy Server Java...
cd /d %~dp0

echo Đang xây dựng project với Maven...
call mvn clean compile assembly:single

echo Đang khởi động server...
java -jar target\product-search-1.0-SNAPSHOT-jar-with-dependencies.jar ..\product_texts.json

pause
