package com.saurabh.service;

import com.opencsv.CSVWriter;
import com.saurabh.DTO.ColumnMapping;
import com.saurabh.DTO.ColumnMappingRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FileUtilOperation {

    public void saveCSVFile(String filePath, List<String> headers, List<Map<String, String>> data) {
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


    public Map<String, String> getColumnMapping(ColumnMappingRequest columnMappingRequest) {
        Map<String, String> columnMapping = new HashMap<>();
        List<ColumnMapping> mappings = columnMappingRequest.getColumnMapping();
        for (ColumnMapping mapping : mappings) {
            columnMapping.put(mapping.getInputColumnName(), mapping.getOutputColumnName());
        }
        return columnMapping;
    }
}
