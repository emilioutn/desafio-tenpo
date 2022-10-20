package com.exampletenpo.calculate.httpapi;

import com.exampletenpo.calculate.dto.calculate.NumbersToCalculateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.rmi.server.ServerNotActiveException;
import java.util.Random;

@Component
public class HttpOtherServiceMock implements HttpOtherService {

    @Override
    public BigDecimal calculateFromApi(String jsonWithNumbers) throws InterruptedException, JsonProcessingException,
            ServerNotActiveException {
        if (generateNumberRandom() < 800) {
            Thread.sleep(1000);
            NumbersToCalculateDto numbersToCalculateDto = convertJsonToNumbersDto(jsonWithNumbers);
            return numbersToCalculateDto.getFirstNumber().add(numbersToCalculateDto.getSecondNumber());
        } else {
            Thread.sleep(3000);
            throw new ServerNotActiveException();
        }
    }

    private NumbersToCalculateDto convertJsonToNumbersDto(String jsonToConvert) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonToConvert, NumbersToCalculateDto.class);
    }

    private Integer generateNumberRandom() {
        Random random = new Random();
        return random.ints(1000, 1, 999).findAny().getAsInt();
    }
}
