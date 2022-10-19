package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.domain.account.Role;
import com.exampletenpo.calculate.domain.account.User;
import com.exampletenpo.calculate.dto.account.UserDto;
import com.exampletenpo.calculate.error.InvalidRequestException;
import com.exampletenpo.calculate.error.UnauthorizedException;
import com.exampletenpo.calculate.mapper.UserMapper;
import com.exampletenpo.calculate.repository.RoleRepository;
import com.exampletenpo.calculate.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {
    private final SecurityService securityService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    private final String USERNAME_NULL_ERROR = "Username cannot be null";
    private final String PASSWORD_NULL_ERROR = "Password cannot be null";
    private final String ROLE_NULL_ERROR = "Does not exist the role ";
    private final String USER_NOT_EXIST = "Does not exist the user name ";
    private final String ROLE_USER = "ROLE_USER";
    private final String ROLE_ADMIN = "ROLE_ADMIN";

    public UserDto createUser(UserDto dto) {
        User user = getUserForCreateOrUpdate(dto, false, true);
        user.setUsername(user.getUsername().toUpperCase());
        return userMapper.mapper(userRepository.save(user));
    }

    public UserDto updateUser(UserDto dto) {
        User user = getUserForCreateOrUpdate(dto, true, dto.getEnabled());
        return userMapper.mapper(userRepository.save(user));
    }

    public void deleteUser(String username) {
        if (username != null && username.length() > 0) {
            User userLocal = userRepository.findByUsername(username.toUpperCase()).orElse(null);
            if (userLocal != null) {
                userRepository.delete(userLocal);
            } else {
                throw new EntityNotFoundException(USER_NOT_EXIST + username);
            }
        } else {
            throw new InvalidRequestException(USERNAME_NULL_ERROR);
        }
    }

    public List<Role> getAuthorities() {
        List<Role> roles = securityService.getAuthenticateAuthorities();
        roles.forEach(role -> role.setId(getIdRole(role.getAuthority())));
        return roles;
    }

    private User getUserForCreateOrUpdate(UserDto dto, Boolean isEditing, Boolean enabled) {
        User user = userMapper.mapper(dto);
        isUserDataNull(user, isEditing);
        User userLocal = userRepository.findByUsername(user.getUsername().toUpperCase()).orElse(null);

        if (!isEditing && userLocal != null) {
            throw new InvalidRequestException("Username already exists: " + user.getUsername());
        }
        if (isEditing && userLocal == null) {
            throw new EntityNotFoundException(USER_NOT_EXIST + user.getUsername());
        }

        if (!isEditing) {
            user.setRoles(getStandardRole());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            List<Role> roleAdmin = getAuthorities()
                    .stream()
                    .filter(role -> role.getAuthority().equals(ROLE_ADMIN)).toList();

            if (securityService.getThreadAccountUserName().equals(userLocal.getUsername()) || !roleAdmin.isEmpty()) {
                user.setId(userLocal.getId());
                user.setUsername(userLocal.getUsername());
                user.setRoles(getRoleForUpdate(userLocal, user));
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                } else {
                    user.setPassword(userLocal.getPassword());
                }
            } else {
                throw new UnauthorizedException("Access is denied");
            }
        }

        Boolean userEnabled = true;
        if (enabled != null) {
            userEnabled = enabled;
        }
        user.setEnabled(userEnabled);
        return user;
    }

    private List<Role> getStandardRole() {
        Role role = roleRepository.findByAuthority(ROLE_USER).orElse(null);
        if (role != null) {
            return Arrays.asList(role);
        }
        throw new EntityNotFoundException(ROLE_NULL_ERROR + ROLE_USER);
    }

    private Integer getIdRole(String authority) {
        Role role = roleRepository.findByAuthority(authority).orElse(null);
        if (role != null) {
            return role.getId();
        }
        return null;
    }

    private List<Role> getRoleForUpdate(User userLocal, User user) {
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            return user.getRoles()
                    .stream()
                    .map(role -> {
                        Role roleLocal = roleRepository.findByAuthority(role.getAuthority()).orElse(null);
                        if (roleLocal != null) {
                            return roleLocal;
                        }
                        throw new InvalidRequestException(ROLE_NULL_ERROR + role.getAuthority());
                    })
                    .collect(Collectors.toList());
        }
        return userLocal.getRoles();
    }

    private void isUserDataNull(User user, Boolean isEditing) {
        String error = "";
        if (user.getUsername() == null || user.getUsername().length() == 0) {
            error = USERNAME_NULL_ERROR;
        } else if (!isEditing && (user.getPassword() == null || user.getPassword().length() == 0)) {
            error = PASSWORD_NULL_ERROR;
        }
        if (error.length() > 0) {
            throw new InvalidRequestException(error);
        }
    }
}
