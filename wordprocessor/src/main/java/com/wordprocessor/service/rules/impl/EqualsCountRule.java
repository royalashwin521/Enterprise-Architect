package com.wordprocessor.service.rules.impl;

import com.wordprocessor.service.rules.CountRuleStrategy;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class EqualsCountRule implements CountRuleStrategy {

    @Override
    public boolean supports(String condition) {
        return "EQUALS".equalsIgnoreCase(condition);
    }

    @Override
    public Predicate<String> buildPredicate(String value) {
        String lowerValue = value == null ? "" : value.toLowerCase();
        return w -> w != null && w.equalsIgnoreCase(lowerValue);
    }
}