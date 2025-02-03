package com.dev.tool;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportService {
    private static final DataFormatter dataFormatter = new DataFormatter();

    public static List<Order> readExcel1(String filePath) throws Exception {
        List<Order> orders = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Order order = new Order();
                    order.setOrderNumber(row.getCell(2) != null ? (long)row.getCell(2).getNumericCellValue() : 0);
                    order.setChrtId(row.getCell(3) != null ? (long)row.getCell(3).getNumericCellValue() : 0);
                    order.setSellerCode(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "");
                    order.setWbarcode("");
                    order.setProductName(row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "");
                    order.setPackageSize(row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "");
                    order.setWeight("");
                    order.setSize(row.getCell(9) != null ? dataFormatter.formatCellValue(row.getCell(9)) : "");
                    order.setColor(row.getCell(10) != null ? row.getCell(10).getStringCellValue() : "");
                    order.setBrand(row.getCell(11) != null ? row.getCell(11).getStringCellValue() : "");
                    order.setProductBarcode(row.getCell(12) != null ? (long)row.getCell(12).getNumericCellValue() : 0);
                    order.setSticker(row.getCell(13) != null ? row.getCell(13).getStringCellValue() : "");
                    order.setStickerScan(row.getCell(14) != null ? row.getCell(14).getStringCellValue() : "");

                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public static List<Product> readExcel2(String filePath) throws Exception {
        List<Product> products = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Product product = new Product();
                    product.setTaskNumber(row.getCell(1) != null ? Long.parseLong(dataFormatter.formatCellValue(row.getCell(1))) : 0);
                    product.setPhoto("");
                    product.setBrand(row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "");
                    product.setName(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "");
                    product.setSize(row.getCell(5) != null ? dataFormatter.formatCellValue(row.getCell(5)) : "");
                    product.setColor(row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "");
                    product.setSellerCode(row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "");
                    product.setSticker(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "");
                    product.setBarcode(row.getCell(9) != null ? dataFormatter.formatCellValue(row.getCell(9)) : "");

                    products.add(product);
                }
            }
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
            String cell2 = qr.length() > 32 ? qr.substring(0, 32) : qr;
            result[i + 1] = new Object[]{cell0, cell1, cell2};
        }
        return result;
    }
}
