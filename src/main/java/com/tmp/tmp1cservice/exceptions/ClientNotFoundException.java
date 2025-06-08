package com.tmp.tmp1cservice.exceptions;

import lombok.Getter;

@Getter
public class ClientNotFoundException extends RuntimeException
{
    private final int statusCode = 404;
    private final String title = "Клиент не найден";

    public ClientNotFoundException(String message) {
        super(message);
    }

    public String getDetail() {
        return getMessage();
    }
}