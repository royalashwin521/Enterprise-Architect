package com.wordprocessor.service;

import com.wordprocessor.dto.RuleDto;
import com.wordprocessor.dto.WordProcessingResult;
import com.wordprocessor.dto.WordRequestDto;
import com.wordprocessor.exception.InvalidRuleException;
import com.wordprocessor.service.engine.FunctionalRuleEngine;
import com.wordprocessor.service.engine.WordProcessingRuleEngine;
import com.wordprocessor.service.rules.CollectRuleStrategy;
import com.wordprocessor.service.rules.CountRuleStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Service
public class WordProcessingService {

    private final AtomicReference<WordProcessingRuleEngine> currentEngine;
    private final AtomicReference<RuleDto> currentRuleState;

    // Injecting all available strategies
    private final List<CountRuleStrategy> countStrategies;
    private final List<CollectRuleStrategy> collectStrategies;

    public WordProcessingService(
            List<CountRuleStrategy> countStrategies,
            List<CollectRuleStrategy> collectStrategies,
            @Value("${app.rules.default.count-condition}") String countCondition,
            @Value("${app.rules.default.count-value}") String countValue,
            @Value("${app.rules.default.collect-condition}") String collectCondition,
            @Value("${app.rules.default.collect-value}") int collectValue) {

        this.countStrategies = countStrategies;
        this.collectStrategies = collectStrategies;

        // Default rules on startup
        Predicate<String> countRule = resolveCountRule(countCondition, countValue);
        Predicate<String> collectRule = resolveCollectRule(collectCondition, collectValue);

        currentEngine = new AtomicReference<>(new FunctionalRuleEngine(countRule,collectRule));

        // Initializing the readable state representing rules
        currentRuleState = new AtomicReference<>(new RuleDto(countCondition, countValue, collectCondition, collectValue));
    }

    public WordProcessingResult process(WordRequestDto requestDto) {
        return currentEngine.get().process(requestDto.words());
    }

    public void updateRules(RuleDto ruleDto) {
        Predicate<String> countRule = resolveCountRule(ruleDto.countCondition(), ruleDto.countValue());
        Predicate<String> collectRule = resolveCollectRule(ruleDto.collectCondition(), ruleDto.collectValue());

        currentEngine.set(new FunctionalRuleEngine(countRule, collectRule));
        currentRuleState.set(ruleDto);
    }

    // --- Fetch the currently active rules
    public RuleDto getCurrentRuleState() {
        return currentRuleState.get();
    }

    private Predicate<String> resolveCountRule(String condition, String value) {
        return countStrategies.stream()
                .filter(strategy -> strategy.supports(condition))
                .findFirst()
                .map(strategy -> strategy.buildPredicate(value))
                .orElseThrow(() -> new InvalidRuleException("Invalid count rule provided: " + condition));
    }

    private Predicate<String> resolveCollectRule(String condition, int value) {
        return collectStrategies.stream()
                .filter(strategy -> strategy.supports(condition))
                .findFirst()
                .map(strategy -> strategy.buildPredicate(value))
                .orElseThrow(() -> new InvalidRuleException("Invalid collect rule provided: " + condition));
    }
}