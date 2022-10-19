package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.domain.Percentage;
import com.exampletenpo.calculate.dto.calculate.NumbersToCalculateDto;
import com.exampletenpo.calculate.httpapi.HttpOtherService;
import com.exampletenpo.calculate.repository.PercentageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.rmi.ServerException;
import java.rmi.server.ServerNotActiveException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculateServiceMockTest {

    @InjectMocks
    private CalculateService calculateService;

    @Mock
    private HttpOtherService httpOtherService;

    @Mock
    private PercentageRepository percentageRepository;

    @Mock
    private CommonService commonService;

    @Test
    public void getPercentageNumbers_whenFailHttpServiceAndPercentageNull_thenReturnException() {
        ServerException thrown = assertThrows(
                ServerException.class,
                () -> calculateService.calculate("5", "5")
        );
        assertTrue(thrown.getMessage().contains("Unable to connect to the percentage server"));
    }

    @Test
    public void getPercentageNumbers_whenFailHttpService_thenReturnExistsPercentageNumber() throws ServerException {
        Percentage percentage = getPercentage();
        when(percentageRepository.findById(anyInt())).thenReturn(Optional.of(percentage));
        BigDecimal result = calculateService.calculate("5", "5");
        assertEquals(result, new BigDecimal("11.00"));
    }

    @Test
    public void getPercentageNumbers_whenReturnHttpService_thenUpdatePercentageNumber()
            throws ServerException, ServerNotActiveException, InterruptedException, JsonProcessingException {
        Percentage percentage = getPercentage();
        when(commonService.objectToJson(any(NumbersToCalculateDto.class))).thenReturn("");
        when(httpOtherService.calculateFromApi(anyString())).thenReturn(new BigDecimal("10"));
        when(percentageRepository.findById(anyInt())).thenReturn(Optional.of(percentage));
        BigDecimal result = calculateService.calculate("5", "5");
        assertEquals(result, new BigDecimal("11.00"));
    }

    @Test
    public void getPercentageNumbers_whenReturnHttpService_thenCreatePercentageNumber()
            throws ServerException, ServerNotActiveException, InterruptedException, JsonProcessingException {
        when(commonService.objectToJson(any(NumbersToCalculateDto.class))).thenReturn("");
        when(httpOtherService.calculateFromApi(anyString())).thenReturn(new BigDecimal("10"));
        when(percentageRepository.findById(anyInt())).then(Answers.RETURNS_SMART_NULLS);
        BigDecimal result = calculateService.calculate("5", "5");
        assertEquals(result, new BigDecimal("11.00"));
    }

    @Test
    public void recalculatePercentage_whenPercentageIsNull_thenDoNothing() {
        when(percentageRepository.findById(anyInt())).then(Answers.RETURNS_SMART_NULLS);
        calculateService.recalculatePercentage();
    }

    @Test
    public void updatePercentageNumbersFromApi_whenPercentageNotIsNull_thenReturnOk()
            throws ServerNotActiveException, InterruptedException, JsonProcessingException {
        Percentage percentage = getPercentage();
        when(commonService.objectToJson(any(NumbersToCalculateDto.class))).thenReturn("");
        when(httpOtherService.calculateFromApi(anyString())).thenReturn(new BigDecimal("10"));
        when(percentageRepository.findById(anyInt())).thenReturn(Optional.of(percentage));
        calculateService.recalculatePercentage();
    }

    @Test
    public void updatePercentageNumbersFromApi_whenFailHttpService_thenReturnOk() throws JsonProcessingException {
        Percentage percentage = getPercentage();
        when(commonService.objectToJson(any(NumbersToCalculateDto.class))).thenReturn("");
        when(percentageRepository.findById(anyInt())).thenReturn(Optional.of(percentage));
        calculateService.recalculatePercentage();
    }

    private Percentage getPercentage() {
        return Percentage
                .builder()
                .firstNumber(new BigDecimal(5))
                .secondNumber(new BigDecimal(5))
                .percentage(new BigDecimal(10))
                .build();
    }
}
