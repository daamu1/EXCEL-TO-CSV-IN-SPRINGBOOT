package com.saurabh.service;

import com.opencsv.CSVWriter;
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
                List<Map<String, String>> jsonData = new ArrayList<>();
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    Iterator<Row> rowIterator = sheet.iterator();
                    List<String> headers = new ArrayList<>();
                    boolean isFirstRow = true;
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        Map<String, String> rowMap = new LinkedHashMap<>();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            String cellValue;

                            if (isFirstRow) {
                                headers.add(cell.getStringCellValue());
                            } else {
                                switch (cell.getCellType()) {
                                    case STRING:
                                        cellValue = cell.getStringCellValue();
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
                                        break;
                                }

                                String header = headers.get(cell.getColumnIndex());
                                rowMap.put(header, cellValue);
                            }
                        }

                        if (!isFirstRow) {
                            jsonData.add(rowMap);
                        }
                        isFirstRow = false;
                    }
                    String csvFileName = "ConvertedExcelToCsv.csv"; // You may want to change the CSV file name here.
                    String csvFilePath = saveDirectory + File.separator + csvFileName;
                    saveCSVFile(csvFilePath, headers, jsonData);
                }
                return "EXCEL FILE SUCCESSFULLY CONVERTED INTO CSV";
            } catch (IOException e) {
                e.printStackTrace();
                return "Error converting Excel to CSV and saving: " + e.getMessage();
            }
        } else {
            return "Please provide an Excel file.";
        }
    }

    private void saveCSVFile(String filePath, List<String> headers, List<Map<String, String>> data) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();

            if (!parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    System.out.println("Directory created: " + parentDir.getAbsolutePath());
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
}
