package com.tmp.tmp1cservice.controllers;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.services.TelematicsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.xml.stream.events.StartElement;
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
    public Mono<ResponseEntity<Map<String, String>>> SendingTelematicsData(UUID clientId, String vehicleCode1C, TelematicsDataDto telematicsDataDto)
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