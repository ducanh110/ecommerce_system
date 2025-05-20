
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

/**
 * Lớp xử lý tìm kiếm sản phẩm
 */
public class ProductSearchService {
    private List<Product> products;

    public ProductSearchService() {
        products = new ArrayList<>();
    }

    /**
     * Tải dữ liệu sản phẩm từ file JSON
     * 
     * @param jsonFilePath Đường dẫn tới file JSON
     */
    public void loadProductsFromJson(String jsonFilePath) throws IOException, JSONException {
        products.clear();

        // Đọc nội dung file JSON
        Path path = Paths.get(jsonFilePath);
        String jsonContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

        // Parse JSON
        JSONArray jsonArray = new JSONArray(jsonContent);

        // Duyệt qua từng đối tượng JSON và chuyển đổi thành đối tượng Product
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonProduct = jsonArray.getJSONObject(i);

            // Lấy các giá trị từ JSON
            String name = jsonProduct.optString("Tên sản phẩm", "");
            String description = jsonProduct.optString("Mô tả sản phẩm", "");

            // Chỉ xử lý các sản phẩm có tên hoặc mô tả
            if (name.isEmpty() && description.isEmpty()) {
                continue;
            }

            // Tạo đối tượng Product
            Product product = new Product(
                    name,
                    jsonProduct.optString("Ảnh", "https://via.placeholder.com/400x300?text=No+Image"),
                    jsonProduct.optString("Giá", "0"),
                    description,
                    jsonProduct.optString("Điểm đánh giá trung bình", "0"),
                    jsonProduct.optString("Số lượt đánh giá", "0 đánh giá"),
                    jsonProduct.optString("Nguồn dữ liệu", ""),
                    jsonProduct.optString("Loại sản phẩm", ""));

            // Đặt giá cũ (nếu có)
            if (jsonProduct.has("Giá cũ")) {
                product.setOldPrice(jsonProduct.getString("Giá cũ"));
            }

            // Nếu không có tên, tạo tên từ mô tả
            if (name.isEmpty() && !description.isEmpty()) {
                product.inferNameFromDescription();
            }

            // Thêm sản phẩm vào danh sách
            products.add(product);
        }

