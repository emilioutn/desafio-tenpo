package com.exampletenpo.calculate.repository;

import com.exampletenpo.calculate.domain.account.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByAuthority(String authority);
}
