package com.logistics.vurban.lab3.common.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"timestamp","status", "message", "path", "details"})
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
    private Map<String, String> details;

    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.path = path;
        this.details = null;
    }
}