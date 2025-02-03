package com.dev.tool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthController {
    private static AuthController instance;
    public boolean isLoggedIn = false;

    public AuthController() {
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }

        return instance;
    }

    public void writeKey(String value) throws IOException {
        Path path = Paths.get("data/auth.txt");
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.WRITE);

        try {
            writer.write(this.hashAccessKey(value));
        } catch (Throwable var7) {
            try {
                writer.close();
            } catch (Throwable var6) {
                var7.addSuppressed(var6);
            }

            throw var7;
        }

        writer.close();

    }

    public String readKey() throws IOException {
        File file = new File("data/auth.txt");
        if (!file.exists()) {
            return null;
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            return reader.readLine();
        }
    }

    private String hashAccessKey(String accessKey) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(accessKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException var4) {
            throw new RuntimeException(var4);
        }
    }

    public Boolean verifyAccessKey(String hashedString) throws IOException {
        String hashedAccessKey = this.hashAccessKey("HongRancho");
        return hashedAccessKey.equals(hashedString);
    }
}
