package com.examples.springbootmonolithicstarter.global.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String code;
    private String message;
    private List<FieldError> errors;

    private ErrorResponse(int status, String code, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.code = code;
        this.message = message;
        this.errors = new ArrayList<>();
    }

    public static ErrorResponse of(int status, String code, String message) {
        return new ErrorResponse(status, code, message);
    }

    public void addFieldError(String field, String message) {
        this.errors.add(new FieldError(field, message));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldError {
        private String field;
        private String message;

        private FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
