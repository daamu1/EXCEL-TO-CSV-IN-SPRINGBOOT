package com.saurabh.controller;

import com.saurabh.DTO.ColumnMappingRequest;
import com.saurabh.service.ExcelToCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/excel-to-json")
public class ExcelToCsvController {

    private final ExcelToCsvService excelToCsvService;

    @Autowired
    public ExcelToCsvController(ExcelToCsvService excelToCsvService) {
        this.excelToCsvService = excelToCsvService;
    }

    @PostMapping(value = "/convert", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> convertExcelToJson(
//            @RequestPart("location") String location,
            @RequestPart("file") MultipartFile file, @RequestPart("request") ColumnMappingRequest columnMappingRequest) {

        try {
            String result = excelToCsvService.convertExcelToCSV(file, "/home/cyno/Desktop", columnMappingRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting Excel to CSV: " + e.getMessage());
        }
    }
}
