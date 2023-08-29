package com.saurabh.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ColumnMappingRequest {
    private MultipartFile file;
    private List<ColumnMapping> columnMapping;
}


