package com.saurabh.controller;

import com.saurabh.DTO.ColumnMappingRequest;
import com.saurabh.service.ExcelToCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/excel-to-json")
public class ExcelToCsvController {

    private final ExcelToCsvService excelToCsvService;

    @Autowired
    public ExcelToCsvController(ExcelToCsvService excelToCsvService) {
        this.excelToCsvService = excelToCsvService;
    }

    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertExcelToJson(@RequestParam("location") String location, ColumnMappingRequest columnMappingRequest) {
        try {
            String result = excelToCsvService.convertExcelToCSV(columnMappingRequest.getFile(), location, columnMappingRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting Excel to CSV: " + e.getMessage());
        }
    }
}
