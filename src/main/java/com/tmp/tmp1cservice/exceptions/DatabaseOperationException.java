package com.tmp.tmp1cservice.exceptions;

import lombok.Getter;

@Getter
public class DatabaseOperationException extends RuntimeException {
    private final int statusCode = 500;
    private final String title = "Ошибка базы данных";

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getDetail() {
        return getMessage();
    }
}
