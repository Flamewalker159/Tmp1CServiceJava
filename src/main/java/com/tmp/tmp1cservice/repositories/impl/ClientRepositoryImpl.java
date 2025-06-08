package com.tmp.tmp1cservice.repositories.impl;


import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.ClientRepositoryCustom;
import com.tmp.tmp1cservice.utils.Base64Converter;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private final WebClient webClient;

    public ClientRepositoryImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public boolean testConnection(ClientDto dto) {
        String credentials = Base64Converter.convertToBase64(dto.getLogin(), dto.getPassword());

        try {
            webClient.get()
                    .uri(dto.getUrl1C())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка соединения: " + e.getMessage(), e);
        }
    }
}
