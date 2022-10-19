package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.config.context.ContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CommonService {

    public void registerResultToContext(Object object) {
        try {
            ContextHolder.setResponse(objectToJson(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeJsonMappingException("Impossible convert Object to Json");
        }
    }

    public String objectToJson(Object object) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }
}
