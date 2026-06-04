package com.wordprocessor;

import com.wordprocessor.dto.RuleDto;
import com.wordprocessor.dto.WordProcessingResult;
import com.wordprocessor.dto.WordRequestDto;
import com.wordprocessor.service.WordProcessingService;
import com.wordprocessor.service.rules.CollectRuleStrategy;
import com.wordprocessor.service.rules.CountRuleStrategy;
import com.wordprocessor.service.rules.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WordProcessingServiceTest {

    private WordProcessingService service;

    @BeforeEach
    void setUp() {
        List<CountRuleStrategy> countStrategies = List.of(
                new StartsWithCountRule(),
                new EndsWithCountRule(),
                new EqualsCountRule()
        );

        List<CollectRuleStrategy> collectStrategies = List.of(
                new LengthGreaterThanCollectRule(),
                new LengthEqualsCollectRule(),
                new LengthLessThanCollectRule()
        );
        service = new WordProcessingService(countStrategies,collectStrategies,
                "STARTS_WITH", "m",
                "LENGTH_GREATER_THAN", 5);
    }

    @Test
    void shouldReturnCurrentRulesOnGet(){
        RuleDto defaultMockRule = service.getCurrentRuleState();
        assertThat(defaultMockRule.countCondition()).isEqualTo("STARTS_WITH");
        assertThat(defaultMockRule.countValue()).isEqualTo("m");
        assertThat(defaultMockRule.collectCondition()).isEqualTo("LENGTH_GREATER_THAN");
        assertThat(defaultMockRule.collectValue()).isEqualTo(5);
    }

    @Test
    void shouldProcessWordsWithDefaultRules() {
        List<String> words = List.of("Mountain", "apple", "moon");

        WordRequestDto wordRequestDto = new WordRequestDto(words);
        WordProcessingResult result = service.process(wordRequestDto);

        assertThat(result.countMatches()).isEqualTo(2);
        assertThat(result.filteredWords()).containsExactly("Mountain");
    }

    @Test
    void shouldUpdateRulesDynamically() {
        RuleDto newRules = new RuleDto("ENDS_WITH", "e", "LENGTH_EQUALS", 5);
        service.updateRules(newRules);

        List<String> words = List.of("apple", "grape", "orange", "pear");
        WordRequestDto wordRequestDto = new WordRequestDto(words);
        WordProcessingResult result = service.process(wordRequestDto);

        assertThat(result.countMatches()).isEqualTo(3);
        assertThat(result.filteredWords()).containsExactly("apple", "grape");
    }
}