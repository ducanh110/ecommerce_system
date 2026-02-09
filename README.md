# Product Search — Java Backend

Small Java HTTP API that loads product data from a JSON file and exposes listing and search endpoints.

Prerequisites
- Java JDK 8 or newer
- Apache Maven

Quick build
```bash
cd java_backend
mvn clean compile assembly:single
```

Run
- From `java_backend` using the provided Windows script:

```powershell
start_server.bat
```

- Or run the produced fat JAR directly (pass an optional path to a JSON file):

```bash
java -jar target/product-search-1.0-SNAPSHOT-jar-with-dependencies.jar ..\product_texts.json
```

API (HTTP)
- GET /api/products — returns all products (JSON array)
- GET /api/search?q={query} — search products (keywords, simple price/origin heuristics)

Examples
- List all products:

```bash
curl http://localhost:8080/api/products
```

- Search:

```bash
curl "http://localhost:8080/api/search?q=tivi+50+inch"
```

Defaults
- Port: 8080
- Default data file: `product_texts.json` at repository root (can be overridden by CLI arg)

Notes
- Responses include fields like `Tên sản phẩm`, `Ảnh`, `Giá`, `Mô tả sản phẩm`, `specifications`, `key_info`, and `is_featured`.
- CORS is enabled (`Access-Control-Allow-Origin: *`) for local frontend usage.

Troubleshooting
- If port 8080 is occupied, stop the other process or change the port in `java_backend/src/main/java/ProductServer.java` and rebuild.
- If Maven fails, ensure JDK and Maven are installed and network access is available for dependency downloads.



