package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.domain.account.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SecurityService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getThreadAccountUserName() {
        if (getAuthentication() != null) {
            return getAuthenticateUsername();
        }
        return "CRON";
    }

    public List<Role> getAuthenticateAuthorities() {
        return getAuthentication()
                .getAuthorities()
                .stream()
                .map(auth ->
                        Role
                                .builder()
                                .authority(auth.getAuthority())
                                .build()
                )
                .toList();
    }

    private String getAuthenticateUsername() {
        return getAuthentication().getName();
    }
}
