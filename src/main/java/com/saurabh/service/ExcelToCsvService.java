package com.saurabh.service;

import com.saurabh.DTO.ColumnMapping;
import com.saurabh.DTO.ColumnMappingRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class ExcelToCsvService {
    public String convertExcelToCSV(MultipartFile file, String saveDirectory, ColumnMappingRequest columnMappingRequest) {
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
                Sheet sheet = workbook.getSheetAt(0);
                Map<String, String> columnMapping = new HashMap<>();
                for (ColumnMapping mapping : columnMappingRequest.getColumnMapping()) {
                    columnMapping.put(mapping.getInputColumnName(), mapping.getOutputColumnName());
                }

                List<Map<String, String>> jsonData = new ArrayList<>();
                List<String> headers = new ArrayList<>();
                List<String> desiredHeaders = new ArrayList<>();
                boolean isFirstRow = true;

                for (Row row : sheet) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    Map<String, String> rowMap = new LinkedHashMap<>();
                    boolean isRowValid = false;

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue;

                        if (isFirstRow) {
                            headers.add(new SanitizationFileContant().sanitizeCellValue(cell.getStringCellValue()));
                        } else {
                            String header = headers.get(cell.getColumnIndex());

                            switch (cell.getCellType()) {
                                case STRING:
                                    cellValue = new SanitizationFileContant().sanitizeCellValue(cell.getStringCellValue());
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
                                isRowValid = true;
                            }
                            if (columnMapping.containsKey(header)) {
                                String outputHeader = columnMapping.get(header);
                                desiredHeaders.add(outputHeader);
                                rowMap.put(outputHeader, cellValue);
                            }
                        }
                    }
                    if (!isFirstRow && isRowValid) {
                        log.info("->>>" + rowMap);
                        jsonData.add(rowMap);
                    }
                    isFirstRow = false;
                }
                List<String> sanitizedHeaders = List.of(new SanitizationFileContant().sanitizeHeader(desiredHeaders.toArray(new String[0])));

                // Create a unique CSV file for each sheet based on the sheet name
                String sheetName = sheet.getSheetName();
                String csvFileName = new SanitizationFileContant().sanitizeSheetName(sheetName) + ".csv";
                String csvFilePath = saveDirectory + File.separator + csvFileName;

                // Only save the data if there's valid data to save
                if (!jsonData.isEmpty()) {
                    new FileUtilOperation().saveCSVFile(csvFilePath, sanitizedHeaders, jsonData);
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
}
