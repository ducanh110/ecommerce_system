
import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.json.*;

/**
 * Server API JSON cơ bản để xử lý tìm kiếm sản phẩm
 */
public class ProductServer {
    private static final int PORT = 8080;
    private ProductSearchService searchService;

    public ProductServer() {
        searchService = new ProductSearchService();
    }

    /**
     * Khởi động server
     */
    public void start(String jsonFilePath) {
        try {
            // Tải dữ liệu sản phẩm
            searchService.loadProductsFromJson(jsonFilePath);

            // Tạo HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Đăng ký các endpoint API
            server.createContext("/api/products", new ProductsHandler());
            server.createContext("/api/search", new SearchHandler());

            // Thiết lập executor
            server.setExecutor(null);

            // Bắt đầu server
            server.start();

            System.out.println("Server đang chạy ở cổng " + PORT);
            System.out.println("API endpoints:");
            System.out.println("  - http://localhost:" + PORT + "/api/products - Lấy tất cả sản phẩm");
            System.out.println("  - http://localhost:" + PORT + "/api/search?q={query} - Tìm kiếm sản phẩm");

        } catch (IOException e) {
            System.err.println("Lỗi khi khởi động server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
        }
    }

    /**
     * Handler xử lý endpoint trả về tất cả sản phẩm
     */
    private class ProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, 0); // Method Not Allowed
                exchange.getResponseBody().close();
                return;
            }

            try {
                List<Product> allProducts = searchService.getAllProducts();
                JSONArray jsonResponse = convertProductsToJson(allProducts);

                // Thiết lập response headers
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

                // Gửi response
                byte[] responseBytes = jsonResponse.toString().getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                // Ghi dữ liệu
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

            } catch (Exception e) {
                String errorMessage = "{\"error\": \"" + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                byte[] responseBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * Handler xử lý endpoint tìm kiếm sản phẩm
     */
    private class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, 0); // Method Not Allowed
                exchange.getResponseBody().close();
                return;
            }

            try {
                // Lấy tham số truy vấn
                String query = exchange.getRequestURI().getQuery();
                String searchQuery = "";

                if (query != null && query.startsWith("q=")) {
                    searchQuery = java.net.URLDecoder.decode(query.substring(2), "UTF-8");
                }

                // Thực hiện tìm kiếm
                List<Product> searchResults = searchService.searchProducts(searchQuery);
                JSONArray jsonResponse = convertProductsToJson(searchResults);

                // Thiết lập response headers
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

                // Gửi response
                byte[] responseBytes = jsonResponse.toString().getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                // Ghi dữ liệu
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

            } catch (Exception e) {
                String errorMessage = "{\"error\": \"" + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                byte[] responseBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * Chuyển đổi danh sách sản phẩm thành JSON
     * 
     * @param products Danh sách sản phẩm
     * @return JSONArray chứa dữ liệu sản phẩm
     */
    private JSONArray convertProductsToJson(List<Product> products) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Product product : products) {
            JSONObject jsonProduct = new JSONObject();

            // Thêm các thông tin cơ bản
            jsonProduct.put("Tên sản phẩm", product.getName());
            jsonProduct.put("Ảnh", product.getImage());
            jsonProduct.put("Giá", product.getPrice());
            jsonProduct.put("Điểm đánh giá trung bình", product.getRating());
            jsonProduct.put("Số lượt đánh giá", product.getRatingCount());
            jsonProduct.put("Mô tả sản phẩm", product.getDescription());

            // Thêm giá cũ nếu có
            if (product.getOldPrice() != null && !product.getOldPrice().isEmpty()) {
                jsonProduct.put("Giá cũ", product.getOldPrice());
            }

            // Thêm nguồn dữ liệu nếu có
            if (product.getSource() != null && !product.getSource().isEmpty()) {
                jsonProduct.put("Nguồn dữ liệu", product.getSource());
            }

            // Thêm các thông số kỹ thuật
            JSONArray specsArray = new JSONArray();
            Specification[] specs = product.extractSpecifications();

            for (Specification spec : specs) {
                JSONObject jsonSpec = new JSONObject();
                jsonSpec.put("label", spec.getLabel());
                jsonSpec.put("value", spec.getValue());
                specsArray.put(jsonSpec);
            }

            jsonProduct.put("specifications", specsArray);

            // Thêm thông tin key info tóm tắt
            jsonProduct.put("key_info", product.extractKeyInfo());

            // Thêm trạng thái nổi bật
            jsonProduct.put("is_featured", product.isFeatured());

            // Thêm sản phẩm vào mảng JSON
            jsonArray.put(jsonProduct);
        }

        return jsonArray;
    }

    public static void main(String[] args) {
        ProductServer server = new ProductServer();

        // Mặc định sử dụng file product_texts.json
        String jsonFilePath = "D:\\project_oop\\test\\product_texts.json";

        // Nếu có tham số dòng lệnh, sử dụng tham số đầu tiên làm đường dẫn file
        if (args.length > 0) {
            jsonFilePath = args[0];
        }

        server.start(jsonFilePath);
    }
}
