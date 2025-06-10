package com.tmp.tmp1cservice.repositories.OData;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData.TelematicsDataDtoOData;
import com.tmp.tmp1cservice.models.Client;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface TelematicsRepositoryOData
{
    Mono<ResponseEntity<String>> SendingTelematicsData(Client client, TelematicsDataDtoOData telematicsDataDto);
}