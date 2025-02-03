package com.dev.tool;

public class Order {
    private long orderNumber;       // Номер заказа
    private long chrtId;           // chrt_id
    private String sellerCode;     // Артикул продавца
    private String wbarcode;       // Артикул Wbarcode
    private String productName;    // Название товара
    private String packageSize;    // Размеры упаковки
    private String weight;         // Вес
    private String size;           // Размер
    private String color;          // Цвет
    private String brand;          // Бренд
    private long productBarcode; // ШК товара
    private String sticker;        // Стикер
    private String stickerScan;    // Стикер при считывании

    public Order() {
    }

    public Order(long orderNumber, long chrtId, String sellerCode, String wbarcode, String productName, String packageSize, String weight, String size, String color, String brand, long productBarcode, String sticker, String stickerScan) {
        this.orderNumber = orderNumber;
        this.chrtId = chrtId;
        this.sellerCode = sellerCode;
        this.wbarcode = wbarcode;
        this.productName = productName;
        this.packageSize = packageSize;
        this.weight = weight;
        this.size = size;
        this.color = color;
        this.brand = brand;
        this.productBarcode = productBarcode;
        this.sticker = sticker;
        this.stickerScan = stickerScan;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public long getChrtId() {
        return chrtId;
    }

    public void setChrtId(long chrtId) {
        this.chrtId = chrtId;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getWbarcode() {
        return wbarcode;
    }

    public void setWbarcode(String wbarcode) {
        this.wbarcode = wbarcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(String packageSize) {
        this.packageSize = packageSize;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public long getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(long productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getStickerScan() {
        return stickerScan;
    }

    public void setStickerScan(String stickerScan) {
        this.stickerScan = stickerScan;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber=" + orderNumber +
                ", chrtId=" + chrtId +
                ", sellerCode='" + sellerCode + '\'' +
                ", wbarcode='" + wbarcode + '\'' +
                ", productName='" + productName + '\'' +
                ", packageSize='" + packageSize + '\'' +
                ", weight='" + weight + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", brand='" + brand + '\'' +
                ", productBarcode=" + productBarcode +
                ", sticker='" + sticker + '\'' +
                ", stickerScan='" + stickerScan + '\'' +
                '}';
    }
}
