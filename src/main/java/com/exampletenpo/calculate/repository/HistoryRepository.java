package com.exampletenpo.calculate.repository;

import com.exampletenpo.calculate.domain.history.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface HistoryRepository extends PagingAndSortingRepository<History, String> {

    Page<History> findByMethod(String method, Pageable p);

    Page<History> findByUsername(String username, Pageable p);

}
