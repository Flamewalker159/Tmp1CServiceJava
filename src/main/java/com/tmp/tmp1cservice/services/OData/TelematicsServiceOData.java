package com.tmp.tmp1cservice.services.OData;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData.TelematicsDataDtoOData;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TelematicsServiceOData
{
    Mono<TelematicsDataDtoOData> sendingTelematicsData(UUID clientId, TelematicsDataDtoOData telematicsDataDto);
}