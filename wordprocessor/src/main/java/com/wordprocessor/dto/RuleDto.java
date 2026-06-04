package com.wordprocessor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RuleDto(
        @NotNull(message = "countCondition cannot be empty")
        String countCondition,   // e.g., "STARTS_WITH", "ENDS_WITH"

        @NotNull(message = "countValue cannot be empty")
        String countValue,

        @NotNull(message = "collectCondition cannot be empty")
        String collectCondition, // e.g., "LENGTH_GREATER_THAN", "LENGTH_EQUALS"

        @PositiveOrZero(message = "collectValue must be 0 or greater")
        int collectValue
) {}