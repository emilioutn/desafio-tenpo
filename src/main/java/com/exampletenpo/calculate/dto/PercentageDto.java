package com.exampletenpo.calculate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PercentageDto {
    private Integer id;
    private BigDecimal percentage;
    private BigDecimal firstNumber;
    private BigDecimal secondNumber;
    private Date dateLastUpdate;
}
