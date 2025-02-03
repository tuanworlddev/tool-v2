package com.dev.tool;

public class QRStore {
    private int storeId;
    private String storeName;

    public QRStore() {
    }

    public QRStore(int storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
