package com.exampletenpo.calculate;

import com.exampletenpo.calculate.domain.account.Role;
import com.exampletenpo.calculate.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    private final RoleRepository roleRepository;

    private final String ROLE_USER = "ROLE_USER";
    private final String ROLE_ADMIN = "ROLE_ADMIN";
    private final Integer ROLE_USER_ID = 1;
    private final Integer ROLE_ADMIN_ID = 2;

    @Override
    public void run(ApplicationArguments args) {
        initData();
    }

    private void initData() {
        List<Role> roles = new ArrayList<>();
        roleRepository.findAll().forEach(roles::add);
        if (roles.isEmpty()) {
            roleRepository.save(Role.builder().id(ROLE_USER_ID).authority(ROLE_USER).build());
            roleRepository.save(Role.builder().id(ROLE_ADMIN_ID).authority(ROLE_ADMIN).build());
        }
    }
}
