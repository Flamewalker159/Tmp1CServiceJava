package com.tmp.tmp1cservice.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final int statusCode = 400;
    private final String title = "Некорректный запрос";

    public BadRequestException(String message) {
        super(message);
    }

    public String getDetail() {
        return getMessage();
    }
}
