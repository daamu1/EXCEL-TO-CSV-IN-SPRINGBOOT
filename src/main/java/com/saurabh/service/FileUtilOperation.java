package com.saurabh.service;

import com.opencsv.CSVWriter;
import com.saurabh.DTO.ColumnMapping;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
                Set<String> uniqueHeaders = new LinkedHashSet<>(headers);
                String[] headerArray = uniqueHeaders.toArray(new String[0]);

                boolean headerWritten = false;

                for (Map<String, String> row : data) {
                    String[] rowData = new String[headerArray.length];
                    for (int i = 0; i < headerArray.length; i++) {
                        rowData[i] = row.get(headerArray[i]);
                    }
                    if (!headerWritten) {
                        csvWriter.writeNext(headerArray);
                        headerWritten = true;
                    }
                    csvWriter.writeNext(rowData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving CSV file: " + e.getMessage());
        }
    }

    public Map<String, String> getColumnMapping(List<ColumnMapping> columnMappingRequest) {
        Map<String, String> columnMapping = new HashMap<>();
        for (ColumnMapping mapping : columnMappingRequest) {
            columnMapping.put(mapping.getInputColumnName(), mapping.getOutputColumnName());
        }
        return columnMapping;
    }
}
