package com.learning.tasklist.domain.dto;

public record ErrorResponse(
        int status,
        String message,
        String details
) {
}
