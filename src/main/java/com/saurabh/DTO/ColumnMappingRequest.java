package com.saurabh.DTO;

import lombok.*;
import  com.saurabh.DTO.ColumnMapping;
import java.util.List;
@Data
@Getter
@Setter
@ToString
public class ColumnMappingRequest {
    private List<ColumnMapping> columnMapping;
}


