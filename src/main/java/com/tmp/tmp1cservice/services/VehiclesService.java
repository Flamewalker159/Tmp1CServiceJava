package com.tmp.tmp1cservice.services;

import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto1C;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface VehiclesService
{
    Mono<List<VehicleDto>> getVehiclesFrom1C(UUID clientId);
    Mono<VehicleDto> getVehicleFrom1C(UUID clientId, String vehicleCode1C);
    Mono<VehicleDto> createVehicleIn1C(UUID clientId, VehicleDto1C vehicleDto1C);
    Mono<Void> updateVehicleIn1C(UUID clientId, String vehicleCode1C, VehicleUpdateDto updateDto);
}