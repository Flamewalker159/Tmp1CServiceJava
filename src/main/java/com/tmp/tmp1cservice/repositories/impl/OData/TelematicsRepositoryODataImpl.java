package com.tmp.tmp1cservice.repositories.impl.OData;

import com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData.TelematicsDataDtoOData;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.OData.TelematicsRepositoryOData;
import com.tmp.tmp1cservice.utils.UrlHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TelematicsRepositoryODataImpl implements TelematicsRepositoryOData {
    public Mono<ResponseEntity<String>> SendingTelematicsData(Client client, TelematicsDataDtoOData telematicsDataDto) {
        String url = UrlHelper.telematicsDataUrlOData(client.getUrl1c());
        return UrlHelper.getWebClient(client)
                .post()
                .uri(url)
                .bodyValue(telematicsDataDto)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value()
                                        , String.format(
                                        "Ошибка при отправке телематических данных для клиента %s: %s", client.getId(), body))))
                )
                .toEntity(String.class);
    }
}