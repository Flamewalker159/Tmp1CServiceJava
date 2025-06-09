package com.tmp.tmp1cservice.services;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TelematicsService
{
    Mono<ResponseEntity<String>> SendingTelematicsData(UUID clientId, String vehicleCode1C, TelematicsDataDto telematicsDataDto);
}