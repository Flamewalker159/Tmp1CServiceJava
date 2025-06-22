package com.tmp.tmp1cservice.services.impl;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.TelematicsRepository;
import com.tmp.tmp1cservice.services.TelematicsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @Override
    public Mono<ResponseEntity<String>> SendingTelematicsData(UUID clientId, String vehicleCode1C, TelematicsDataDto telematicsDataDto) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> telematicsRepository.SendingTelematicsData(client, vehicleCode1C, telematicsDataDto)
                        .flatMap(response -> {
                            String body = response.getBody();
                            if (body == null || body.isEmpty())
                                return Mono.error(new OneCRequestException(400, "Тело ответа пустое"));
                            if (response.getStatusCode() != HttpStatus.OK)
                                return Mono.error(new OneCRequestException(response.getStatusCode().value(),
                                        "Ошибка на стороне 1С"));
                            return Mono.just(response);
                        }));
    }
}