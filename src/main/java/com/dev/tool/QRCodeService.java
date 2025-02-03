package com.dev.tool;

import com.dev.tool.SQLiteConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QRCodeService {

    // Thêm mã QR vào bảng qr_codes
    public static boolean insertQRCode(String qrCode, int storeId) {
        String sql = "INSERT INTO qr_codes(qr_code, store_id) VALUES(?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, qrCode);
            pstmt.setInt(2, storeId);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean insertQRCodeList(List<String> qrCodes, int storeId) {
        String sql = "INSERT INTO qr_codes(qr_code, store_id) VALUES(?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Bắt đầu giao dịch
            conn.setAutoCommit(false);

            for (String qrCode : qrCodes) {
                pstmt.setString(1, qrCode);
                pstmt.setInt(2, storeId);
                pstmt.addBatch();
            }

            // Thực thi batch
            int[] rowsInserted = pstmt.executeBatch();
            conn.commit();

            // Kiểm tra nếu tất cả mã QR đều được thêm thành công
            return Arrays.stream(rowsInserted).allMatch(rows -> rows > 0);

        } catch (Exception e) {
            throw new RuntimeException("Error inserting QR codes: " + e.getMessage(), e);
        }
    }


    // Xóa mã QR từ bảng qr_codes
    public static boolean deleteQRCode(String qrCode, int storeId) {
        String sql = "DELETE FROM qr_codes WHERE qr_code = ? AND store_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, qrCode);
            pstmt.setInt(2, storeId);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isQRCodeExists(String qrCode, int storeId) {
        String sql = "SELECT COUNT(*) AS count FROM qr_codes WHERE qr_code = ? AND store_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, qrCode);
            pstmt.setInt(2, storeId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static int getCountQRCodeByStoreId(int storeId) {
        String sql = "SELECT COUNT(*) AS count FROM qr_codes WHERE store_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, storeId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getQRCodesByStoreId(int storeId, int limit) {
        String query = "SELECT qr_code FROM qr_codes WHERE store_id = ? ORDER BY id ASC LIMIT ?";
        List<String> qrCodes = new ArrayList<>();

        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                qrCodes.add(rs.getString("qr_code"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting QR codes: " + e.getMessage(), e);
        }
        return qrCodes;
    }

    public static void deleteQRCodesByStoreId(int storeId, int count) {
        String deleteQuery = "DELETE FROM qr_codes WHERE id IN " +
                "(SELECT id FROM qr_codes WHERE store_id = ? ORDER BY id ASC LIMIT ?)";

        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, count);
            int deletedRows = pstmt.executeUpdate();

            System.out.println("Deleted " + deletedRows + " QR codes for store ID: " + storeId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting QR codes: " + e.getMessage(), e);
        }
    }

    public static void deleteQRCodes(List<String> qrCodes) {
        if (qrCodes == null || qrCodes.isEmpty()) {
            System.out.println("Danh sách mã QR trống, không có gì để xóa.");
            return;
        }

        String placeholders = qrCodes.stream()
                .map(qr -> "?")
                .collect(Collectors.joining(", "));

        String sql = "DELETE FROM qr_codes WHERE qr_code IN (" + placeholders + ")";

        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < qrCodes.size(); i++) {
                pstmt.setString(i + 1, qrCodes.get(i));
            }

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Đã xóa " + affectedRows + " mã QR khỏi database.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

