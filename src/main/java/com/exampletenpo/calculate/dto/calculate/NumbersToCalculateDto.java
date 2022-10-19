package com.exampletenpo.calculate.dto.calculate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NumbersToCalculateDto {
    private BigDecimal firstNumber;
    private BigDecimal secondNumber;
}
