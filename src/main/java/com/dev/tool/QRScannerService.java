package com.dev.tool;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QRScannerService {
    private static Dialog<Void> dialog;

    public static List<String> scannerQRCode(File pdfFile) throws IOException, ChecksumException, NotFoundException, FormatException {
        List<String> qrCodesList = new ArrayList<>();
        try (PDDocument pdfDocument = Loader.loadPDF(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);

            int pageCount = pdfDocument.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300); // 300 DPI for higher resolution
                String qrCodeData = scanQRCodeImage(bufferedImage);
                if (qrCodeData != null) {
                    qrCodesList.add(qrCodeData);
                }
            }
        }
        return qrCodesList;
    }

    public static void scannerQRCode(File pdfFile, TableView<String> tableView) {

        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                List<String> result = new ArrayList<>();
                PDDocument pdfDocument = Loader.loadPDF(pdfFile);
                PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
                int pageCount = pdfDocument.getNumberOfPages();

                for (int i = 0; i < pageCount; i++) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                    }
                    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300);
                    String qrCodeData = scanQRCodeImage(bufferedImage);
                    if (qrCodeData != null) {
                        result.add(qrCodeData);
                    }
                    updateProgress(i + 1, pageCount);
                    int pagesScanned = i + 1;
                    Platform.runLater(() -> dialog.setHeaderText("Scanning QR Code (" + pagesScanned + "/" + pageCount + ")"));
                }
                pdfDocument.close();
                return result;
            }
        };
        createProgressDialog(task);
        task.setOnSucceeded(e -> {
            dialog.close();
            ObservableList<String> data = FXCollections.observableArrayList(task.getValue());
            tableView.setItems(data);
        });
        task.setOnFailed(e -> {
            dialog.close();
            Throwable exception = task.getException();
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
            alert.showAndWait();
        });

        dialog.setOnCloseRequest(e -> {
            task.cancel();
        });
        dialog.show();

        new Thread(task).start();
    }

    private static String scanQRCodeImage(BufferedImage bufferedImage) throws ChecksumException, NotFoundException, FormatException {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        String result = decodeQRCode(bufferedImage);
        if (result != null) {
            return result;
        }

        int horizontal = 2;
        for (int i = 0; i < horizontal; i++) {
            int startX = i * (width / horizontal);
            int regionWidth = (i == horizontal - 1) ? (width - startX) : (width / horizontal);
            BufferedImage region = bufferedImage.getSubimage(startX, 0, regionWidth, height);
            String result1 = decodeQRCode(region);
            if (result1 != null) {
                return result1;
            }
        }

        for (int i = 0; i < horizontal; i++) {
            int startY = i * (height / horizontal);
            int regionHeight = (i == horizontal - 1) ? (height - startY) : (height / horizontal);
            BufferedImage region = bufferedImage.getSubimage(0, startY, width, regionHeight);
            String result2 = decodeQRCode(region);
            if (result2 != null) {
                return result2;
            }
        }

        int numRegions = 4;
        for (int i = 0; i < numRegions; i++) {
            for (int j = 0; j < numRegions; j++) {
                int startX = i * (width / numRegions);
                int startY = j * (height / numRegions);
                int regionWidth = (i == numRegions - 1) ? (width - startX) : (width / numRegions);
                int regionHeight = (j == numRegions - 1) ? (height - startY) : (height / numRegions);

                BufferedImage region = bufferedImage.getSubimage(startX, startY, regionWidth, regionHeight);
                String result3 = decodeQRCode(region);
                if (result3 != null) {
                    return result3;
                }
            }
        }

        return null;
    }

    private static String decodeQRCode(BufferedImage bufferedImage) throws ChecksumException, NotFoundException, FormatException {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        Result result = reader.decode(binaryBitmap);
        return result.getText();
    }

    private static void createProgressDialog(Task<?> task) {
        dialog = new Dialog<>();
        dialog.setTitle("QR Code Scanner");
        dialog.setHeaderText("Scanning QR Code...");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.setPrefHeight(30);
        progressBar.progressProperty().bind(task.progressProperty());

        VBox dialogContent = new VBox(progressBar);
        dialog.getDialogPane().setContent(dialogContent);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setDisable(true);

        task.setOnSucceeded(e -> dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setDisable(false));
        task.setOnFailed(e -> dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setDisable(false));
    }
}
