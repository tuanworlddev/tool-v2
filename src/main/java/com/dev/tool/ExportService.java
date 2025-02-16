package com.dev.tool;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportService {
    private static final DataFormatter dataFormatter = new DataFormatter();

    public static List<Order> readExcel1(String filePath) {
        List<Order> orders = new ArrayList<>();
        int currentRow = 1;
        DataFormatter dataFormatter = new DataFormatter(); // Dùng để lấy giá trị dưới dạng String

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);

            for (currentRow = 1; currentRow <= sheet.getLastRowNum(); currentRow++) {
                Row row = sheet.getRow(currentRow);
                if (row != null) {
                    Order order = new Order();

                    // Xử lý Order Number (Cột 2)
                    Cell cell2 = row.getCell(2);
                    order.setOrderNumber((cell2 != null && cell2.getCellType() == CellType.NUMERIC) ?
                            (long) cell2.getNumericCellValue() : 0);

                    // Xử lý Chrt ID (Cột 3)
                    Cell cell3 = row.getCell(3);
                    order.setChrtId((cell3 != null && cell3.getCellType() == CellType.NUMERIC) ?
                            (long) cell3.getNumericCellValue() : 0);

                    // Xử lý Seller Code (Cột 4)
                    Cell cell4 = row.getCell(4);
                    order.setSellerCode((cell4 != null) ? dataFormatter.formatCellValue(cell4) : "");

                    order.setWbarcode("");

                    // Xử lý Product Name (Cột 6)
                    Cell cell6 = row.getCell(6);
                    order.setProductName((cell6 != null) ? dataFormatter.formatCellValue(cell6) : "");

                    // Xử lý Package Size (Cột 7)
                    Cell cell7 = row.getCell(7);
                    order.setPackageSize((cell7 != null) ? dataFormatter.formatCellValue(cell7) : "");

                    order.setWeight("");

                    // Xử lý Size (Cột 9)
                    Cell cell9 = row.getCell(9);
                    order.setSize((cell9 != null) ? dataFormatter.formatCellValue(cell9) : "");

                    // Xử lý Color (Cột 10)
                    Cell cell10 = row.getCell(10);
                    order.setColor((cell10 != null) ? dataFormatter.formatCellValue(cell10) : "");

                    // Xử lý Brand (Cột 11)
                    Cell cell11 = row.getCell(11);
                    order.setBrand((cell11 != null) ? dataFormatter.formatCellValue(cell11) : "");

                    // Xử lý Product Barcode (Cột 12)
                    Cell cell12 = row.getCell(12);
                    order.setProductBarcode((cell12 != null && cell12.getCellType() == CellType.NUMERIC) ?
                            (long) cell12.getNumericCellValue() : 0);

                    // Xử lý Sticker (Cột 13)
                    Cell cell13 = row.getCell(13);
                    order.setSticker((cell13 != null) ? dataFormatter.formatCellValue(cell13) : "");

                    // Xử lý Sticker Scan (Cột 14)
                    Cell cell14 = row.getCell(14);
                    order.setStickerScan((cell14 != null) ? dataFormatter.formatCellValue(cell14) : "");

                    System.out.println("Row: " + currentRow);
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " at row " + (currentRow - 1));
        }
        return orders;
    }

    public static List<Product> readExcel2(String filePath) {
        List<Product> products = new ArrayList<>();
        int currentRow = 1;
        DataFormatter dataFormatter = new DataFormatter(); // Định dạng giá trị ô

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);

            for (currentRow = 1; currentRow <= sheet.getLastRowNum(); currentRow++) {
                Row row = sheet.getRow(currentRow);
                if (row != null) {
                    Product product = new Product();

                    Cell cell1 = row.getCell(1);
                    String taskNumberStr = (cell1 != null) ? dataFormatter.formatCellValue(cell1).trim() : "0";
                    product.setTaskNumber(!taskNumberStr.isEmpty() ? Long.parseLong(taskNumberStr) : 0);

                    product.setPhoto("");

                    // Xử lý Brand (Cột 3)
                    Cell cell3 = row.getCell(3);
                    product.setBrand((cell3 != null) ? dataFormatter.formatCellValue(cell3) : "");

                    // Xử lý Name (Cột 4)
                    Cell cell4 = row.getCell(4);
                    product.setName((cell4 != null) ? dataFormatter.formatCellValue(cell4) : "");

                    // Xử lý Size (Cột 5)
                    Cell cell5 = row.getCell(5);
                    product.setSize((cell5 != null) ? dataFormatter.formatCellValue(cell5) : "");

                    // Xử lý Color (Cột 6)
                    Cell cell6 = row.getCell(6);
                    product.setColor((cell6 != null) ? dataFormatter.formatCellValue(cell6) : "");

                    // Xử lý Seller Code (Cột 7)
                    Cell cell7 = row.getCell(7);
                    product.setSellerCode((cell7 != null) ? dataFormatter.formatCellValue(cell7) : "");

                    // Xử lý Sticker (Cột 8)
                    Cell cell8 = row.getCell(8);
                    product.setSticker((cell8 != null) ? dataFormatter.formatCellValue(cell8) : "");

                    // Xử lý Barcode (Cột 9)
                    Cell cell9 = row.getCell(9);
                    product.setBarcode((cell9 != null) ? dataFormatter.formatCellValue(cell9) : "");

                    products.add(product);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " at row " + (currentRow - 1));
        }
        return products;
    }


    public static Object[][] export1(List<Order> orders, List<Product> products, List<String> qrCodes) throws Exception {
        Object[][] result = new Object[products.size() + 1][10];
        result[0] = new Object[]{"Стикер № 1", "Стикер № 2", "QR-Код", "Артикул-цвет", "Размер", "Баркод", "Бренд", "Продавец", "QR-Ч-Знак", "Предмет"};

        Map<String, Order> orderMap = orders.stream()
                .collect(Collectors.toMap(Order::getSticker, order -> order));

        int index = 1;
        int qrIndex = 0;
        for (Product product : products) {
            if (orderMap.containsKey(product.getSticker())) {
                Order order = orderMap.get(product.getSticker());

                String[] stickers = product.getSticker().split(" ");
                String[] designations = product.getName().split(" ");

                String cell0Value = stickers.length > 0 ? stickers[0] : "";
                String cell1Value = stickers.length > 1 ? stickers[1] : "";
                String cell2Value = order.getStickerScan();
                String cell3Value = product.getSellerCode();
                String cell4Value = product.getSize();
                String cell5Value = product.getBarcode();
                String cell6Value = product.getBrand();
                String cell7Value = "ИП ДИНЬ ТХИ МАЙ";
                String cell8Value = qrIndex < qrCodes.size() ? qrCodes.get(qrIndex) : "";
                String cell9Value = designations.length >= 2 ? designations[0] + " " + designations[1] : designations[0];
                result[index++] = new Object[]{
                        cell0Value, cell1Value, cell2Value, cell3Value, cell4Value, cell5Value, cell6Value, cell7Value, cell8Value, cell9Value
                };
                qrIndex++;
            }
        }
        return result;
    }

    public static Object[][] export2(List<Product> products, List<String> qrCodes) throws Exception {
        Object[][] result = new Object[products.size() + 1][3];
        result[0] = new Object[]{"№ задания", "Стикер", "КИЗ"};

        for (int i = 0; i < products.size(); i++) {
            long cell0 = products.get(i).getTaskNumber();
            String cell1 = products.get(i).getSticker().replace(" ", "");
            String qr = i < qrCodes.size() ? qrCodes.get(i) : "";
            String cell2 = qr.length() > 32 ? qr.substring(1, 32) : qr;
            result[i + 1] = new Object[]{cell0, cell1, cell2};
        }
        return result;
    }
}
