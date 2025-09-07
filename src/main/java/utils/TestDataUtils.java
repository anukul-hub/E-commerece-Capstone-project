package utils;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestDataUtils {
    private static final List<String> DEFAULT_PRODUCTS = List.of("mosquito all out", "Mobile", "power bank");

    public static void createDefaultExcel(Path excelPath) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0, CellType.STRING).setCellValue("Product");
            for (int i = 0; i < DEFAULT_PRODUCTS.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0, CellType.STRING).setCellValue(DEFAULT_PRODUCTS.get(i));
            }
            sheet.autoSizeColumn(0);
            Files.createDirectories(excelPath.getParent());
            try (OutputStream os = Files.newOutputStream(excelPath)) {
                wb.write(os);
            }
        }
    }

    public static boolean hasDataInFirstColumn(ExcelUtils excel) {
        int total = excel.getRowCount();
        for (int r = 1; r < total; r++) {
            String val = excel.getCellData(r, 0);
            if (val != null && !val.trim().isEmpty()) return true;
        }
        return false;
    }
}
