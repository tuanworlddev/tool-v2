package com.dev.tool;

import com.dev.tool.SQLiteConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QRStoreService {

    public static void createTable() {
        try (Connection connection = SQLiteConnection.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS qr_stores (id INTEGER PRIMARY KEY AUTOINCREMENT, store_name TEXT NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS qr_codes (id INTEGER PRIMARY KEY AUTOINCREMENT, qr_code TEXT NOT NULL, store_id INTEGER NOT NULL, FOREIGN KEY (store_id) REFERENCES qr_stores (id) ON DELETE CASCADE)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static QRStore createQRStore(String storeName) {
        String insertSql = "INSERT INTO qr_stores (store_name) VALUES (?)";
        String selectSql = "SELECT last_insert_rowid() AS store_id";

        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertSql);
             Statement selectStmt = connection.createStatement()) {

            insertStmt.setString(1, storeName);
            insertStmt.executeUpdate();

            ResultSet rs = selectStmt.executeQuery(selectSql);
            if (rs.next()) {
                int storeId = rs.getInt("store_id");

                return new QRStore(storeId, storeName);
            }

            throw new RuntimeException("Failed to create QR Store: Unable to fetch store_id.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static List<QRStore> getAllStores() {
        List<QRStore> qrStores = new ArrayList<>();
        String sql = "SELECT * FROM qr_stores";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                qrStores.add(new QRStore(rs.getInt("id"), rs.getString("store_name")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return qrStores;
    }

    public static List<QRCode> getQRCodesByStore(int storeId) {
        List<QRCode> qrCodes = new ArrayList<>();
        String sql = "SELECT qr_code FROM qr_codes WHERE store_id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                qrCodes.add(new QRCode(rs.getInt("id"), storeId, rs.getString("qr_code")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return qrCodes;
    }

    public static boolean deleteQRStore(int storeId) {
        String sql = "DELETE FROM qr_stores WHERE id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete QR Store with ID: " + storeId, e);
        }
    }

}
