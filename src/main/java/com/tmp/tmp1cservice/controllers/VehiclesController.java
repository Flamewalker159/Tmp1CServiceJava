package com.tmp.tmp1cservice.controllers;

import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto1C;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.services.VehiclesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/clients/{clientId}/vehicles")
@Slf4j
@RequiredArgsConstructor
public class VehiclesController
{
    private final VehiclesService vehiclesService;

    @GetMapping
    public Mono<List<VehicleDto>> GetVehiclesFrom1C(@PathVariable UUID clientId)
    {
        log.debug("Получение списка ТС для клиента {}", clientId);
        return vehiclesService.getVehiclesFrom1C(clientId);
    }

    @GetMapping("/{vehicleCode}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<VehicleDto> GetVehicleFrom1C(@PathVariable UUID clientId, @PathVariable String vehicleCode)
    {
        return vehiclesService.getVehicleFrom1C(clientId, vehicleCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VehicleDto> CreateVehicle1C(@PathVariable UUID clientId,@Validated @RequestBody VehicleDto1C vehicleDto1C)
    {
        return vehiclesService.createVehicleIn1C(clientId, vehicleDto1C);
    }

    @PutMapping("/{vehicleCode1C}")
    public Mono<ResponseEntity<Map<String, String>>> UpdateVehicle1C(@PathVariable UUID clientId, @PathVariable String vehicleCode1C, @RequestBody VehicleUpdateDto vehicleUpdateDto)
    {
        return vehiclesService.updateVehicleIn1C(clientId, vehicleCode1C, vehicleUpdateDto)
                .thenReturn(ResponseEntity.ok().body(
                        Collections.singletonMap("message", "Данные ТС успешно обновлены")
                ))
                .onErrorResume(OneCRequestException.class, ex -> {
                    log.error("Ошибка при обновлении ТС с кодом {} для клиента {}: {}",
                            vehicleCode1C, clientId, ex.getMessage());
                    return Mono.error(ex);
                });
    }
}