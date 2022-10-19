package com.exampletenpo.calculate.mapper;

import com.exampletenpo.calculate.domain.Percentage;
import com.exampletenpo.calculate.dto.PercentageDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PercentageMapper {
    PercentageMapper MAPPER = Mappers.getMapper(PercentageMapper.class);

    PercentageDto mapper(Percentage entity);

    Percentage mapper(PercentageDto dto);
}
