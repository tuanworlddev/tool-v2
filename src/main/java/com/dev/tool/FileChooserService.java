package com.dev.tool;

import javafx.scene.Scene;
import javafx.stage.FileChooser;

import java.io.File;

public class FileChooserService {
    private static FileChooser pdfFileChooser;
    private static FileChooser excelFileChooser;
    private static FileChooser csvFileChooser;

    public static File pdfFileChooser(Scene scene) {
        if (pdfFileChooser == null) {
            pdfFileChooser = new FileChooser();
            pdfFileChooser.setTitle("Open PDF File");
            pdfFileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
        }
        File file = pdfFileChooser.showOpenDialog(scene.getWindow());
        if (file != null) {
            pdfFileChooser.setInitialDirectory(file.getParentFile());
        }
        return file;
    }

    public static FileChooser excelFileChooser() {
        if (excelFileChooser == null) {
            excelFileChooser = new FileChooser();
            excelFileChooser.setTitle("Open Excel File");
            excelFileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
        }
        return excelFileChooser;
    }

    public static FileChooser csvFileChooser() {
        if (csvFileChooser == null) {
            csvFileChooser = new FileChooser();
            csvFileChooser.setTitle("Save CSV File");
            csvFileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
        }
        return csvFileChooser;
    }

}
