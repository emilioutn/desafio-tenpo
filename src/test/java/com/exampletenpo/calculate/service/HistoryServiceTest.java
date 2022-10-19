package com.exampletenpo.calculate.service;

import com.exampletenpo.calculate.domain.history.History;
import com.exampletenpo.calculate.dto.history.HistoryDto;
import com.exampletenpo.calculate.mapper.HistoryMapper;
import com.exampletenpo.calculate.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceTest {

    @InjectMocks
    private HistoryService historyService;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private HistoryMapper historyMapper;

    private HistoryDto historyDto;
    private History history;

    @BeforeEach
    public void init() {
        historyDto = getHistoryDto();
        history = getHistory();
    }

    @Test
    public void createHistory_whenReceivedDto_thenReturnOk() {
        when(historyMapper.mapper(any(HistoryDto.class))).thenReturn(history);
        when(historyRepository.save(any(History.class))).thenReturn(history);
        History result = historyService.createHistory(historyDto);
        assertEquals(history, result);
    }

    @Test
    public void getAllCallHistory_whenReceivedPageable_thenReturnOk() {
        when(historyMapper.mapper(any(History.class))).thenAnswer((Answer) invocation -> getHistoryDto());
        when(historyRepository.findAll(any(Pageable.class))).thenReturn(createPageResponseHistory());
        Page<HistoryDto> pageHistory = historyService.getAllCallHistory(createPageableHistory());
        assertEquals(createDtoPageResponseHistory(), pageHistory);
    }

    @Test
    public void getCallHistoryByMethod_whenReceivedMethodAndPageable_thenReturnOk() {
        when(historyMapper.mapper(any(History.class))).thenAnswer((Answer) invocation -> getHistoryDto());
        when(historyRepository.findByMethod(anyString(), any(Pageable.class))).thenReturn(createPageResponseHistory());
        Page<HistoryDto> pageHistory = historyService.getCallHistoryByMethod("GET", createPageableHistory());
        assertEquals(createDtoPageResponseHistory(), pageHistory);
    }

    @Test
    public void getCallHistoryByUsername_whenSearchByExistsUser_thenReturnOk() {
        when(historyMapper.mapper(any(History.class))).thenAnswer((Answer) invocation -> getHistoryDto());
        when(historyRepository.findByUsername(anyString(), any(Pageable.class))).thenReturn(createPageResponseHistory());
        Page<HistoryDto> pageHistory = historyService.getCallHistoryByUsername("username", createPageableHistory());
        assertEquals(createDtoPageResponseHistory(), pageHistory);
    }

    @Test
    public void getCallHistoryByUsername_whenSearchByAnonymousUser_thenReturnOk() {
        when(historyMapper.mapper(any(History.class))).thenAnswer((Answer) invocation -> getHistoryDto());
        when(historyRepository.findByUsername(anyString(), any(Pageable.class))).thenReturn(createPageResponseHistory());
        Page<HistoryDto> pageHistory = historyService.getCallHistoryByUsername("ANONYMOUSUSER", createPageableHistory());
        assertEquals(createDtoPageResponseHistory(), pageHistory);
    }

    private Pageable createPageableHistory() {
        return PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "date"));
    }

    private Page<HistoryDto> createDtoPageResponseHistory() {
        List<HistoryDto> historyDtoList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            historyDtoList.add(getHistoryDto());
        }
        return new PageImpl<>(historyDtoList);
    }

    private Page<History> createPageResponseHistory() {
        List<History> historyList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            historyList.add(getHistory());
        }
        return new PageImpl<>(historyList);
    }

    private List<HistoryDto> getHistoryDtoList() {
        List<HistoryDto> historyDtoList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            historyDtoList.add(getHistoryDto());
        }
        return historyDtoList;
    }

    private HistoryDto getHistoryDto() {
        return HistoryDto
                .builder()
                .method("GET")
                .path("/somepath")
                .username("username")
                .ipFrom("127.0.0.1")
                .status(200)
                .result("{some Json}")
                .date(null)
                .build();
    }

    private History getHistory() {
        return History
                .builder()
                .method("GET")
                .path("/somepath")
                .username("username")
                .ipFrom("127.0.0.1")
                .status(200)
                .result("{some Json}")
                .date(null)
                .build();
    }
}
