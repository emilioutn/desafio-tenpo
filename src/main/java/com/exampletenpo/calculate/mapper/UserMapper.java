package com.exampletenpo.calculate.mapper;

import com.exampletenpo.calculate.domain.account.User;
import com.exampletenpo.calculate.dto.account.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserDto mapper(User entity);

    User mapper(UserDto dto);
}
