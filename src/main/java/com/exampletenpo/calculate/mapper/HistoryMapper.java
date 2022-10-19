package com.exampletenpo.calculate.mapper;

import com.exampletenpo.calculate.domain.history.History;
import com.exampletenpo.calculate.dto.history.HistoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoryMapper {
    HistoryMapper MAPPER = Mappers.getMapper(HistoryMapper.class);

    HistoryDto mapper(History entity);

    History mapper(HistoryDto dto);
}
