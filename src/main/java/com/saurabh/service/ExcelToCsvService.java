package com.saurabh.service;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class ExcelToCsvService {
    public String convertExcelToCSV(MultipartFile file, String saveDirectory) {
        if (!file.isEmpty()) {
            try (InputStream is = file.getInputStream()) {
                Workbook workbook;
                if (file.getOriginalFilename().endsWith(".xls")) {
                    workbook = new HSSFWorkbook(is);
                } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(is);
                } else {
                    throw new IllegalArgumentException("Unsupported Excel file format.");
                }

                // Assuming there is only one sheet
                Sheet sheet = workbook.getSheetAt(0);

                List<Map<String, String>> jsonData = new ArrayList<>();
                List<String> headers = new ArrayList<>();
                boolean isFirstRow = true;

                for (Row row : sheet) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    Map<String, String> rowMap = new LinkedHashMap<>();
                    boolean isRowValid = false; // Initialize as false

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue;

                        if (isFirstRow) {
                            headers.add(sanitizeCellValue(cell.getStringCellValue()));
                        } else {
                            switch (cell.getCellType()) {
                                case STRING:
                                    cellValue = sanitizeCellValue(cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    cellValue = String.valueOf(cell.getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    cellValue = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                case BLANK:
                                    cellValue = "";
                                    break;
                                default:
                                    cellValue = "";
                            }
                            if (!cellValue.isEmpty()) {
                                isRowValid = true; // At least one cell has data
                            }

                            String header = headers.get(cell.getColumnIndex());
                            rowMap.put(header, cellValue);
                        }
                    }

                    if (!isFirstRow && isRowValid) {
                        log.info("->>>"+rowMap);
                        jsonData.add(rowMap);
                    }
                    isFirstRow = false;
                }

                // Sanitize the headers before saving
                List<String> sanitizedHeaders = List.of(sanitizeHeader(headers.toArray(new String[0])));

                // Create a unique CSV file for each sheet based on the sheet name
                String sheetName = sheet.getSheetName();
                String csvFileName = sanitizeSheetName(sheetName) + ".csv";
                String csvFilePath = saveDirectory + File.separator + csvFileName;

                // Only save the data if there's valid data to save
                if (!jsonData.isEmpty()) {
                    saveCSVFile(csvFilePath, sanitizedHeaders, jsonData);
                }

                return "EXCEL FILE SUCCESSFULLY CONVERTED INTO CSV";
            } catch (IOException e) {
                e.printStackTrace();
                return "ERROR CONVERTING EXCEL TO CSV AND SAVING: " + e.getMessage();
            }
        } else {
            return "PLEASE PROVIDE AN EXCEL SHEET";
        }
    }

    private void saveCSVFile(String filePath, List<String> headers, List<Map<String, String>> data) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();

            if (!parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    log.info("Directory created: " + parentDir.getAbsolutePath());
                } else {
                    throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
                }
            }
            try (FileWriter fileWriter = new FileWriter(file); CSVWriter csvWriter = new CSVWriter(fileWriter)) {
                String[] headerArray = headers.toArray(new String[0]);
                csvWriter.writeNext(headerArray);

                for (Map<String, String> row : data) {
                    String[] rowData = new String[headerArray.length];
                    for (int i = 0; i < headerArray.length; i++) {
                        rowData[i] = row.get(headerArray[i]);
                    }
                    csvWriter.writeNext(rowData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving CSV file: " + e.getMessage());
        }
    }

    private String sanitizeCellValue(String cellValue) {
        cellValue = cellValue.replaceAll("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
        boolean cellValueFlag = containsHiddenCharacters(cellValue);
        log.info("---------------->" + cellValueFlag);
        return cellValue;
    }

    private String[] sanitizeHeader(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].replaceAll("[^\\p{Print}]", "");
        }
        return headers;
    }

    private boolean containsHiddenCharacters(String value) {
        return value.matches(".*[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}].*");
    }

    private String sanitizeSheetName(String sheetName) {
        return sheetName.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
