package com.tmp.tmp1cservice.controllers;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.services.TelematicsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/clients/{clientId}/telematicsData")
@Slf4j
@RequiredArgsConstructor
public class TelematicsController
{
    private final TelematicsService telematicsService;

    @PostMapping("{vehicleCode1C}")
    public Mono<ResponseEntity<Map<String, String>>> SendingTelematicsData(@PathVariable UUID clientId, @PathVariable String vehicleCode1C,
                                                                           @Validated @RequestBody TelematicsDataDto telematicsDataDto)
    {
        log.debug("Отправка телематических данных для ТС {} клиента {}", vehicleCode1C,
            clientId);

        return telematicsService.SendingTelematicsData(clientId, vehicleCode1C, telematicsDataDto)
                .thenReturn(ResponseEntity.ok().body(
                        Collections.singletonMap("message", "Данные успешно приняты")
                ))
                .onErrorResume(OneCRequestException.class, Mono::error);
    }
}