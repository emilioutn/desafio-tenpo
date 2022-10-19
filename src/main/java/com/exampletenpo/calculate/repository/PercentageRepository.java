package com.exampletenpo.calculate.repository;

import com.exampletenpo.calculate.domain.Percentage;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PercentageRepository extends CrudRepository<Percentage, Integer> {
    Optional<Percentage> findById(Integer id);
}
