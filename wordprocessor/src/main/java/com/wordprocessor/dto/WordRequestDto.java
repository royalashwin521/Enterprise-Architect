package com.wordprocessor.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WordRequestDto(
    @NotEmpty(message = "List cannot be empty")
    List<@NotNull(message = "List must not contain null values") String> words
) {}