package com.wordprocessor.service.rules;

import java.util.function.Predicate;

public interface CountRuleStrategy {
    boolean supports(String condition);
    Predicate<String> buildPredicate(String value);
}