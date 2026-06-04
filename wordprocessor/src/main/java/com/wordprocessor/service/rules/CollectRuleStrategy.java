package com.wordprocessor.service.rules;

import java.util.function.Predicate;

public interface CollectRuleStrategy {
    boolean supports(String condition);
    Predicate<String> buildPredicate(int value);
}