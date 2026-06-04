package com.wordprocessor.service.rules.impl;

import com.wordprocessor.service.rules.CountRuleStrategy;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class EndsWithCountRule implements CountRuleStrategy {

    @Override
    public boolean supports(String condition) {
        return "ENDS_WITH".equalsIgnoreCase(condition);
    }

    @Override
    public Predicate<String> buildPredicate(String value) {
        String lowerValue = value == null ? "" : value.toLowerCase();
        return w -> w != null && w.toLowerCase().endsWith(lowerValue);
    }
}