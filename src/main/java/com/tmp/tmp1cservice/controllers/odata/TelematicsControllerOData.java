package com.tmp.tmp1cservice.controllers.odata;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData.TelematicsDataDtoOData;
import com.tmp.tmp1cservice.services.OData.TelematicsServiceOData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/clients/{clientId}/telematicsData/odata")
@RequiredArgsConstructor
public class TelematicsControllerOData
{
    private final TelematicsServiceOData telematicsServiceOData;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TelematicsDataDtoOData> SendingTelematicsData(@PathVariable UUID clientId,@RequestBody TelematicsDataDtoOData telematicsDataDtoOData)
    {
        return telematicsServiceOData.sendingTelematicsData(clientId, telematicsDataDtoOData);
    }
}