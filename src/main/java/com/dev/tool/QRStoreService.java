package com.dev.tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QRStoreService {

    public static void createTable() {
        try (Connection connection = SQLiteConnection.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS qr_stores (
                    id TEXT PRIMARY KEY,
                    store_name TEXT NOT NULL
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS qr_codes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    qr_code TEXT NOT NULL,
                    store_id TEXT NOT NULL,
                    FOREIGN KEY (store_id) REFERENCES qr_stores(id)
                        ON DELETE CASCADE
                        ON UPDATE CASCADE
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS auth (
                    id TEXT PRIMARY KEY
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo bảng: " + e.getMessage(), e);
        }
    }

    public static QRStore createQRStore(String storeId, String storeName) {
        String insertSql = "INSERT INTO qr_stores (id, store_name) VALUES (?, ?)";
        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

            insertStmt.setString(1, storeId);
            insertStmt.setString(2, storeName);
            insertStmt.executeUpdate();

            return new QRStore(storeId, storeName);
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo cửa hàng: " + e.getMessage(), e);
        }
    }

    public static boolean updateStore(String oldStoreId, String newStoreId, String newStoreName) {
        String sql = "UPDATE qr_stores SET id = ?, store_name = ? WHERE id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStoreId);
            pstmt.setString(2, newStoreName);
            pstmt.setString(3, oldStoreId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            throw new RuntimeException("Không thể cập nhật mã cửa hàng từ " + oldStoreId + " sang " + newStoreId, e);
        }
    }

    public static List<QRStore> getAllStores() {
        List<QRStore> qrStores = new ArrayList<>();
        String sql = "SELECT * FROM qr_stores";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                qrStores.add(new QRStore(rs.getString("id"), rs.getString("store_name")));
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy danh sách cửa hàng: " + e.getMessage(), e);
        }
        return qrStores;
    }

    public static boolean deleteQRStore(String storeId) {
        String sql = "DELETE FROM qr_stores WHERE id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, storeId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa cửa hàng: " + e.getMessage(), e);
        }
    }
}
