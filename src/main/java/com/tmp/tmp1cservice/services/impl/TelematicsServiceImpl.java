package com.tmp.tmp1cservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.TelematicsRepository;
import com.tmp.tmp1cservice.services.TelematicsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelematicsServiceImpl implements TelematicsService {
    private final ClientRepository clientRepository;
    private final TelematicsRepository telematicsRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<ResponseEntity<String>> SendingTelematicsData(UUID clientId, String vehicleCode1C, TelematicsDataDto telematicsDataDto) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> telematicsRepository.SendingTelematicsData(client, vehicleCode1C, telematicsDataDto)
                        .flatMap(response -> {
                            String body = response.getBody();
                            if (body == null || body.isEmpty())
                                return Mono.error(new OneCRequestException(400,
                                        String.format("Телематические данные для ТС %s клиента %s отправлены", vehicleCode1C, clientId)));
                            try {
                                var telematics = objectMapper.readValue(body, TelematicsDataDto.class);
                                if (telematics == null)
                                    return Mono.error(new OneCRequestException(500,
                                            String.format("Некорректный ответ от 1С при отправке телематических данных для клиента %s", clientId)));
                                return Mono.just(response);
                            } catch (Exception e) {
                                log.error("Ошибка десериализации при создании ТС: {}", e.getMessage(), e);
                                return Mono.error(new OneCRequestException(500,
                                        "Ошибка при десериализации ответа от 1С: " + e.getMessage()));
                            }
                        }));
    }
}