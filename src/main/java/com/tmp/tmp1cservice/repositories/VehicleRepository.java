package com.tmp.tmp1cservice.repositories;

import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto1C;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.models.Client;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface VehicleRepository
{
    Mono<ResponseEntity<String>> getVehiclesFrom1C(Client client);
    Mono<ResponseEntity<String>> getVehicleFrom1C(Client client, String vehicleCode1C);
    Mono<ResponseEntity<String>> createVehicleIn1C(Client client, VehicleDto1C vehicleDto1C);
    Mono<ResponseEntity<String>> updateVehicleIn1C(Client client, String vehicleCode1C, VehicleUpdateDto updateDto);
}