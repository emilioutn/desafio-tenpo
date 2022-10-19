package com.exampletenpo.calculate;

import com.exampletenpo.calculate.domain.account.Role;
import com.exampletenpo.calculate.domain.account.User;
import com.exampletenpo.calculate.repository.RoleRepository;
import com.exampletenpo.calculate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AuthTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public static final String ADMIN_USER_TEST = "ADMIN_USER_TEST";
    public static final String NO_ADMIN_USER_TEST = "NO_ADMIN_USER_TEST";

    private final Integer COMMON_USER_ROLE_ID = 1;
    private final String COMMON_USER_ROLE = "ROLE_USER";

    public UserDetails getUserDetails(User user) {
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.getEnabled(), true,
                true, true, authorities
        );
    }

    public User getAdminUser() {
        return User
                .builder()
                .username(ADMIN_USER_TEST)
                .password(passwordEncoder.encode("12345"))
                .enabled(true)
                .roles(getAllRoles())
                .build();
    }

    public User getNoAdminUser() {
        return User
                .builder()
                .username(NO_ADMIN_USER_TEST)
                .password(passwordEncoder.encode("12345"))
                .enabled(true)
                .roles(getRoleUserNoAdmin())
                .build();
    }

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        roleRepository.findAll().iterator().forEachRemaining(roles::add);
        return roles;
    }

    public List<Role> getRoleUserNoAdmin() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role
                .builder()
                .id(COMMON_USER_ROLE_ID)
                .authority(COMMON_USER_ROLE)
                .build());
        return roles;
    }

    public User createNoAdminUserForTest() {
        return userRepository.save(getNoAdminUser());
    }

    public User createUserAdminForTest() {
        return userRepository.save(getAdminUser());
    }

    public void deleteNoAdminUserTest() {
        userRepository.delete(
                Objects.requireNonNull(userRepository.findByUsername(NO_ADMIN_USER_TEST).orElse(null)));
    }

    public void deleteAdminUserTest() {
        userRepository.delete(
                Objects.requireNonNull(userRepository.findByUsername(ADMIN_USER_TEST).orElse(null)));
    }
}
