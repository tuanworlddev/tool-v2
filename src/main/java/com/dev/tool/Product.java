package com.dev.tool;

public class Product {
    private long taskNumber;      // № задания
    private String photo;        // Фото
    private String brand;        // Бренд
    private String name;         // Наименование
    private String size;         // Размер
    private String color;        // Цвет
    private String sellerCode;   // Артикул продавца
    private String sticker;      // Стикер
    private String barcode;      // Баркод

    public Product() {
    }

    public Product(long taskNumber, String photo, String brand, String name, String size, String color, String sellerCode, String sticker, String barcode) {
        this.taskNumber = taskNumber;
        this.photo = photo;
        this.brand = brand;
        this.name = name;
        this.size = size;
        this.color = color;
        this.sellerCode = sellerCode;
        this.sticker = sticker;
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(long taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String toString() {
        return "Product{" +
                "taskNumber='" + taskNumber + '\'' +
                ", photo='" + photo + '\'' +
                ", brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", sellerCode='" + sellerCode + '\'' +
                ", sticker='" + sticker + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
