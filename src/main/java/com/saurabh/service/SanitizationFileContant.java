package com.saurabh.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SanitizationFileContant {
    String sanitizeCellValue(String cellValue) {
        cellValue = cellValue.replaceAll("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
        boolean cellValueFlag = containsHiddenCharacters(cellValue);
        log.info("---------------->" + cellValueFlag);
        return cellValue;
    }

    String[] sanitizeHeader(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].replaceAll("[^\\p{Print}]", "");
        }
        return headers;
    }

    private boolean containsHiddenCharacters(String value) {
        return value.matches(".*[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}].*");
    }

    String sanitizeSheetName(String sheetName) {
        return sheetName.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

}
