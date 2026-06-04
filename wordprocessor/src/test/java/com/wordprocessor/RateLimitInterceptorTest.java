package com.wordprocessor;

import com.wordprocessor.interceptors.RateLimitInterceptor;
import com.wordprocessor.service.RateLimitService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitInterceptorTest {

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private Bucket bucket;

    @InjectMocks
    private RateLimitInterceptor rateLimitInterceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldReturnTrueWhenTokensAreAvailable() throws Exception {
        String testIp = "192.168.1.50";
        request.setRemoteAddr(testIp);

        when(rateLimitService.resolveBucket(testIp)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        verify(rateLimitService, times(1)).resolveBucket(testIp);
        verify(bucket, times(1)).tryConsume(1);
    }

    @Test
    void shouldReturnFalseAnd429WhenTokensAreExhausted() throws Exception {
        String testIp = "10.0.0.99";
        request.setRemoteAddr(testIp);

        // Define the Mockito rules for the Blocked Path
        when(rateLimitService.resolveBucket(testIp)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(false);

        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(response.getContentAsString()).isEqualTo("Too many requests. Please try again in a minute.");
    }
}