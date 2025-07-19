package com.dev.tool;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class QRCardController implements Initializable {
    public Label storeIdField;
    public Label sizeField;
    public Label storeNameField;
    public HBox cardPane;
    private VBox qrContainer;

    public void setQRContainer(VBox qrContainer) {
        this.qrContainer = qrContainer;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void onEditStoreId(ActionEvent actionEvent) {
        String currentStoreId = storeIdField.getText();
        String currentStoreName = storeNameField.getText();

        if (currentStoreId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã cửa hàng hiện tại đang trống.");
            return;
        }

        // Tạo dialog tùy chỉnh cho cả ID và Tên
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa Cửa Hàng");
        dialog.setHeaderText("Nhập mã và tên cửa hàng mới");

        ButtonType updateButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField(currentStoreId);
        TextField nameField = new TextField(currentStoreName);

        grid.add(new Label("Mã cửa hàng mới:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Tên cửa hàng mới:"), 0, 1);
        grid.add(nameField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(idField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new Pair<>(idField.getText().trim(), nameField.getText().trim());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String newId = pair.getKey();
            String newName = pair.getValue();

            if (newId.isEmpty() || newName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã hoặc tên cửa hàng không được để trống.");
                return;
            }

            try {
                boolean updated = QRStoreService.updateStore(currentStoreId, newId, newName);
                if (updated) {
                    storeIdField.setText(newId);
                    storeNameField.setText(newName);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thất bại. Có thể mã cửa hàng đã tồn tại.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi cập nhật: " + e.getMessage());
            }
        });
    }

    public void onImportQRCode(ActionEvent actionEvent) {
        File pdfFileSelected = FileChooserService.pdfFileChooser(cardPane.getScene());
        if (pdfFileSelected != null && pdfFileSelected.exists()) {
            Dialog<Void> loadingDialog = new Dialog<>();
            loadingDialog.setTitle("Đang quét mã QR...");
            BorderPane loadingDialogPane = new BorderPane();
            loadingDialogPane.setPrefSize(250, 50);
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefSize(200, 30);
            loadingDialogPane.setCenter(progressBar);
            loadingDialog.getDialogPane().setContent(loadingDialogPane);
            loadingDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            loadingDialog.initModality(Modality.APPLICATION_MODAL);

            Task<List<String>> task = getListTask(pdfFileSelected, loadingDialog);

            new Thread(task).start();
            loadingDialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.CANCEL) {
                    task.cancel();
                }
                return null;
            });
            loadingDialog.showAndWait();
        }
    }

    private Task<List<String>> getListTask(File pdfFileSelected, Dialog<Void> loadingDialog) {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return QRScannerService.scannerQRCode(pdfFileSelected);
            }
        };

        task.setOnSucceeded(event -> {
            loadingDialog.close();
            List<String> qrList = task.getValue();
            if (qrList != null && !qrList.isEmpty()) {
                QRCodeService.insertQRCodeList(qrList, storeIdField.getText());
                int count = QRCodeService.getCountQRCodeByStoreId(storeIdField.getText());
                sizeField.setText(String.valueOf(count));
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi không tìm thấy mã QR", "Vui lòng kiểm tra lại file PDF.");
            }
        });

        task.setOnFailed(event -> {
            loadingDialog.close();
            Throwable exception = task.getException();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Error scanning QR codes: " + exception.getMessage());
            exception.printStackTrace();
        });
        return task;
    }

    public void onDeleteStore(ActionEvent actionEvent) {
        if (storeIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Store ID is empty.");
            return;
        }

        String storeId = storeIdField.getText();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Delete");
        confirmationAlert.setHeaderText("Delete Store by id: " + storeId + " and name: " + storeNameField.getText());
        confirmationAlert.setContentText("Are you sure you want to delete this store? All associated QR codes will also be removed.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean isDeleted = QRStoreService.deleteQRStore(storeId);
                if (isDeleted) {
                    qrContainer.getChildren().remove(cardPane);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the store. It may not exist, or there was an error with the database.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String description) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(description);
        alert.show();
    }
}