package com.exampletenpo.calculate.error;

import lombok.Data;

import java.io.Serializable;

@Data
public class FieldErrorVM implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final String message;
}
