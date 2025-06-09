package com.tmp.tmp1cservice.repositories.impl;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto1C;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.TelematicsRepository;
import com.tmp.tmp1cservice.utils.UrlHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TelematicsRepositoryImpl implements TelematicsRepository {
    public Mono<ResponseEntity<String>> SendingTelematicsData(Client client, String vehicleCode1C, TelematicsDataDto telematicsDataDto) {
        String url = UrlHelper.telematicsDataUrl(client.getUrl1c(), vehicleCode1C);

        return UrlHelper.getWebClient(client)
                .post()
                .uri(url)
                .bodyValue(telematicsDataDto)
                .retrieve()
                .onStatus(statusCode -> !statusCode.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        clientResponse.statusCode().value(),
                                        String.format("Ошибка при отправке телематических данных для ТС %s клиента %s: %s",
                                                vehicleCode1C, client.getId(), body)))))
                .toEntity(String.class);
    }
}