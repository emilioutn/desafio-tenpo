package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.domain.Percentage;
import com.exampletenpo.calculate.dto.calculate.NumbersToCalculateDto;
import com.exampletenpo.calculate.httpapi.HttpOtherService;
import com.exampletenpo.calculate.mapper.PercentageMapper;
import com.exampletenpo.calculate.repository.PercentageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.ServerException;
import java.rmi.server.ServerNotActiveException;
import java.util.Date;

@Slf4j
@Service
public class CalculateService {

    @Autowired
    @Qualifier("httpOtherServiceMock")
    private HttpOtherService httpOtherService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private PercentageRepository percentageRepository;

    @Autowired
    private PercentageMapper percentageMapper;

    private final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    private final Integer ID_PERCENTAGE = 1;
    private final Integer INITIAL_RETRY = 0;
    private final Integer LAST_RETRY = 3;
    private final String ERROR_SERVER_PERCENTAGE = "Unable to connect to the percentage server";

    public BigDecimal calculate(String first, String second) throws ServerException {
        BigDecimal firstNumber = new BigDecimal(first);
        BigDecimal secondNumber = new BigDecimal(second);
        BigDecimal percentage = getPercentageNumbers(firstNumber, secondNumber);
        return calculateSumWithPercentage(firstNumber, secondNumber, percentage);
    }

    private BigDecimal calculateSumWithPercentage(BigDecimal firstNumber, BigDecimal secondNumber,
                                                  BigDecimal percentage) {
        BigDecimal sumNumbers = firstNumber.add(secondNumber);
        BigDecimal plusPercent = sumNumbers
                .multiply(percentage)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_EVEN);
        return sumNumbers.add(plusPercent);
    }

    private String buildJsonWithNumbers(BigDecimal firstNumber, BigDecimal secondNumber)
            throws JsonProcessingException {
        NumbersToCalculateDto numbersToCalculateDto = NumbersToCalculateDto
                .builder()
                .firstNumber(firstNumber)
                .secondNumber(secondNumber)
                .build();
        return commonService.objectToJson(numbersToCalculateDto);
    }

    private BigDecimal getPercentageNumbers(BigDecimal firstNumber, BigDecimal secondNumber)
            throws ServerException {
        BigDecimal percentageNumber = getPercentageNumberFromApi(INITIAL_RETRY, firstNumber, secondNumber);
        if (percentageNumber == null) {

            log.warn(ERROR_SERVER_PERCENTAGE);

            Percentage percentageObj = getPercentageObj();
            if (percentageObj == null) {
                throw new ServerException(ERROR_SERVER_PERCENTAGE);
            }
            return percentageObj.getPercentage();
        }
        saveNewPercentage(firstNumber, secondNumber, percentageNumber);
        return percentageNumber;
    }

    private void saveNewPercentage(BigDecimal firstNumber, BigDecimal secondNumber, BigDecimal percentageNumber) {
        Percentage percentageObj = getPercentageObj();
        if (percentageObj != null) {
            updatePercentageObj(percentageObj, firstNumber, secondNumber, percentageNumber);
        } else {
            createPercentageObj(firstNumber, secondNumber, percentageNumber);
        }
    }

    private Percentage getPercentageObj() {
        return percentageRepository.findById(ID_PERCENTAGE).orElse(null);
    }

    private void updatePercentageObj(Percentage percentageObj, BigDecimal firstNumber, BigDecimal secondNumber,
                                     BigDecimal percentageNumber) {
        percentageObj.setPercentage(percentageNumber);
        percentageObj.setFirstNumber(firstNumber);
        percentageObj.setSecondNumber(secondNumber);
        percentageObj.setDateLastUpdate(new Date());
        percentageRepository.save(percentageObj);
    }

    private void createPercentageObj(BigDecimal firstNumber, BigDecimal secondNumber, BigDecimal percentageNumber) {
        percentageRepository.save(
                Percentage
                        .builder()
                        .id(ID_PERCENTAGE)
                        .percentage(percentageNumber)
                        .firstNumber(firstNumber)
                        .secondNumber(secondNumber)
                        .dateLastUpdate(new Date())
                        .build()
        );
    }

    private BigDecimal getPercentageNumberFromApi(Integer retries, BigDecimal firstNumber, BigDecimal secondNumber) {
        if (retries < LAST_RETRY) {
            try {
                return getPercentageNumberHttp(firstNumber, secondNumber);
            } catch (Exception e) {
                log.warn("Attempt number {} to connect to percentage server", retries + 1);
                getPercentageNumberFromApi(retries + 1, firstNumber, secondNumber);
            }
        }
        return null;
    }

    private BigDecimal getPercentageNumberHttp(BigDecimal firstNumber, BigDecimal secondNumber)
            throws JsonProcessingException, InterruptedException, ServerNotActiveException {
        String json = buildJsonWithNumbers(firstNumber, secondNumber);
        return httpOtherService.calculateFromApi(json);
    }

    @Scheduled(cron = "${tenpo.app.percentageCron}")
    public void recalculatePercentage() {
        synchronized (percentageRepository) {
            updatePercentageNumbersFromApi();
        }
    }

    private void updatePercentageNumbersFromApi() {

        log.info("\n\n******************START CRON******************\n");

        Percentage percentageObj = getPercentageObj();
        if (percentageObj != null) {
            BigDecimal firstNumber = percentageObj.getFirstNumber();
            BigDecimal secondNumber = percentageObj.getSecondNumber();

            BigDecimal percentageNumber = getPercentageNumberFromApi(INITIAL_RETRY, firstNumber, secondNumber);

            if (percentageNumber != null) {
                updatePercentageObj(percentageObj, firstNumber, secondNumber, percentageNumber);
                log.info("++++++++++++UPDATE Percentage object++++++++++++");
            } else {
                log.warn("-+-+-+-+-+-+-{}-+-+-+-+-+-+-", ERROR_SERVER_PERCENTAGE);
            }
        } else {
            log.error("----------FAIL CRON: Percentage object has not been found----------");
        }
        log.info("\n\n******************FINISH CRON******************\n");
    }
}
