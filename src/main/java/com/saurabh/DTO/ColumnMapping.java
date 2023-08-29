package com.saurabh.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
@AllArgsConstructor
public class ColumnMapping {
    private String inputColumnName;
    private String outputColumnName;
    private String inputDataType; 
    private String outputType;

}