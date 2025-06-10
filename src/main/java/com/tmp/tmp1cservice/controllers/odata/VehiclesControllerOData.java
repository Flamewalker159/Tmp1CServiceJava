package com.tmp.tmp1cservice.controllers.odata;

import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleCreateDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.services.OData.VehiclesServiceOData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/clients/{clientId}/vehicles/odata")
@RequiredArgsConstructor
public class VehiclesControllerOData
{
    private final VehiclesServiceOData vehiclesServiceOData;
    @GetMapping
    public Mono<List<VehicleDtoOData>> GetVehiclesFrom1C(@PathVariable UUID clientId)
    {
        return vehiclesServiceOData.getVehiclesFrom1C(clientId);
    }

    @GetMapping("{refKey}")
    public Mono<VehicleDtoOData> GetVehicleFrom1C(@PathVariable UUID clientId,@PathVariable UUID refKey)
    {
        return vehiclesServiceOData.getVehicleFrom1C(clientId, refKey);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VehicleCreateDtoOData> CreateVehicle1C(@PathVariable UUID clientId,@Validated @RequestBody VehicleCreateDtoOData vehicleCreateDtoOData)
    {
        return vehiclesServiceOData.createVehicleIn1C(clientId, vehicleCreateDtoOData);
    }

    @PatchMapping("{refKey}")
    public Mono<VehicleDtoOData> UpdateVehicle1C(@PathVariable UUID clientId,@PathVariable UUID refKey,@RequestBody VehicleUpdateDto vehicleUpdateDto)
    {
        return vehiclesServiceOData.updateVehicle1C(clientId, refKey, vehicleUpdateDto);
    }
}