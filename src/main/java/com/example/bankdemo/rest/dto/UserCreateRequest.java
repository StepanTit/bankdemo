package com.example.bankdemo.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank String login
) {
}
