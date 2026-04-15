package com.example.bankdemo.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountAmountOperationRequest(
        @NotNull @Positive BigDecimal amount
) {
}
