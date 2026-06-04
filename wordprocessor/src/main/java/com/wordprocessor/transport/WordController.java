package com.wordprocessor.transport;

import com.wordprocessor.dto.RuleDto;
import com.wordprocessor.dto.WordProcessingResult;
import com.wordprocessor.dto.WordRequestDto;
import com.wordprocessor.service.WordProcessingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/v1/words")
@Validated
public class WordController {

    private final WordProcessingService service;

    private final AtomicInteger totalClientCount = new AtomicInteger(0);

    public WordController(WordProcessingService service) {
        this.service = service;
    }

    /**
     * API 1: Get the currently active business rules
     * GET /api/words/rules
     */
    @GetMapping("/rules")
    public ResponseEntity<RuleDto> getCurrentRules() {
        RuleDto activeRules = service.getCurrentRuleState();
        return ResponseEntity.ok(activeRules);
    }

    /**
     * API 2: Post inputs and get response
     * POST /api/words/process
     */
    @PostMapping("/process")
    public ResponseEntity<WordProcessingResult> processWords(@Valid @RequestBody WordRequestDto request) {
        WordProcessingResult result = service.process(request);
        return ResponseEntity.ok(result);
    }

    /**
     * API 3: Change or modify business rules dynamically
     * PUT /api/words/rules
     */
    @PutMapping("/rules")
    public ResponseEntity<String> updateRules(@Valid @RequestBody RuleDto newRules) {
        log.info("Total client rule update requests processed: {}", totalClientCount.incrementAndGet());
        service.updateRules(newRules);
        return ResponseEntity.ok("Success: Business rules have been updated.");
    }
}