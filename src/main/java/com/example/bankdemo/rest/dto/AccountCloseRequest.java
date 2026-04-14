package com.example.bankdemo.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AccountCloseRequest(
        @NotNull @Positive Long accountId
) {
}
