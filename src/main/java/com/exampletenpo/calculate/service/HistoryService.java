package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.domain.history.History;
import com.exampletenpo.calculate.dto.history.HistoryDto;
import com.exampletenpo.calculate.mapper.HistoryMapper;
import com.exampletenpo.calculate.repository.HistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;

    public History createHistory(HistoryDto historyDto) {
        return historyRepository.save(historyMapper.mapper(historyDto));
    }

    public Page<HistoryDto> getAllCallHistory(Pageable pageable) {
        return historyRepository.findAll(pageable).map(historyMapper::mapper);
    }

    public Page<HistoryDto> getCallHistoryByMethod(String method, Pageable pageable) {
        return historyRepository.findByMethod(method, pageable).map(historyMapper::mapper);
    }

    public Page<HistoryDto> getCallHistoryByUsername(String username, Pageable pageable) {
        if (!username.equalsIgnoreCase("ANONYMOUSUSER")) {
            username = username.toUpperCase();
        } else {
            username = "anonymousUser";
        }
        return historyRepository.findByUsername(username, pageable).map(historyMapper::mapper);
    }

}
