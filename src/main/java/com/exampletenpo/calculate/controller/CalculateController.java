package com.exampletenpo.calculate.controller;

import com.exampletenpo.calculate.service.CalculateService;
import com.exampletenpo.calculate.service.CommonService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.rmi.ServerException;

@AllArgsConstructor
@RestController
public class CalculateController {

    private final CommonService commonService;
    private final CalculateService calculateService;

    @Secured({"ROLE_ADMIN"})
    @GetMapping(value = "/calculate")
    public ResponseEntity<BigDecimal> calculate(@RequestParam("firstNumber") String firstNumber,
                                                @RequestParam("secondNumber") String secondNumber)
            throws ServerException {
        BigDecimal calculation = calculateService.calculate(firstNumber, secondNumber);
        commonService.registerResultToContext(calculation);
        return ResponseEntity.ok(calculation);
    }

}
