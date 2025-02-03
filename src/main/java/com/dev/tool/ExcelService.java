package com.dev.tool;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelService {
    public static void writeExcel(String fileName, Object[][] data) {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Sheet1");

            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);

                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    Object value = data[i][j];

                    switch (value) {
                        case String s -> cell.setCellValue(s);
                        case Integer integer -> cell.setCellValue(integer);
                        case Double v -> cell.setCellValue(v);
                        case Boolean b -> cell.setCellValue(b);
                        case Long l -> cell.setCellValue(l);
                        case null -> cell.setCellValue("");
                        default -> cell.setCellValue(value.toString());
                    }
                }
            }

            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
