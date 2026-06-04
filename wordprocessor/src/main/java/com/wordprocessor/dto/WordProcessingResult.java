package com.wordprocessor.dto;

import java.util.List;

/**
 * A perfectly immutable data carrier for our processing results.
 */
public record WordProcessingResult(
    long countMatches, 
    List<String> filteredWords
) {}