package com.tmp.tmp1cservice.repositories;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.models.Client;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface TelematicsRepository {
    Mono<ResponseEntity<String>> SendingTelematicsData(Client client, String vehicleCode1C, TelematicsDataDto telematicsDataDto);
}