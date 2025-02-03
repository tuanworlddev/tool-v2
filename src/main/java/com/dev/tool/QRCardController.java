package com.dev.tool;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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

            // Bắt đầu Task và hiển thị dialog
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
                QRCodeService.insertQRCodeList(qrList, Integer.parseInt(storeIdField.getText()));
                int count = QRCodeService.getCountQRCodeByStoreId(Integer.parseInt(storeIdField.getText()));
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
            return;
        }

        int storeId = Integer.parseInt(storeIdField.getText());

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
