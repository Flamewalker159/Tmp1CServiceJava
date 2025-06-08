package com.tmp.tmp1cservice.utils;

import com.tmp.tmp1cservice.models.Client;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

public class UrlHelper {

    private static final String ODATA_URL = "odata/standard.odata";
    private static final String FORMAT_JSON = "format=json";

    private static WebClient WebClientWithAuth(Client client) {
        String credentials = Base64Converter.convertToBase64(client.getLogin(), client.getPassword());
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public static WebClient getWebClient(Client client)
    {
        return WebClientWithAuth(client);
    }

    public static String employeesUrl(String url1C) {
        return url1C + "/hs/employees";
    }

    public static String employeeUrl(String url1C, String empCode) {
        return url1C + "/hs/employees/code/" + empCode;
    }

    public static String vehiclesUrl(String url1C) {
        return url1C + "/hs/vehicles";
    }

    public static String vehicleUrl(String url1C, String vehicleCode1C) {
        return url1C + "/hs/vehicles/" + vehicleCode1C;
    }

    public static String telematicsDataUrl(String url1C, String vehicleCode1C) {
        return url1C + "/hs/telematics/" + vehicleCode1C;
    }

    public static String vehiclesUrlOData(String url1C, boolean formatJson) {
        return formatJson
                ? url1C + "/" + ODATA_URL + "/Catalog_ТранспортныеСредства?$" + FORMAT_JSON
                : url1C + "/" + ODATA_URL + "/Catalog_ТранспортныеСредства";
    }

    public static String vehicleUrlOData(String url1C, UUID refKey) {
        return url1C + "/" + ODATA_URL + "/Catalog_ТранспортныеСредства(guid'" + refKey + "')?$" + FORMAT_JSON;
    }

    public static String telematicsDataUrlOData(String url1C) {
        return url1C + "/" + ODATA_URL + "/InformationRegister_ТелематическиеДанные?$" + FORMAT_JSON;
    }
}
