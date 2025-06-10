package com.tmp.tmp1cservice.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto1C;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.VehicleRepository;
import com.tmp.tmp1cservice.services.VehiclesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehiclesServiceImpl implements VehiclesService {
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
            //.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            //.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            //.setTimeZone(TimeZone.getTimeZone("UTC"));

    @Override
    public Mono<List<VehicleDto>> getVehiclesFrom1C(UUID clientId) {
        log.debug("Получение списка ТС для клиента {}", clientId);

        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepository.getVehiclesFrom1C(client)
                        .flatMap(stringResponseEntity -> {
                            String body = stringResponseEntity.getBody();
                            log.debug("Полученный ответ от 1С: {}", body);
                            try {
                                List<VehicleDto> vehicles = objectMapper
                                        .readValue(body, new TypeReference<>() {
                                        });
                                if (vehicles == null || vehicles.isEmpty()) {
                                    log.error("ТС для клиента {} не получены", clientId);
                                    return Mono.error(new OneCRequestException(400,
                                            "Некорректный ответ от 1С при получении ТС"));
                                }
                                log.info("Получены {} ТС для клиента {}", vehicles.size(), clientId);
                                return Mono.just(vehicles);
                            } catch (Exception ex) {
                                log.error("Ошибка десериализации: {}", ex.getMessage(), ex);
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при обработке ответа от 1С: " + ex.getMessage()));
                            }
                        }));
    }

    public Mono<VehicleDto> getVehicleFrom1C(UUID clientId, String vehicleCode1C) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepository.getVehicleFrom1C(client, vehicleCode1C)
                        .flatMap(stringResponseEntity -> {
                            String body = stringResponseEntity.getBody();
                            try {
                                VehicleDto vehicle = objectMapper.readValue(body, new TypeReference<>() {
                                });
                                if (vehicle == null) {
                                    log.error("ТС для клиента {} не получены", clientId);
                                    return Mono.error(new OneCRequestException(400,
                                            "Некорректный ответ от 1С при получении ТС"));
                                }
                                log.info("ТС {} для клиента {} успешно получено", vehicleCode1C, clientId);
                                return Mono.just(vehicle);
                            } catch (Exception ex) {
                                log.error("Ошибка десериализации: {}", ex.getMessage(), ex);
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при обработке ответа от 1С: " + ex.getMessage()));
                            }
                        }));
    }

    @Override
    public Mono<VehicleDto> createVehicleIn1C(UUID clientId, VehicleDto1C vehicleDto1C) {
        log.debug("Создание ТС для клиента {}", clientId);

        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepository.createVehicleIn1C(client, vehicleDto1C)
                        .flatMap(response -> {
                            String body = response.getBody();
                            if (body == null || body.isBlank()) {
                                log.error("Пустой ответ от 1С при создании ТС для клиента {}", clientId);
                                return Mono.error(new OneCRequestException(500,
                                        String.format("Некорректный ответ от 1С при создании ТС для клиента %s", clientId)));
                            }
                            try {
                                VehicleDto vehicle = objectMapper.readValue(body, VehicleDto.class);
                                if (vehicle == null) {
                                    log.error("ТС не создано для клиента {}", clientId);
                                    return Mono.error(new OneCRequestException(500,
                                            String.format("Некорректный ответ от 1С при создании ТС для клиента %s", clientId)));
                                }
                                log.info("ТС успешно создано для клиента {}", clientId);
                                return Mono.just(vehicle);
                            } catch (Exception e) {
                                log.error("Ошибка десериализации при создании ТС: {}", e.getMessage(), e);
                                return Mono.error(new OneCRequestException(500,
                                        "Ошибка при десериализации ответа от 1С: " + e.getMessage()));
                            }
                        }));
    }

    @Override
    public Mono<Void> updateVehicleIn1C(UUID clientId, String vehicleCode1C, VehicleUpdateDto updateDto) {
        log.debug("Обновление ТС {} для клиента {}", vehicleCode1C, clientId);

        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepository.updateVehicleIn1C(client, vehicleCode1C, updateDto)
                        .doOnSuccess(response ->
                                log.info("ТС {} для клиента {} отправлено на обновление", vehicleCode1C, clientId))
                        .then());
    }
}
