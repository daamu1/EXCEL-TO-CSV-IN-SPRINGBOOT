package com.saurabh.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ColumnMapping {
    private String inputColumnName;
    private String outputColumnName;
}