package com.exampletenpo.calculate.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private String id;
    private String username;
    private String password;
    private Boolean enabled;
    private List<RoleDto> roles;
}
