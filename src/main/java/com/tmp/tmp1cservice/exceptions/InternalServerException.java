package com.tmp.tmp1cservice.exceptions;

import lombok.Getter;

@Getter
public class InternalServerException extends RuntimeException {
    private final int statusCode = 500;
    private final String title = "Внутренняя ошибка сервера";

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getDetail() {
        return getMessage();
    }
}
