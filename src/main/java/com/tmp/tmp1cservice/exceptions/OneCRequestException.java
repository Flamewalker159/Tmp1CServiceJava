package com.tmp.tmp1cservice.exceptions;

import lombok.Getter;

@Getter
public class OneCRequestException extends RuntimeException {
    private final int statusCode;
    private final String title = "Ошибка при запросе к 1С";

    public OneCRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode >= 400 ? statusCode : 502;
    }

    public OneCRequestException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode >= 400 ? statusCode : 502;
    }

    public String getDetail() {
        return getMessage();
    }
}
