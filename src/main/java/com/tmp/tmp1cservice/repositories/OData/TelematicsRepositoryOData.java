package com.tmp.tmp1cservice.repositories.OData;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData.TelematicsDataDtoOData;
import com.tmp.tmp1cservice.models.Client;
import reactor.core.publisher.Mono;

public interface TelematicsRepositoryOData
{
    Mono<String> SendingTelematicsData(Client client, TelematicsDataDtoOData telematicsDataDto);
}