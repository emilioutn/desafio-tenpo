package com.exampletenpo.calculate.dto.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoryDto {
    private String Id;
    private String method;
    private String path;
    private String username;
    private String ipFrom;
    private Integer status;
    private String result;
    private Date date;
}
