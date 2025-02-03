package com.dev.tool;

public class QRCode {
    private int qrId;
    private int storeId;
    private String qrCode;

    public QRCode() {
    }

    public QRCode(int qrId, int storeId, String qrCode) {
        this.qrId = qrId;
        this.storeId = storeId;
        this.qrCode = qrCode;
    }

    public int getQrId() {
        return qrId;
    }

    public void setQrId(int qrId) {
        this.qrId = qrId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
