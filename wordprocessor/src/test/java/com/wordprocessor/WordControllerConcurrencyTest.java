package com.wordprocessor;

import com.wordprocessor.dto.RuleDto;
import com.wordprocessor.dto.WordRequestDto;
import com.wordprocessor.interceptors.RateLimitInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordControllerConcurrencyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RateLimitInterceptor rateLimitInterceptor;

    // Bypassing the rate limiting to perform load test
    @BeforeEach
    void setUp() throws Exception {
        when(rateLimitInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void shouldHandleConcurrentRuleUpdatesWithoutCrashing() throws InterruptedException {
        int numberOfClients = 50;
        
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfClients);
        CountDownLatch startingGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(numberOfClients);

        List<Future<ResponseEntity<String>>> responses = new ArrayList<>();

        for (int i = 0; i < numberOfClients; i++) {
            RuleDto payload = (i % 2 == 0) ? 
                new RuleDto("ENDS_WITH", "z", "LENGTH_EQUALS", 3) : 
                new RuleDto("STARTS_WITH", "m", "LENGTH_GREATER_THAN", 5);

            HttpEntity<RuleDto> request = new HttpEntity<>(payload);

            responses.add(executorService.submit(() -> {
                startingGun.await();
                
                ResponseEntity<String> response = restTemplate.exchange(
                        "/api/v1/words/rules",
                        HttpMethod.PUT,
                        request,
                        String.class
                );
                
                finishLine.countDown();
                return response;
            }));
        }

        // FIRE! This releases all 50 threads at the exact same millisecond
        startingGun.countDown();
        
        // Wait for all 50 threads to complete their requests
        finishLine.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // Assert that EVERY request returned a 200 OK and nothing crashed
        for (Future<ResponseEntity<String>> futureResponse : responses) {
            try {
                ResponseEntity<String> response = futureResponse.get();
                assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                assertThat(response.getBody()).contains("Success");
            } catch (ExecutionException e) {
                throw new RuntimeException("Thread failed during execution", e);
            }
        }
    }

}