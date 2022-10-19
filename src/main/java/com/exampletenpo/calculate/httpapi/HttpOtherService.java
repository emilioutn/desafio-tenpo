package com.exampletenpo.calculate.httpapi;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;
import java.rmi.server.ServerNotActiveException;

public interface HttpOtherService {
    BigDecimal calculateFromApi(String jsonWithNumbers) throws InterruptedException, JsonProcessingException,
            ServerNotActiveException;
}
