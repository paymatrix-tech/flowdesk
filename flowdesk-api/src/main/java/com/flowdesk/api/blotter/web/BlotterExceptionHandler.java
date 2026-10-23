package com.flowdesk.api.blotter.web;

import com.flowdesk.api.blotter.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BlotterExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> notFound(IllegalArgumentException ex) {
        if ("Payment not found".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, ex.getMessage(), null, 404, java.time.Instant.now().toString()));
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null, 400, java.time.Instant.now().toString()));
    }
}
