package com.wordprocessor;

import com.wordprocessor.dto.RuleDto;
import com.wordprocessor.dto.WordProcessingResult;
import com.wordprocessor.interceptors.RateLimitInterceptor;
import com.wordprocessor.service.WordProcessingService;
import com.wordprocessor.transport.WordController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
class WordControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordProcessingService service;

    @MockBean
    private RateLimitInterceptor rateLimitInterceptor;

    // Bypassing the rate limiting to check controller validations
    @BeforeEach
    void setUp() throws Exception {
        when(rateLimitInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void shouldReturnCurrentRulesOnGet() throws Exception {
        RuleDto mockRules = new RuleDto("STARTS_WITH", "A", "LENGTH_GREATER_THAN", 5);
        when(service.getCurrentRuleState()).thenReturn(mockRules);

        mockMvc.perform(get("/api/v1/words/rules")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countCondition").value("STARTS_WITH"))
                .andExpect(jsonPath("$.countValue").value("A"))
                .andExpect(jsonPath("$.collectCondition").value("LENGTH_GREATER_THAN"))
                .andExpect(jsonPath("$.collectValue").value(5));
    }

    @Test
    void shouldProcessWordsSuccessfullyOnPost() throws Exception {
        WordProcessingResult mockResult = new WordProcessingResult(1, List.of("Apple"));

        when(service.process(any())).thenReturn(mockResult);

        String validJson = """
                {
                  "words": ["Apple", "Banana", "Cherry"]
                }
                """;

        mockMvc.perform(post("/api/v1/words/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countMatches").value(1))
                .andExpect(jsonPath("$.filteredWords[0]").value("Apple"));
    }

    @Test
    void shouldReturn400WhenListContainsNull() throws Exception {
        String jsonBody = """
                {
                  "words": ["apple", null, "banana"]
                }
                """;

        mockMvc.perform(post("/api/v1/words/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenListIsEmpty() throws Exception {
        String jsonBody = """
                {
                  "words": []
                }
                """;

        mockMvc.perform(post("/api/v1/words/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

}