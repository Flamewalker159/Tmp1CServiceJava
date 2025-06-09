package com.tmp.tmp1cservice.services.OData;

import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface VehiclesServiceOData {
    Mono<List<VehicleDtoOData>> getVehiclesFrom1C(UUID clientId);

    Mono<VehicleDtoOData> getVehicleFrom1C(UUID clientId, UUID refKey);

    Mono<VehicleDtoOData> createVehicleIn1C(UUID clientId, VehicleDtoOData vehicleDto1C);

    Mono<VehicleDtoOData> updateVehicle1C(UUID clientId, UUID refKey, VehicleUpdateDto updateDto);
}