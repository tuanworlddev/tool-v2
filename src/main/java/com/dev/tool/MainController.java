package com.dev.tool;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable {
    public TextField excel1Field;
    public TextField excel2Field;
    public TextArea codeTextArea;
    public Text statusVerifyField;
    private List<QRStore> qrStores;

    public VBox qrContainer;
    public AnchorPane rootPane;

    public void onAddQRItem(ActionEvent actionEvent) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Thêm QR");
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        TextField idField = new TextField();
        idField.setPromptText("Nhập tên");

        GridPane gridPane = new GridPane();
        gridPane.setPrefSize(300, 50);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Tên:"), 0, 0);
        gridPane.add(idField, 1, 0);
        GridPane.setHgrow(idField, Priority.ALWAYS);

        dialog.getDialogPane().setContent(gridPane);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return idField.getText();
            }
            return null;
        });
        dialog.showAndWait().ifPresent(storeName -> {
            if (!storeName.isEmpty()) {
                try {
                    QRStore qrStore = QRStoreService.createQRStore(storeName);
                    addQRCard(qrStore.getStoreId(), qrStore.getStoreName(), 0);
                } catch (Exception e) {
                    notification("Lỗi", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    public void addQRCard(int storeId, String storeName, int size) {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("qr-card.fxml"));
            loader.load();
            QRCardController controller = loader.getController();
            controller.storeIdField.setText(String.valueOf(storeId));
            controller.storeNameField.setText(storeName);
            controller.sizeField.setText(String.valueOf(size));
            controller.setQRContainer(qrContainer);
            qrContainer.getChildren().add(loader.getRoot());
            qrContainer.layout();
        } catch (IOException e) {
            notification("Lỗi", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void notification(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        QRStoreService.createTable();
        refreshQRCard();
        checkAuthInit();
    }

    private void checkAuthInit() {
        List<String> keys = AuthService.getAllKeys();
        for (String key : keys) {
            if (key.equals("HongRancho")) {
                AuthController.getInstance().isLoggedIn = true;
                statusVerifyField.setText("Trạng thái: đã xác thực");
                statusVerifyField.setFill(Color.GREEN);
                break;
            }
        }
    }

    private void refreshQRCard() {
        qrContainer.getChildren().clear();
        qrStores = QRStoreService.getAllStores();
        for (QRStore qrStore : qrStores) {
            int count = QRCodeService.getCountQRCodeByStoreId(qrStore.getStoreId());
            addQRCard(qrStore.getStoreId(), qrStore.getStoreName(), count);
        }
    }

    public void onImportExcel1(ActionEvent actionEvent) {
        File excel1 = FileChooserService.excelFileChooser().showOpenDialog(rootPane.getScene().getWindow());
        if (excel1 != null) {
            excel1Field.setText(excel1.getAbsolutePath());
            FileChooserService.excelFileChooser().setInitialDirectory(excel1.getParentFile());
        }
    }

    public void onImportExcel2(ActionEvent actionEvent) {
        File excel2 = FileChooserService.excelFileChooser().showOpenDialog(rootPane.getScene().getWindow());
        if (excel2 != null) {
            excel2Field.setText(excel2.getAbsolutePath());
            FileChooserService.excelFileChooser().setInitialDirectory(excel2.getParentFile());
        }
    }

    public void onExport(ActionEvent actionEvent) {
        if (!AuthController.getInstance().isLoggedIn) {
            showLoginPop();
        } else {
            if (!excel1Field.getText().isEmpty() || !excel2Field.getText().isEmpty()) {
                Task<Void> exportTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            updateMessage("Đọc dữ liệu từ file Excel...");
                            List<Order> orders = ExportService.readExcel1(excel1Field.getText());
                            List<Product> products = ExportService.readExcel2(excel2Field.getText());
                            List<String> qrCodes = new ArrayList<>();

                            if (!codeTextArea.getText().isBlank()) {
                                updateMessage("Xử lý QR Codes...");
                                Pattern pattern = Pattern.compile("(\\d+):(\\d+)-(\\d+)");
                                String[] commands = codeTextArea.getText().split("\n");
                                for (String command : commands) {
                                    Matcher matcher = pattern.matcher(command);
                                    if (matcher.matches()) {
                                        int id = Integer.parseInt(matcher.group(1));
                                        int from = Integer.parseInt(matcher.group(2));
                                        int to = Integer.parseInt(matcher.group(3));
                                        List<String> current = QRCodeService.getQRCodesByStoreId(id, to - from + 1);
                                        qrCodes.addAll(current);
                                    }
                                }
                            }

                            // Sử dụng Platform.runLater để hiển thị hộp thoại lưu file
                            final File[] fileExport1 = new File[1];  // Lưu giá trị file để sử dụng sau
                            Platform.runLater(() -> {
                                FileChooser fileChooser = FileChooserService.csvFileChooser();
                                fileExport1[0] = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
                                synchronized (fileExport1) {
                                    fileExport1.notify();  // Đánh thức thread đang chờ
                                }
                            });

                            synchronized (fileExport1) {
                                fileExport1.wait();  // Đợi cho đến khi hộp thoại lưu file được xử lý xong
                            }

                            if (fileExport1[0] != null) {
                                updateMessage("Xuất dữ liệu...");
                                Object[][] export1Data = ExportService.export1(orders, products, qrCodes);
                                Object[][] export2Data = ExportService.export2(products, qrCodes);

                                int lastDotIndex = fileExport1[0].getAbsolutePath().lastIndexOf(".");
                                String fileNameWithoutExtension = (lastDotIndex != -1)
                                        ? fileExport1[0].getAbsolutePath().substring(0, lastDotIndex)
                                        : fileExport1[0].getAbsolutePath();
                                String fileExtension = (lastDotIndex != -1)
                                        ? fileExport1[0].getAbsolutePath().substring(lastDotIndex)
                                        : ".csv";

                                String fileExport2 = fileNameWithoutExtension + "_1" + fileExtension;

                                CSVService.writeCSV(fileExport1[0].getAbsolutePath(), export1Data);
                                CSVService.writeCSV(fileExport2, export2Data);

                                // Mở file sau khi ghi xong (chạy trên UI Thread)
                                Platform.runLater(() -> {
                                    try {
                                        Desktop.getDesktop().open(fileExport1[0]);
                                        Desktop.getDesktop().open(new File(fileExport2));
                                        showConfirmationDialog(qrCodes);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        notification("Lỗi", e.getMessage(), Alert.AlertType.ERROR);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> notification("Lỗi", e.getMessage(), Alert.AlertType.ERROR));
                        }
                        return null;
                    }
                };

                createProgressDialog(exportTask);
                new Thread(exportTask).start();
            } else {
                notification("Lỗi", "Vui lòng chọn ít nhất một file Excel.", Alert.AlertType.ERROR);
            }
        }
    }

    private void createProgressDialog(Task<?> task) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đang xuất dữ liệu...");
        dialog.setHeaderText("Vui lòng chờ trong giây lát");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.setPrefHeight(30);
        progressBar.progressProperty().bind(task.progressProperty());

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(task.messageProperty());

        VBox dialogContent = new VBox(10, statusLabel, progressBar);
        dialogContent.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(dialogContent);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setDisable(true);

        task.setOnSucceeded(e -> {
            closeButton.setDisable(false);
            dialog.setResult(ButtonType.CLOSE);
            dialog.close();
        });

        task.setOnFailed(e -> {
            closeButton.setDisable(false);
            dialog.setResult(ButtonType.CLOSE);
            dialog.close();
        });

        dialog.show();
    }

    private void showConfirmationDialog(List<String> qrCodes) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận xóa QR Codes");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Bạn có muốn xóa các QR Codes đã xuất không?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            QRCodeService.deleteQRCodes(qrCodes);
            refreshQRCard();
            notification("Thành công", "Đã xóa " + qrCodes.size() + " QR Codes.", Alert.AlertType.INFORMATION);
        }
    }

    private void showLoginPop() {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        vBox.setSpacing(10.0);
        vBox.setPrefWidth(300.0);
        Label keyLabel = new Label("Nhập khóa:");
        vBox.getChildren().add(keyLabel);
        PasswordField keyField = new PasswordField();
        keyField.setPromptText("••••••••••");
        vBox.getChildren().add(keyField);
        CheckBox rememberCheckBox = new CheckBox("Lưu lại");
        vBox.getChildren().add(rememberCheckBox);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().setContent(vBox);
        alert.setTitle("Đăng nhập");
        Objects.requireNonNull(keyField);
        Platform.runLater(keyField::requestFocus);
        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent((buttonType) -> {
            if (buttonType == ButtonType.OK) {
                String key = keyField.getText();
                boolean remember = rememberCheckBox.isSelected();
                if (key.equals("HongRancho")) {
                    AuthController.getInstance().isLoggedIn = true;
                    statusVerifyField.setText("Trạng thái: đã xác thực");
                    statusVerifyField.setFill(Color.GREEN);
                    if (remember) {
                        try {
                            AuthService.insertKey(key);
                        } catch (Exception var7) {
                            this.notification("Lỗi", var7.getMessage(), Alert.AlertType.ERROR);
                        }
                    }

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Xác thực thành công!");
                    successAlert.showAndWait();
                } else {
                    this.notification("Lỗi", "Mật khẩu không chính xác", Alert.AlertType.ERROR);
                }
            }

        });
    }
}
