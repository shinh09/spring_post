package com.board.common.dto;

import com.board.common.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        Map<String, String> fieldErrors
) {

    public static ErrorResponse of(
            ErrorCode errorCode,
            String path,
            Map<String, String> fieldErrors
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path,
                fieldErrors
        );
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            String path
    ) {
        return of(errorCode, path, Map.of());
    }
}