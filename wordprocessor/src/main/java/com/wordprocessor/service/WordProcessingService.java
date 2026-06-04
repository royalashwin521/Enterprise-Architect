package com.wordprocessor.service;

import com.wordprocessor.dto.RuleDto;
import com.wordprocessor.dto.WordProcessingResult;
import com.wordprocessor.dto.WordRequestDto;
import com.wordprocessor.exception.InvalidRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Service
public class WordProcessingService {

    private final AtomicReference<WordProcessingRuleEngine> currentEngine;
    private final AtomicReference<RuleDto> currentRuleState;

    public WordProcessingService(@Value("${app.rules.default.count-condition}") String countCondition,
                                 @Value("${app.rules.default.count-value}") String countValue,
                                 @Value("${app.rules.default.collect-condition}") String collectCondition,
                                 @Value("${app.rules.default.collect-value}") int collectValue) {
        // Default rules on startup
        Predicate<String> countRule = buildStringPredicate(countCondition, countValue);
        Predicate<String> collectRule = buildLengthPredicate(collectCondition, collectValue);
        currentEngine = new AtomicReference<>(new FunctionalRuleEngine(countRule,collectRule));

        // Initializing the readable state representing rules
        currentRuleState = new AtomicReference<>(new RuleDto(countCondition, countValue, collectCondition, collectValue));
    }

    public WordProcessingResult process(WordRequestDto requestDto) {
        return currentEngine.get().process(requestDto.words());
    }

    public void updateRules(RuleDto ruleDto) {
        Predicate<String> countRule = buildStringPredicate(ruleDto.countCondition(), ruleDto.countValue());
        Predicate<String> collectRule = buildLengthPredicate(ruleDto.collectCondition(), ruleDto.collectValue());

        currentEngine.set(new FunctionalRuleEngine(countRule, collectRule));
        currentRuleState.set(ruleDto);
    }

    // --- Fetch the currently active rules
    public RuleDto getCurrentRuleState() {
        return currentRuleState.get();
    }

    private Predicate<String> buildStringPredicate(String condition, String value) {
        if (value == null) value = "";
        String lowerValue = value.toLowerCase();
        
        return switch (condition.toUpperCase()) {
            case "STARTS_WITH" -> w -> w.toLowerCase().startsWith(lowerValue);
            case "ENDS_WITH"   -> w -> w.toLowerCase().endsWith(lowerValue);
            case "EQUALS"      -> w -> w.equalsIgnoreCase(lowerValue);
            default            -> throw new InvalidRuleException("Invalid count rule provided: " + condition);
        };
    }

    private Predicate<String> buildLengthPredicate(String condition, int value) {
        return switch (condition.toUpperCase()) {
            case "LENGTH_GREATER_THAN" -> w -> w.length() > value;
            case "LENGTH_EQUALS"       -> w -> w.length() == value;
            case "LENGTH_LESS_THAN"    -> w -> w.length() < value;
            default                    -> throw new InvalidRuleException("Invalid collect rule provided: " + condition);
        };
    }
}