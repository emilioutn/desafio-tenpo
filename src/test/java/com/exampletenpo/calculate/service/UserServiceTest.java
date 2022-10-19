package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.domain.account.Role;
import com.exampletenpo.calculate.domain.account.User;
import com.exampletenpo.calculate.dto.account.RoleDto;
import com.exampletenpo.calculate.dto.account.UserDto;
import com.exampletenpo.calculate.error.InvalidRequestException;
import com.exampletenpo.calculate.error.UnauthorizedException;
import com.exampletenpo.calculate.mapper.UserMapper;
import com.exampletenpo.calculate.repository.RoleRepository;
import com.exampletenpo.calculate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    private UserDto userDtoNotNull;
    private User userNotNull;

    private final String USERNAME_NULL_ERROR = "Username cannot be null";
    private final String PASSWORD_NULL_ERROR = "Password cannot be null";
    private final String USER_NOT_EXIST = "Does not exist the user name ";

    private final String ROLE_USER = "ROLE_USER";
    private final String ROLE_ADMIN = "ROLE_ADMIN";


    @BeforeEach
    public void init() {
        userDtoNotNull = getUserDto();
        userNotNull = getUser();
    }

    @Test
    public void createUser_whenUsernameNull_thenReturnsException() {
        UserDto userDto = validateDataUserUsernameNull();
        InvalidRequestException thrown = assertThrows(
                InvalidRequestException.class,
                () -> userService.createUser(userDto)
        );
        assertTrue(thrown.getMessage().contains(USERNAME_NULL_ERROR));
    }

    @Test
    public void createUser_whenPasswordNull_thenReturnsException() {
        UserDto userDto = validateDataPasswordNull();
        InvalidRequestException thrown = assertThrows(
                InvalidRequestException.class,
                () -> userService.createUser(userDto)
        );
        assertTrue(thrown.getMessage().contains(PASSWORD_NULL_ERROR));
    }

    @Test
    public void createUser_whenExistsUser_thenReturnsException() {
        when(userMapper.mapper(any(UserDto.class))).thenReturn(userNotNull);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(userNotNull));
        InvalidRequestException thrown = assertThrows(
                InvalidRequestException.class,
                () -> userService.createUser(userDtoNotNull)
        );
        assertTrue(thrown.getMessage().contains("Username already exists"));
    }

    @Test
    public void createUser_whenNotExistsUser_thenReturnsOk() {
        when(userMapper.mapper(any(UserDto.class))).thenReturn(userNotNull);
        when(userRepository.findByUsername(anyString())).then(Answers.RETURNS_SMART_NULLS);
        when(roleRepository.findByAuthority(anyString())).thenReturn(Optional.ofNullable(getRole(ROLE_USER)));
        when(passwordEncoder.encode(anyString())).thenReturn(userNotNull.getPassword());
        when(userMapper.mapper(userRepository.save(any(User.class)))).thenReturn(userDtoNotNull);
        userDtoNotNull.setUsername(userDtoNotNull.getUsername().toUpperCase());
        userDtoNotNull.setRoles(getListStandardRoleDto());
        UserDto userCreated = userService.createUser(userDtoNotNull);
        assertEquals(userDtoNotNull, userCreated);
    }

    @Test
    public void updateUser_whenUsernameNull_thenReturnsException() {
        UserDto userDto = validateDataUserUsernameNull();
        InvalidRequestException thrown = assertThrows(
                InvalidRequestException.class,
                () -> userService.updateUser(userDto)
        );
        assertTrue(thrown.getMessage().contains(USERNAME_NULL_ERROR));
    }

    @Test
    public void updateUser_whenNotExistsUser_thenReturnsException() {
        when(userMapper.mapper(any(UserDto.class))).thenReturn(userNotNull);
        when(userRepository.findByUsername(anyString())).then(Answers.RETURNS_SMART_NULLS);
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUser(userDtoNotNull)
        );
        assertTrue(thrown.getMessage().contains(USER_NOT_EXIST));
    }

    @Test
    public void updateUser_whenEditSameUserWithPassword_thenReturnsOk() {
        when(userMapper.mapper(any(UserDto.class))).thenReturn(userNotNull);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(userNotNull));
        when(securityService.getThreadAccountUserName()).thenReturn(userNotNull.getUsername());
        when(securityService.getAuthenticateAuthorities()).thenReturn(getListStandardRole());
        when(passwordEncoder.encode(anyString())).thenReturn(userNotNull.getPassword());

        when(userMapper.mapper(userRepository.save(any(User.class)))).thenReturn(userDtoNotNull);
        userDtoNotNull.setUsername(userDtoNotNull.getUsername().toUpperCase());
        userDtoNotNull.setRoles(getListStandardRoleDto());

        UserDto userUpdated = userService.updateUser(userDtoNotNull);
        assertEquals(userDtoNotNull, userUpdated);
    }

    @Test
    public void updateUser_whenEditSameUserWithoutPassword_thenReturnsOk() {
        User user = User.builder().username("user").build();
        when(userMapper.mapper(any(UserDto.class))).thenReturn(user);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(user));
        when(securityService.getThreadAccountUserName()).thenReturn(user.getUsername());
        when(securityService.getAuthenticateAuthorities()).thenReturn(getListStandardRole());

        when(userMapper.mapper(userRepository.save(any(User.class)))).thenReturn(userDtoNotNull);
        userDtoNotNull.setUsername(userDtoNotNull.getUsername().toUpperCase());
        userDtoNotNull.setRoles(getListStandardRoleDto());

        UserDto userUpdated = userService.updateUser(userDtoNotNull);
        assertEquals(userDtoNotNull, userUpdated);
    }

    @Test
    public void updateUser_whenAdminUser_thenReturnsOk() {
        when(userMapper.mapper(any(UserDto.class))).thenReturn(userNotNull);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(userNotNull));
        when(securityService.getThreadAccountUserName()).thenReturn("");
        when(securityService.getAuthenticateAuthorities()).thenReturn(getAllRoles());
        when(passwordEncoder.encode(anyString())).thenReturn(userNotNull.getPassword());

        when(userMapper.mapper(userRepository.save(any(User.class)))).thenReturn(userDtoNotNull);
        userDtoNotNull.setUsername(userDtoNotNull.getUsername().toUpperCase());
        userDtoNotNull.setRoles(getListStandardRoleDto());

        UserDto userUpdated = userService.updateUser(userDtoNotNull);
        assertEquals(userDtoNotNull, userUpdated);
    }

    @Test
    public void updateUser_whenNotTheSameUserNeitherAdmin_thenReturnsException() {
        when(userMapper.mapper(any(UserDto.class))).thenReturn(userNotNull);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(userNotNull));
        when(securityService.getThreadAccountUserName()).thenReturn("");
        when(securityService.getAuthenticateAuthorities()).thenReturn(getListStandardRole());
        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> userService.updateUser(userDtoNotNull)
        );
        assertTrue(thrown.getMessage().contains("Access is denied"));
    }


    private UserDto validateDataUserUsernameNull() {
        UserDto userDto = UserDto.builder().password("12345").build();
        User user = User.builder().enabled(true).password("12345").build();
        when(userMapper.mapper(any(UserDto.class))).thenReturn(user);
        return userDto;
    }

    private UserDto validateDataPasswordNull() {
        UserDto userDto = UserDto.builder().username("user").build();
        User user = User.builder().enabled(true).username("user").build();
        when(userMapper.mapper(any(UserDto.class))).thenReturn(user);
        return userDto;
    }

    private List<Role> getListStandardRole() {
        List<Role> roles = new ArrayList<>();
        roles.add(getRole(ROLE_USER));
        return roles;
    }

    private List<RoleDto> getListStandardRoleDto() {
        List<RoleDto> roles = new ArrayList<>();
        roles.add(getStandardRoleDto());
        return roles;
    }

    private RoleDto getStandardRoleDto() {
        return RoleDto.builder().id(1).authority("ROLE_USER").build();
    }

    private List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        roles.add(getRole(ROLE_USER));
        roles.add(getRole(ROLE_ADMIN));
        return roles;
    }

    private Role getRole(String authority) {
        if (authority.equals(ROLE_USER)) {
            return Role.builder().id(1).authority(authority).build();
        }
        return Role.builder().id(2).authority(authority).build();
    }

    private UserDto getUserDto() {
        return UserDto
                .builder()
                .username("user")
                .password("12345")
                .build();
    }

    private User getUser() {
        return User
                .builder()
                .username("user")
                .password("12345")
                .build();
    }
}
