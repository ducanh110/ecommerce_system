Write-Host "Đang chạy Server Java..."

Write-Host "Đang xây dựng project với Maven..."
mvn clean compile assembly:single

Write-Host "Đang khởi động server..."
java -jar target\product-search-1.0-SNAPSHOT-jar-with-dependencies.jar ..\product_texts.json

Read-Host -Prompt "Nhấn Enter để thoát"
