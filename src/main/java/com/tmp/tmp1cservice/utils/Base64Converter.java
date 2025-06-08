package com.tmp.tmp1cservice.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Converter {

    public static String convertToBase64(String login, String password) {
        String credentials = login + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
