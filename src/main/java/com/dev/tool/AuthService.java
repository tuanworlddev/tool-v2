package com.dev.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    public static void insertKey(String key) {
        String sql = "INSERT INTO auth (id) VALUES (?)";
        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteKey(String key) {
        String sql = "DELETE FROM auth WHERE id = ?";
        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deleted key: " + key);
            } else {
                System.out.println("Key not found: " + key);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting key", e);
        }
    }

    public static List<String> getAllKeys() {
        String sql = "SELECT id FROM auth";
        List<String> keys = new ArrayList<>();
        try (Connection connection = SQLiteConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                keys.add(rs.getString("id"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving keys", e);
        }
        return keys;
    }
}
