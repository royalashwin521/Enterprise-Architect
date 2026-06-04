package com.wordprocessor.service;

import com.wordprocessor.dto.WordProcessingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FunctionalRuleEngine implements WordProcessingRuleEngine {

    private final Predicate<String> countRule;
    private final Predicate<String> collectRule;

    public FunctionalRuleEngine(Predicate<String> countRule, Predicate<String> collectRule) {
        this.countRule = countRule;
        this.collectRule = collectRule;
    }

    @Override
    public WordProcessingResult process(List<String> words) {
        long count = 0;
        List<String> collected = new ArrayList<>();

        for (String word : words) {
            if (countRule.test(word)) { 
                count++;
            }
            if (collectRule.test(word)) {
                collected.add(word);
            }
        }
        return new WordProcessingResult(count, collected);
    }
}