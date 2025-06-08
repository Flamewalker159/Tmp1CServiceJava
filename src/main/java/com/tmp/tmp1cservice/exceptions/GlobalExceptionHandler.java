package com.tmp.tmp1cservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Object> handleClientNotFound(ClientNotFoundException ex, HttpServletRequest request) {
        return buildResponse(ex.getStatusCode(), ex.getTitle(), ex.getDetail(), request.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(ex.getStatusCode(), ex.getTitle(), ex.getDetail(), request.getRequestURI());
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<Object> handleDatabase(DatabaseOperationException ex, HttpServletRequest request) {
        return buildResponse(ex.getStatusCode(), ex.getTitle(), ex.getDetail(), request.getRequestURI());
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Object> handleInternal(InternalServerException ex, HttpServletRequest request) {
        return buildResponse(ex.getStatusCode(), ex.getTitle(), ex.getDetail(), request.getRequestURI());
    }

    @ExceptionHandler(OneCRequestException.class)
    public ResponseEntity<Object> handleOneC(OneCRequestException ex, HttpServletRequest request) {
        return buildResponse(ex.getStatusCode(), ex.getTitle(), ex.getDetail(), request.getRequestURI());
    }


    // Обработка всех прочих необработанных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknown(Exception ex, HttpServletRequest request) {
        return buildResponse(
                500,
                "Неизвестная ошибка",
                "Произошла необработанная ошибка: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    private ResponseEntity<Object> buildResponse(int status, String title, String detail, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("title", title);
        body.put("detail", detail);
        body.put("instance", path);

        return new ResponseEntity<>(body, HttpStatus.valueOf(status));
    }
}