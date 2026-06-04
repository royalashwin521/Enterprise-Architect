package com.wordprocessor.service.rules.impl;

import com.wordprocessor.service.rules.CollectRuleStrategy;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class LengthGreaterThanCollectRule implements CollectRuleStrategy {

    @Override
    public boolean supports(String condition) {
        return "LENGTH_GREATER_THAN".equalsIgnoreCase(condition);
    }

    @Override
    public Predicate<String> buildPredicate(int value) {
        return w -> w != null && w.length() > value;
    }
}