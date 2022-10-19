package com.exampletenpo.calculate.repository;

import com.exampletenpo.calculate.domain.account.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUsername(String username);
}
