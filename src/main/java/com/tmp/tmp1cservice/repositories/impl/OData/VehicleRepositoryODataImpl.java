package com.tmp.tmp1cservice.repositories.impl.OData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.OData.VehicleRepositoryOData;
import com.tmp.tmp1cservice.utils.UrlHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VehicleRepositoryODataImpl implements VehicleRepositoryOData
{
    private final ObjectMapper objectMapper;

    public Mono<ResponseEntity<String>> getVehiclesFrom1C(Client client)
    {
        String url = UrlHelper.vehiclesUrlOData(client.getUrl1c(), true);
        return UrlHelper.getWebClient(client)
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value()
                                        , String.format(
                                        "Ошибка при получении списка ТС для клиента %s: %s", client.getId(), body))))
                )
                .toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> getVehicleFrom1C(Client client, UUID refKey)
    {
        String url = UrlHelper.vehicleUrlOData(client.getUrl1c(), refKey);
        return UrlHelper.getWebClient(client)
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value()
                                        , String.format(
                                        "Ошибка при получении ТС для клиента %s: %s", client.getId(), body))))
                )
                .toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> createVehicleIn1C(Client client, VehicleDtoOData vehicleDtoOdata)
    {
        String url = UrlHelper.vehiclesUrlOData(client.getUrl1c(), true);
        return UrlHelper.getWebClient(client)
                .post()
                .uri(url)
                .bodyValue(vehicleDtoOdata)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value()
                                        , String.format(
                                        "Ошибка при создании ТС для клиента %s: %s", client.getId(), body))))
                )
                .toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> updateVehicleIn1C(Client client, UUID refKey,
        VehicleUpdateDto updateDto)
    {
        String url = UrlHelper.vehicleUrlOData(client.getUrl1c(), refKey);
        return UrlHelper.getWebClient(client)
                .patch()
                .uri(url)
                .bodyValue(updateDto)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value()
                                        , String.format(
                                        "Ошибка при обновлении ТС %s для клиента %s: %s", refKey, client.getId(), body))))
                )
                .toEntity(String.class);
    }
}