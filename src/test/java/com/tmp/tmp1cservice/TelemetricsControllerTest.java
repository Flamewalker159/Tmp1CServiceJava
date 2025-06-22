package com.tmp.tmp1cservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.TelematicsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TelemetricsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TelematicsRepository telematicsRepository;

    @MockitoBean
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Client testClient;
    private TelematicsDataDto validDto;
    private TelematicsDataDto invalidDto;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String API_KEY = "qweasd";
    private static final String VEHICLE_CODE = "000000001";

    @BeforeEach
    void setUp() throws IOException {
        testClient = new Client();
        testClient.setId(UUID.randomUUID());
        testClient.setLogin("TestClient");
        testClient.setPassword("123");
        testClient.setUrl1c("http://localhost/1CTest");

        validDto = objectMapper.readValue(
                Files.readString(new ClassPathResource("testData/TelematicsData.json").getFile().toPath()),
                TelematicsDataDto.class
        );
        invalidDto = objectMapper.readValue(
                Files.readString(new ClassPathResource("testData/InvalidTelematicsData.json").getFile().toPath()),
                TelematicsDataDto.class
        );

        when(clientRepository.findById(testClient.getId()))
                .thenReturn(Mono.just(testClient));
    }

    @Nested
    class Return200 {
        @Test
        void testSendTelematicsData() {
            when(telematicsRepository.SendingTelematicsData(
                    eq(testClient),
                    eq(VEHICLE_CODE),
                    any(TelematicsDataDto.class))
            ).thenReturn(Mono.just(ResponseEntity.ok("Данные успешно записаны")));

            webTestClient.post()
                    .uri("/api/clients/{clientId}/telematicsData/{vehicleCode}", testClient.getId(), VEHICLE_CODE)
                    .header(API_KEY_HEADER, API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validDto)
                    .exchange()
                    .expectStatus().isOk();

            verify(telematicsRepository, times(1))
                    .SendingTelematicsData(eq(testClient), eq(VEHICLE_CODE), any(TelematicsDataDto.class));
        }
    }

    @Nested
    class Return400 {
        @Test
        void whenDataInvalid_thenReturns400() {
            when(telematicsRepository.SendingTelematicsData(
                    eq(testClient),
                    eq(VEHICLE_CODE),
                    any(TelematicsDataDto.class))
            ).thenReturn(Mono.just(ResponseEntity.badRequest().body("Ошибка: Некорректные данные")));

            webTestClient.post()
                    .uri("/api/clients/{clientId}/telematicsData/{vehicleCode}", testClient.getId(), VEHICLE_CODE)
                    .header(API_KEY_HEADER, API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidDto)
                    .exchange()
                    .expectStatus().isBadRequest();

            verify(telematicsRepository, times(1))
                    .SendingTelematicsData(eq(testClient), eq(VEHICLE_CODE), any());
        }
    }

    @Nested
    @DisplayName("return 401")
    class Return401 {
        @Test
        void whenNoApiKey_thenReturns401() throws JsonProcessingException {
            // Без header API_KEY
            webTestClient.post()
                    .uri("/api/clients/{clientId}/telematicsData/{vehicleCode}", testClient.getId(), VEHICLE_CODE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(validDto))
                    .exchange()
                    .expectStatus().isUnauthorized();

            verifyNoInteractions(telematicsRepository);
        }
    }

    @Nested
    @DisplayName("return 404")
    class Return404 {
        @Test
        void whenClientNotFound_thenReturns404() throws JsonProcessingException {
            when(clientRepository.findById(any(UUID.class)))
                    .thenReturn(Mono.empty());

            webTestClient.post()
                    .uri("/api/clients/{clientId}/telematicsData/{vehicleCode}", UUID.randomUUID(), VEHICLE_CODE)
                    .header(API_KEY_HEADER, API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(validDto))
                    .exchange()
                    .expectStatus().isNotFound();

            verifyNoInteractions(telematicsRepository);
        }
    }
}