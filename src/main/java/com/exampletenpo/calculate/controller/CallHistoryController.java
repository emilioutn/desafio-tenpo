package com.exampletenpo.calculate.controller;

import com.exampletenpo.calculate.dto.history.HistoryDto;
import com.exampletenpo.calculate.service.HistoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/call-history")
public class CallHistoryController {

    private final HistoryService historyService;

    @Secured({"ROLE_ADMIN"})
    @GetMapping(value = "/all")
    public ResponseEntity<Page<HistoryDto>> getAllCallHistory(Pageable pageable) {
        return ResponseEntity.ok(historyService.getAllCallHistory(pageable));
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping(value = "/method/{method}")
    public ResponseEntity<Page<HistoryDto>> getCallHistoryByMethod(@PathVariable String method,
                                                                   Pageable pageable) {
        return ResponseEntity.ok(historyService.getCallHistoryByMethod(method, pageable));
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping(value = "/username/{username}")
    public ResponseEntity<Page<HistoryDto>> getCallHistoryByUsername(@PathVariable String username,
                                                                     Pageable pageable) {
        return ResponseEntity.ok(historyService.getCallHistoryByUsername(username, pageable));
    }
}
