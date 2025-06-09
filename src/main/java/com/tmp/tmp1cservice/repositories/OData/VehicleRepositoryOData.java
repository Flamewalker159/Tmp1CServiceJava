package com.tmp.tmp1cservice.repositories.OData;

import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.models.Client;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VehicleRepositoryOData {
    Mono<ResponseEntity<String>> getVehiclesFrom1C(Client client);

    Mono<ResponseEntity<String>> getVehicleFrom1C(Client client, UUID refKey);

    Mono<ResponseEntity<String>> createVehicleIn1C(Client client, VehicleDtoOData odataVehicleDto);

    Mono<ResponseEntity<String>> updateVehicleIn1C(Client client, UUID refKey,
                                                   VehicleUpdateDto updateDto);
}