        System.out.println("Đã tải " + products.size() + " sản phẩm từ file JSON");
    }

    /**
     * Tìm kiếm sản phẩm dựa trên từ khóa
     * 
     * @param query Từ khóa tìm kiếm
     * @return Danh sách sản phẩm phù hợp
     */
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(products);
        }

        List<Product> results = new ArrayList<>();
        String[] keywords = query.toLowerCase().split("\\s+");

        // Tạo bảng điểm cho từng sản phẩm
        Map<Product, Integer> productScores = new HashMap<>();

        for (Product product : products) {
            int score = 0;

            // Văn bản sản phẩm để tìm kiếm
            String productText = (product.getName() + " " + product.getDescription()).toLowerCase();

            // Tính điểm cho từng từ khóa có trong văn bản sản phẩm
            for (String keyword : keywords) {
                if (productText.contains(keyword)) {
                    score++;
                }
                // Tăng điểm nếu từ khóa xuất hiện trong tên sản phẩm
                if (product.getName().toLowerCase().contains(keyword)) {
                    score += 2;
                }
            }

            // Kiểm tra giá trong query và so sánh với giá sản phẩm
            if (query.contains("dưới") || query.contains("nhỏ hơn") || query.contains("it hon")) {
                // Tìm giá trị số trong truy vấn
                int priceLimit = extractPriceFromQuery(query, "dưới", "nhỏ hơn", "it hon");
                if (priceLimit > 0 && product.parsePrice() <= priceLimit) {
                    score += 3;
                }
            }

            if (query.contains("trên") || query.contains("lớn hơn") || query.contains("lon hon")) {
                // Tìm giá trị số trong truy vấn
                int priceLimit = extractPriceFromQuery(query, "trên", "lớn hơn", "lon hon");
                if (priceLimit > 0 && product.parsePrice() >= priceLimit) {
                    score += 3;
                }
            }

            // Tìm kiếm theo số người
            if (query.contains("người") || query.contains("nguoi")) {
                if ((query.contains("2") || query.contains("hai")) &&
                        (query.contains("3") || query.contains("ba")) &&
                        product.getDescription().contains("2 - 3 người")) {
                    score += 3;
                } else if ((query.contains("4") || query.contains("bốn") || query.contains("bon")) &&
                        (query.contains("5") || query.contains("năm") || query.contains("nam")) &&
                        product.getDescription().contains("4 - 5 người")) {
                    score += 3;
                } else if ((query.contains("5") || query.contains("năm") || query.contains("nam") ||
                        query.contains("nhiều") || query.contains("nhieu")) &&
                        product.getDescription().contains("Trên 5 người")) {
                    score += 3;
                }
            }

            // Tìm kiếm theo xuất xứ
            if ((query.contains("việt nam") || query.contains("viet nam")) &&
                    product.getDescription().contains("Sản xuất tại: Việt Nam")) {
                score += 3;
            }
            if ((query.contains("thái lan") || query.contains("thai lan")) &&
                    product.getDescription().contains("Sản xuất tại: Thái Lan")) {
                score += 3;
            }
            if ((query.contains("trung quốc") || query.contains("trung quoc") || query.contains("tàu")
                    || query.contains("tau")) &&
                    product.getDescription().contains("Sản xuất tại: Trung Quốc")) {
                score += 3;
            }

            // Lưu điểm số nếu sản phẩm có điểm > 0
            if (score > 0) {
                productScores.put(product, score);
            }
        }

        // Sắp xếp sản phẩm theo điểm số giảm dần
        List<Map.Entry<Product, Integer>> sortedEntries = new ArrayList<>(productScores.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Lấy các sản phẩm phù hợp nhất, tối đa 15 sản phẩm
        for (Map.Entry<Product, Integer> entry : sortedEntries) {
            results.add(entry.getKey());
            if (results.size() >= 15) {
                break;
            }
        }

        return results;
    }

    /**
     * Trích xuất giá trị giá tiền từ câu truy vấn
     */
    private int extractPriceFromQuery(String query, String... priceKeywords) {
        // Tìm các từ khóa giá tiền trong truy vấn
        int startPos = -1;
        for (String keyword : priceKeywords) {
            if (query.contains(keyword)) {
                int pos = query.indexOf(keyword);
                if (startPos == -1 || pos < startPos) {
                    startPos = pos + keyword.length();
                }
            }
        }

        if (startPos != -1) {
            // Tìm số trong truy vấn sau vị trí từ khóa giá tiền
            StringBuilder priceStr = new StringBuilder();
            boolean foundDigit = false;

            for (int i = startPos; i < query.length(); i++) {
                char c = query.charAt(i);

                if (Character.isDigit(c)) {
                    priceStr.append(c);
                    foundDigit = true;
                } else if (foundDigit && c == ',' || c == '.') {
                    // Bỏ qua các dấu phân cách số
                    continue;
                } else if (foundDigit && !Character.isDigit(c)) {
                    // Nếu đã tìm thấy số và gặp ký tự không phải số, kiểm tra đơn vị
                    String remainingText = query.substring(i).trim().toLowerCase();
                    if (remainingText.startsWith("triệu") || remainingText.startsWith("trieu")) {
                        // Nếu đơn vị là triệu, nhân với 1000000
                        try {
                            return Integer.parseInt(priceStr.toString()) * 1000000;
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    } else if (remainingText.startsWith("nghìn") ||
                            remainingText.startsWith("nghin") ||
                            remainingText.startsWith("k")) {
                        // Nếu đơn vị là nghìn, nhân với 1000
                        try {
                            return Integer.parseInt(priceStr.toString()) * 1000;
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    } else {
                        // Nếu không có đơn vị rõ ràng, giả định là giá trị đồng
                        break;
                    }
                }
            }

            // Trả về giá trị tìm được
            if (priceStr.length() > 0) {
                try {
                    return Integer.parseInt(priceStr.toString());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }

        return 0;
    }

    /**
     * Lấy tất cả sản phẩm
     * 
     * @return Danh sách tất cả sản phẩm
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Format giá tiền theo định dạng tiền Việt Nam
     * 
     * @param price Giá tiền
     * @return Chuỗi giá tiền định dạng VND
     */
    public static String formatPrice(long price) {
        return String.format("%,d₫", price).replace(",", ".");
    }
}
