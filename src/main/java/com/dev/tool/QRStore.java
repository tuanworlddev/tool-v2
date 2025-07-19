package com.dev.tool;

public class QRStore {
    private String storeId;
    private String storeName;

    public QRStore(String storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
