package com.wordprocessor.service.engine;

import com.wordprocessor.dto.WordProcessingResult;

import java.util.List;

public interface WordProcessingRuleEngine {

    WordProcessingResult process(List<String> words);
    
}