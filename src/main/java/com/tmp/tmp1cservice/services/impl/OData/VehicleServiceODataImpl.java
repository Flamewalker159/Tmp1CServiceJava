package com.tmp.tmp1cservice.services.impl.OData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleCreateDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleResponseOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.OData.VehicleRepositoryOData;
import com.tmp.tmp1cservice.services.OData.VehiclesServiceOData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceODataImpl implements VehiclesServiceOData {
    private final VehicleRepositoryOData vehicleRepositoryOData;
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public Mono<List<VehicleDtoOData>> getVehiclesFrom1C(UUID clientId) {
        return Mono.justOrEmpty(clientRepository.findById(clientId))
                .switchIfEmpty(Mono.error(new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepositoryOData.getVehiclesFrom1C(client)
                        .flatMap(response -> {
                            String body = response.getBody();
                            try {
                                VehicleResponseOData vehicleResponse = objectMapper.readValue(body, VehicleResponseOData.class);
                                List<VehicleDtoOData> vehicles = vehicleResponse.getVehicles();

                                if (vehicles == null || vehicles.isEmpty()) {
                                    return Mono.error(new OneCRequestException(400,
                                            "Некорректный ответ от 1С при получении списка ТС"));
                                }
                                return Mono.just(vehicles);
                            } catch (Exception ex) {
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при обработке ответа от 1С: " + ex.getMessage()));
                            }
                        }));
    }

    public Mono<VehicleDtoOData> getVehicleFrom1C(UUID clientId, UUID refKey) {
        return Mono.justOrEmpty(clientRepository.findById(clientId))
                .switchIfEmpty(Mono.error(new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepositoryOData.getVehicleFrom1C(client, refKey)
                        .flatMap(stringResponseEntity -> {
                            String body = stringResponseEntity.getBody();
                            try {
                                VehicleDtoOData vehicle = objectMapper.readValue(body, new TypeReference<>() {
                                });
                                if (vehicle == null) {
                                    return Mono.error(new OneCRequestException(400,
                                            "Некорректный ответ от 1С при получении ТС"));
                                }
                                return Mono.just(vehicle);
                            } catch (Exception ex) {
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при обработке ответа от 1С: " + ex.getMessage()));
                            }
                        }));
    }

    public Mono<VehicleCreateDtoOData> createVehicleIn1C(UUID clientId, VehicleCreateDtoOData vehicleCreateDtoOData) {
        return Mono.justOrEmpty(clientRepository.findById(clientId))
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepositoryOData.createVehicleIn1C(client, vehicleCreateDtoOData)
                        .flatMap(response -> {
                            String body = response.getBody();
                            if (body == null || body.isBlank()) {
                                return Mono.error(new OneCRequestException(400,
                                        String.format("Некорректный ответ от 1С при создании ТС для клиента %s", clientId)));
                            }
                            try {
                                VehicleCreateDtoOData vehicle = objectMapper.readValue(body, VehicleCreateDtoOData.class);
                                if (vehicle == null) {
                                    return Mono.error(new OneCRequestException(400,
                                            String.format("Некорректный ответ от 1С при создании ТС для клиента %s", clientId)));
                                }
                                return Mono.just(vehicle);
                            } catch (Exception e) {
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при десериализации ответа от 1С: " + e.getMessage()));
                            }
                        }));
    }

    public Mono<VehicleDtoOData> updateVehicle1C(UUID clientId, UUID refKey, VehicleUpdateDto updateDto) {
        return Mono.justOrEmpty(clientRepository.findById(clientId))
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepositoryOData.updateVehicleIn1C(client, refKey, updateDto)
                        .flatMap(response -> {
                            String body = response.getBody();
                            if (body == null || body.isBlank()) {
                                return Mono.error(new OneCRequestException(400,
                                        String.format("Некорректный ответ от 1С при обновлении ТС для клиента %s", clientId)));
                            }
                            try {
                                VehicleDtoOData vehicle = objectMapper.readValue(body, VehicleDtoOData.class);
                                if (vehicle == null) {
                                    return Mono.error(new OneCRequestException(400,
                                            String.format("Некорректный ответ от 1С для клиента %s", clientId)));
                                }
                                return Mono.just(vehicle);
                            } catch (Exception e) {
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при десериализации ответа от 1С: " + e.getMessage()));
                            }
                        }));
    }
}
