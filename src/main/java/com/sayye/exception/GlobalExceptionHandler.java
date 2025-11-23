package com.sayye.exception;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleCustomException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("handleCustomException: {}", errorCode, e);

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(errorCode.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
        MethodArgumentNotValidException e) {

        Map<String, String> errors = e.getBindingResult() // 검증 결과
            .getFieldErrors() // 필드 에러만 가져옴
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField, // 필드 이름을 key로
                FieldError::getDefaultMessage // 기본 메시지를 value로 (validation에 작성한 메시지)
            ));

        log.warn("Validation failed: {}", errors, e);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
    }
}
