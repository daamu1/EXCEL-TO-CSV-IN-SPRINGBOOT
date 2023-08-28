package com.saurabh.controller;

import com.saurabh.service.ExcelToCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/excel-to-json")
public class ExcelToJsonController {

    @Autowired
    private ExcelToCsvService excelToCsvService;

    @PostMapping("/convert")
    public ResponseEntity<String> convertExcelToJson(@RequestParam("file") MultipartFile file, @RequestParam("location") String location) {
        try {
            String result = excelToCsvService.convertExcelToCSV(file, location);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting Excel to CSV: " + e.getMessage());
        }
    }
}
