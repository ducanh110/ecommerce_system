
/**
 * Lớp đại diện cho một sản phẩm
 */
public class Product {
    private String name;
    private String image;
    private String price;
    private String oldPrice;
    private String description;
    private String rating;
    private String ratingCount;
    private String source;
    private String productType;

    // Constructor
    public Product(
            String name,
            String image,
            String price,
            String description,
            String rating,
            String ratingCount,
            String source,
            String productType) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.source = source;
        this.productType = productType;
    }

    // Getters và Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * Phân tích thông số kỹ thuật từ mô tả sản phẩm
     * 
     * @return Mảng thông số kỹ thuật dưới dạng [tên, giá trị]
     */
    public Specification[] extractSpecifications() {
        if (description == null || description.isEmpty()) {
            return new Specification[0];
        }

        // Danh sách các mẫu thông số cần trích xuất
        String[][] patterns = {
                { "Loại sản phẩm", "Kiểu tủ: ([^\\.]+)" },
                { "Kiểu sản phẩm", "Loại Tivi: ([^\\.]+)" },
                { "Loại máy giặt", "Loại máy giặt: ([^\\.]+)" },
                { "Dung tích sử dụng", "Dung tích sử dụng: ([^\\.]+)" },
                { "Dung tích ngăn đá", "Dung tích ngăn đá: ([^\\.]+)" },
                { "Dung tích ngăn lạnh", "Dung tích ngăn lạnh: ([^\\.]+)" },
                { "Kích cỡ màn hình", "Kích cỡ màn hình: ([^\\.]+)" },
                { "Độ phân giải", "Độ phân giải: ([^\\.]+)" },
                { "Công nghệ hình ảnh", "Công nghệ hình ảnh: ([^\\.]+)" },
                { "Khối lượng giặt", "Khối lượng giặt: ([^\\.]+)" },
                { "Chất liệu cửa", "Chất liệu cửa tủ lạnh: ([^\\.]+)" },
                { "Chất liệu khay", "Chất liệu khay ngăn lạnh: ([^\\.]+)" },
                { "Năm ra mắt", "Năm ra mắt: ([^\\.]+)" },
                { "Nơi sản xuất", "Sản xuất tại: ([^\\.]+)" }
        };

        // Sử dụng java.util.regex.Pattern để trích xuất thông tin
        java.util.List<Specification> specsList = new java.util.ArrayList<>();

        for (String[] pattern : patterns) {
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern[1]);
            java.util.regex.Matcher matcher = regex.matcher(description);

            if (matcher.find()) {
                String value = matcher.groupCount() > 0 ? matcher.group(1).trim() : "Có";
                specsList.add(new Specification(pattern[0], value));
            }
        }

        // Xác định số người sử dụng
        if (description.contains("2 - 3 người")) {
            specsList.add(new Specification("Số người sử dụng", "2 - 3 người"));
        } else if (description.contains("4 - 5 người")) {
            specsList.add(new Specification("Số người sử dụng", "4 - 5 người"));
        } else if (description.contains("Trên 5 người")) {
            specsList.add(new Specification("Số người sử dụng", "Trên 5 người"));
        }

        // Chuyển danh sách thành mảng
        return specsList.toArray(new Specification[0]);
    }

    /**
     * Trích xuất thông tin quan trọng từ mô tả
     * 
     * @return Thông tin ngắn gọn về sản phẩm
     */
    public String extractKeyInfo() {
        if (description == null || description.isEmpty()) {
            return "";
        }

        java.util.List<String> keyInfo = new java.util.ArrayList<>();

        // Các regex pattern để trích xuất thông tin
        java.util.regex.Pattern capacityPattern = java.util.regex.Pattern.compile("Dung tích sử dụng: ([^\\.]+)");
        java.util.regex.Pattern screenSizePattern = java.util.regex.Pattern.compile("Kích cỡ màn hình: ([^\\.]+)");
        java.util.regex.Pattern typePattern = java.util.regex.Pattern.compile("Kiểu tủ: ([^\\.]+)");
        java.util.regex.Pattern tvTypePattern = java.util.regex.Pattern.compile("Loại Tivi: ([^\\.]+)");
        java.util.regex.Pattern washerTypePattern = java.util.regex.Pattern.compile("Loại máy giặt: ([^\\.]+)");
        java.util.regex.Pattern originPattern = java.util.regex.Pattern.compile("Sản xuất tại: ([^\\.]+)");
        java.util.regex.Pattern yearPattern = java.util.regex.Pattern.compile("Năm ra mắt: ([^\\.]+)");

        // Tìm và thêm thông tin về dung tích
        java.util.regex.Matcher capacityMatcher = capacityPattern.matcher(description);
        if (capacityMatcher.find()) {
            keyInfo.add(capacityMatcher.group(1).trim());
        }

        // Tìm và thêm thông tin về kích thước màn hình
        java.util.regex.Matcher screenSizeMatcher = screenSizePattern.matcher(description);
        if (screenSizeMatcher.find()) {
            keyInfo.add(screenSizeMatcher.group(1).trim());
        }

        // Tìm và thêm thông tin về kiểu tủ
        java.util.regex.Matcher typeMatcher = typePattern.matcher(description);
        if (typeMatcher.find()) {
            keyInfo.add(typeMatcher.group(1).trim());
        }

        // Tìm và thêm thông tin về loại Tivi
        java.util.regex.Matcher tvTypeMatcher = tvTypePattern.matcher(description);
        if (tvTypeMatcher.find()) {
            keyInfo.add(tvTypeMatcher.group(1).trim());
        }

        // Tìm và thêm thông tin về loại máy giặt
        java.util.regex.Matcher washerTypeMatcher = washerTypePattern.matcher(description);
        if (washerTypeMatcher.find()) {
            keyInfo.add(washerTypeMatcher.group(1).trim());
        }

        // Tìm và thêm thông tin về xuất xứ
        java.util.regex.Matcher originMatcher = originPattern.matcher(description);
        if (originMatcher.find()) {
            keyInfo.add("Sản xuất: " + originMatcher.group(1).trim());
        }

        // Tìm và thêm thông tin về năm ra mắt
        java.util.regex.Matcher yearMatcher = yearPattern.matcher(description);
        if (yearMatcher.find()) {
            keyInfo.add("Năm: " + yearMatcher.group(1).trim());
        }

        // Kết hợp các thông tin đã trích xuất
        return String.join(" • ", keyInfo);
    }

    /**
     * Phân tích giá từ chuỗi và trả về giá trị số
     * 
     * @return Giá dưới dạng long
     */
    public long parsePrice() {
        if (price == null || price.isEmpty()) {
            return 0;
        }

        // Loại bỏ các ký tự không phải số
        String numericPrice = price.replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(numericPrice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Đoán tên sản phẩm từ mô tả nếu tên rỗng
     */
    public void inferNameFromDescription() {
        if ((name == null || name.isEmpty()) && description != null && !description.isEmpty()) {
            // Xác định loại sản phẩm
            String productType = "Sản phẩm";

            if (description.contains("Kiểu tủ:")) {
                productType = "Tủ lạnh";
            } else if (description.contains("Loại Tivi:")) {
                productType = "Tivi";
            } else if (description.contains("Loại máy giặt:")) {
                productType = "Máy giặt";
            }

            // Tạo tên tạm thời
            StringBuilder tempName = new StringBuilder(productType);
            tempName.append(" ");

            // Thêm thông tin dung tích/kích thước
            if (description.contains("Dung tích sử dụng:")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Dung tích sử dụng: ([^\\.]+)");
                java.util.regex.Matcher matcher = pattern.matcher(description);
                if (matcher.find()) {
                    tempName.append(matcher.group(1).trim());
                }
            } else if (description.contains("Kích cỡ màn hình:")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Kích cỡ màn hình: ([^\\.]+)");
                java.util.regex.Matcher matcher = pattern.matcher(description);
                if (matcher.find()) {
                    tempName.append(matcher.group(1).trim());
                }
            }

            // Thêm thông tin năm ra mắt
            java.util.regex.Pattern yearPattern = java.util.regex.Pattern.compile("Năm ra mắt: ([^\\.]+)");
            java.util.regex.Matcher yearMatcher = yearPattern.matcher(description);
            if (yearMatcher.find()) {
                tempName.append(" (").append(yearMatcher.group(1).trim()).append(")");
            }

            // Cập nhật tên sản phẩm
            this.name = tempName.toString();
        }
    }

    /**
     * Kiểm tra xem sản phẩm có được đánh giá cao không (điểm >= 4.8)
     * 
     * @return True nếu sản phẩm có đánh giá cao
     */
    public boolean isFeatured() {
        try {
            float ratingValue = Float.parseFloat(this.rating);
            return ratingValue >= 4.8;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}

/**
 * Lớp biểu diễn một thông số kỹ thuật
 */
class Specification {
    private String label;
    private String value;

    public Specification(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
